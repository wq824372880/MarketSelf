package com.zeekrlife.market.utils;

import android.annotation.SuppressLint;
import android.os.Build;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public  class HookWebView {
/**
 * 钩子函数，用于hook WebView的实现，以便在不改变应用代码的情况下，自定义WebView的行为。
 * 此方法主要用于在不同Android版本上动态修改WebView的实现类。
 * 无参数和返回值。
 */
public static void hookWebView() {
    int sdkInt = Build.VERSION.SDK_INT;
    try {
        // 加载android.webkit.WebViewFactory类，这是WebView创建的核心工厂类
        Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
        // 获取并设置WebViewFactory类的sProviderInstance字段的访问权限
        Field field = factoryClass.getDeclaredField("sProviderInstance");
        field.setAccessible(true);
        // 尝试获取当前的sProviderInstance对象，如果已存在，则无需进一步操作
        Object sProviderInstance = field.get(null);
        if (sProviderInstance != null) {
            return;
        }

        // 根据Android版本动态获取getProviderClass方法
        Method getProviderClassMethod;
        if (sdkInt > 22) {
            getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
        } else if (sdkInt == 22) {
            getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
        } else {
            return;
        }
        // 设置方法可访问
        getProviderClassMethod.setAccessible(true);
        // 调用getProviderClass方法，获取实际的WebView工厂提供者类
        Class<?> factoryProviderClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
        // 加载WebViewDelegate类，这是WebView内部的核心代理类
        Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
        // 获取WebViewDelegate类的无参构造函数，并设置可访问
        Constructor<?> delegateConstructor = delegateClass.getDeclaredConstructor();
        delegateConstructor.setAccessible(true);

        if (sdkInt < 26) { // 针对Android O以下版本的WebView实现替换
            // 获取工厂提供者类的构造函数，并实例化
            Constructor<?> providerConstructor = factoryProviderClass.getConstructor(delegateClass);
            if (providerConstructor != null) {
                providerConstructor.setAccessible(true);
                sProviderInstance = providerConstructor.newInstance(delegateConstructor.newInstance());
            }
        } else { // 针对Android O及以后版本的WebView实现替换
            // 获取并设置WebViewFactory类中定义的CHROMIUM_WEBVIEW_FACTORY_METHOD字段的访问权限
            Field chromiumMethodName = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD");
            chromiumMethodName.setAccessible(true);
            // 获取chromiumMethodName字段的值，用于调用静态工厂方法
            String chromiumMethodNameStr = (String)chromiumMethodName.get(null);
            if (chromiumMethodNameStr == null) {
                chromiumMethodNameStr = "create";
            }
            // 获取并调用相应的静态工厂方法
            Method staticFactory = factoryProviderClass.getMethod(chromiumMethodNameStr, delegateClass);
            if (staticFactory!=null){
                sProviderInstance = staticFactory.invoke(null, delegateConstructor.newInstance());
            }
        }

        // 如果成功实例化了新的sProviderInstance对象，则设置回WebViewFactory的sProviderInstance字段
        if (sProviderInstance != null){
            field.set("sProviderInstance", sProviderInstance);
        } else {
            // hook失败处理
        }
    } catch (Throwable e) {
        // 捕获并处理可能的异常，避免影响应用的正常运行
    }
}

}
