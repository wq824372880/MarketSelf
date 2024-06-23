package com.zeekrlife.market.task.utils;

import android.os.IBinder;

import com.zeekrlife.common.ext.CommExtKt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class OverlayHelper {
    /**
     * 获取指定目标包名的覆盖信息列表。
     * <p>此方法通过反射调用系统服务，查询与指定包名相关的所有覆盖信息（Overlay Info）。</p>
     *
     * @param packageName 目标包名，用于查询与其相关的覆盖信息。
     * @return 返回一个包含所有查询到的覆盖信息的字符串列表。如果发生异常，则返回null。
     */
    public static List<String> getOverlayInfosForTarget(String packageName) {
        List<String> list = new ArrayList<String>();
        try {
            // 加载OverlayInfo类，此类用于表示覆盖信息
            Class<?> overlayInfoClass = Class.forName("android.content.om.OverlayInfo");
            Field fPckageName = overlayInfoClass.getField("packageName");

            // 加载ServiceManager类，用于获取系统服务
            Class<?> cServiceManager = Class.forName("android.os.ServiceManager");
            Method mGetService = cServiceManager.getMethod("getService", String.class);
            // 获取OverlayManager服务
            Object overlayManagerService = ((Method) mGetService).invoke(null, "overlay");
            // 加载IOverlayManager$Stub类，用于与OverlayManager服务进行远程交互
            Class<?> cIOverlayManagerStub = Class.forName("android.content.om.IOverlayManager$Stub");
            Method mAsInterface = cIOverlayManagerStub.getMethod("asInterface", IBinder.class);
            // 将OverlayManager服务绑定为接口
            Object overlayManager = mAsInterface.invoke(null, overlayManagerService);
            // 调用获取指定目标包名的覆盖信息列表的方法
            Method mGetOverlayInfosForTarget = cIOverlayManagerStub.getMethod("getOverlayInfosForTarget", String.class, int.class);
            List<Object> infoList = (List<Object>) mGetOverlayInfosForTarget.invoke(overlayManager, packageName, 0);
            // 遍历列表，提取每个覆盖信息的包名并添加到结果列表中
            for (int i = 0; i < infoList.size(); i++) {
                Object infoObject = infoList.get(i);
                String pkg_name = (String) fPckageName.get(infoObject);
                list.add(pkg_name);
            }
            return list;
        } catch (Exception ex) {
            CommExtKt.logStackTrace(ex); // 记录异常堆栈轨迹
            return null;
        }
    }

    /**
     * 获取指定包名的覆盖信息。
     * 此方法通过反射调用系统服务，查询与指定包名关联的覆盖信息（如主题覆盖）。
     *
     * @param packageName 需要查询覆盖信息的包名。
     * @return 如果找到对应的覆盖信息，则返回该信息的包名；如果未找到或发生异常，则返回null。
     */
    public static String getOverlayInfo(String packageName) {
        try {
            // 加载android.content.om.OverlayInfo类，用于后续获取覆盖信息的结构
            Class<?> overlayInfoClass = Class.forName("android.content.om.OverlayInfo");
            // 获取OverlayInfo类中的targetPackageName字段
            Field fTargetPckageName = overlayInfoClass.getField("targetPackageName");

            // 加载android.os.ServiceManager类，用于获取系统服务
            Class<?> cServiceManager = Class.forName("android.os.ServiceManager");
            // 获取getService方法，用于从ServiceManager中获取指定的服务
            Method mGetService = cServiceManager.getMethod("getService", String.class);
            // 调用getService方法获取overlay服务
            Object overlayManagerService = ((Method) mGetService).invoke(null, "overlay");
            // 加载android.content.om.IOverlayManager$Stub类，用于与overlay服务进行交互
            Class<?> cIOverlayManagerStub = Class.forName("android.content.om.IOverlayManager$Stub");
            // 获取asInterface方法，用于创建IOverlayManager的代理对象
            Method mAsInterface = cIOverlayManagerStub.getMethod("asInterface", IBinder.class);
            // 调用asInterface方法，传入overlay服务的Binder，创建代理对象
            Object overlayManager = mAsInterface.invoke(null, overlayManagerService);
            // 获取getOverlayInfosForTarget方法，用于查询指定包名的覆盖信息
            Method mGetOverlayInfosForTarget = cIOverlayManagerStub.getMethod("getOverlayInfo", String.class, int.class);
            // 调用getOverlayInfosForTarget方法，查询指定包名的覆盖信息
            Object infoObject = mGetOverlayInfosForTarget.invoke(overlayManager, packageName, 0);
            // 从返回的覆盖信息对象中获取targetPackageName字段的值
            String pkg_name = (String) fTargetPckageName.get(infoObject);
            return pkg_name;
        } catch (Exception ex) {
            // 捕获反射调用可能引发的异常，并进行日志记录
            CommExtKt.logStackTrace(ex);
            return null;
        }
    }

    /**
     * 设置指定包名的Overlay是否启用。
     *
     * @param packageName 要操作的Overlay的包名。
     * @param enable 指定是否启用Overlay。如果为true，则启用；如果为false，则禁用。
     * @return 如果操作成功，则返回true；如果操作失败，则返回false。
     */
    public static boolean setEnable(String packageName, boolean enable) {
        try {
            // 加载android.content.om.OverlayInfo类，用于后续操作Overlay信息。
            Class<?> overlayInfoClass = Class.forName("android.content.om.OverlayInfo");
            Method mIsEnabled = overlayInfoClass.getMethod("isEnabled");

            // 通过ServiceManager获取OverlayManager服务。
            Class<?> cServiceManager = Class.forName("android.os.ServiceManager");
            Method mGetService = cServiceManager.getMethod("getService", String.class);
            Object overlayManagerService = ((Method) mGetService).invoke(null, "overlay");

            // 获取IOverlayManager的Stub接口，以便于远程调用OverlayManager服务。
            Class<?> cIOverlayManagerStub = Class.forName("android.content.om.IOverlayManager$Stub");
            Method mAsInterface = cIOverlayManagerStub.getMethod("asInterface", IBinder.class);
            Object overlayManager = mAsInterface.invoke(null, overlayManagerService);

            // 调用OverlayManager服务的方法，获取指定包名的Overlay信息。
            Method mGetOverlayInfosForTarget = cIOverlayManagerStub.getMethod("getOverlayInfo", String.class, int.class);
            Object infoObject = mGetOverlayInfosForTarget.invoke(overlayManager, packageName, 0);

            // 检查当前Overlay是否已启用。
            boolean isEnabled = (boolean) mIsEnabled.invoke(infoObject);

            // 如果需要启用而当前未启用，则调用接口启用该Overlay。
            if (enable && !isEnabled) {
                Method mSetEnable = cIOverlayManagerStub.getMethod("setEnabled", String.class, boolean.class, int.class);
                mSetEnable.invoke(overlayManager, packageName, enable, 0);
            }
            return true;
        } catch (Exception ex) {
            CommExtKt.logStackTrace(ex);
            return false;
        }
    }
}
