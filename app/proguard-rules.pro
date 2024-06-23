
# 代码混淆压缩比，在0和7之间，默认为5，一般不需要改
-optimizationpasses 5

# 混淆时不使用大小写混合，混淆后的类名为小写，Windows用户必须指定，否则当你的项目中有超过26个类的
#话，ProGuard就会默认混用大小写文件名，而导致class文件相互覆盖。
-dontusemixedcaseclassnames

# 指定不去忽略非公共的库和类，不要跳过对非公开类的处理，默认情况下是跳过的
-dontskipnonpubliclibraryclasses

# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的4个步骤之一
# Android不需要preverify，去掉这一步可加快混淆速度
-dontpreverify

# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后使用printmapping指定映射文件的名称
#-verbose
#-printmapping proguardMapping.txt

# 指定混淆时采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 保护代码中的Annotation不被混淆，这在JSON实体映射时非常重要，比如fastJson
-keepattributes *Annotation*,InnerClasses

# 避免混淆泛型，这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature

#抛出异常时保留代码行号，在异常分析中可以方便定位
-keepattributes SourceFile,LineNumberTable

### ecarx SDK 引用的Android 隐藏 API 部分 ===== 开始
-dontwarn android.os.**
-keep class android.os.**{*;}

-dontwarn android.provider.**
-keep class android.provider.**{*;}

-dontwarn android.util.Slog
-keep class android.util.Slog{*;}

-dontwarn android.view.**
-keep class android.view.**{*;}

-dontwarn android.location.**
-keep class android.location.**{*;}

-dontwarn com.android.internal.**
-keep class com.android.internal.**{*;}

-dontwarn android.app.**
-keep class android.app.**{*;}

-dontwarn android.media.**
-keep class android.media.**{*;}

-dontwarn android.net.wifi.**
-keep class android.net.wifi.**{*;}
### ecarx SDK 引用的Android 隐藏 API 部分 ===== 结束

-dontwarn ecarx.sysconfig**
-keep class ecarx.sysconfig.**{*;}

#Ecarx
-keep class com.ecarx.sdk.** { *; }
-dontwarn com.ecarx.sdk.**
-keep class com.ecarx.utils.** { *; }
-dontwarn com.ecarx.utils.**
-keep class com.ecarx.xui.adaptapi.** { *; }
-dontwarn com.ecarx.xui.adaptapi.**
-keep class com.ecarx.widget.** { *; }
-dontwarn com.ecarx.widget.**
-keep class com.ecarx.tip.** { *; }
-dontwarn com.ecarx.tip.**
-keep class com.ecarx.openapihelper.** { *; }
-dontwarn com.ecarx.openapihelper.**
-keep class com.ecarx.backanimlib.** { *; }
-dontwarn com.ecarx.backanimlib.**
-keep class com.ecarx.support.** { *; }
-dontwarn com.ecarx.support.**
-keep class com.ecarx.xui.adaptapi.** { *; }
-dontwarn com.ecarx.xui.adaptapi.**
-keep class com.ecarx.pushsdk.** { *; }
-dontwarn com.ecarx.pushsdk.**
-keep class com.ecarx.compat.** { *; }
-dontwarn com.ecarx.compat.**

-keep class com.google.** { *; }
-dontwarn com.google.**

-keep class com.scwang.smartrefresh.layout.** { *; }
-dontwarn com.scwang.smartrefresh.layout.**
-keep class com.scwang.smartrefresh.horizontal.** { *; }
-dontwarn com.scwang.smartrefresh.horizontal.**

#神策埋点
-keep class com.ecarx.dataprovidersdk.** { *; }
-dontwarn com.ecarx.dataprovidersdk.**
-keep class com.sensorsdata.analytics.android.sdk.** { *; }
-dontwarn com.sensorsdata.analytics.android.sdk.**

-keep class * extends androidx.fragment.app.Fragment { *; }

-keep class com.ecarx.appstore.task.IArrangeCallback** { *; }
-keep class com.ecarx.appstore.task.ITaskCallback** { *; }
-keep class com.ecarx.appstore.task.ITaskInfo** { *; }
-keep class com.ecarx.appstore.task.ITaskService** { *; }

# split screen
-keep class ecarx.splitscreen.**{*;}

# AppStore
-keep class com.ecarx.remote.base.** {*;}
-keep class com.ecarx.remote.appstore.** {*;}
-keep class com.ecarx.retrofit.server.** {*;}
-keep class com.ecarx.appstore.task.base.bean.** {*;}
-keep class com.ecarx.appstore.task.ITaskInfo {*;}
#########ecarx SDK 引用的Android 隐藏 API 部分 ===== 结束############

################common###############

 #实体类不参与混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepnames class * implements java.io.Serializable
-keepattributes Signature
-keep class **.R$* {*;}
-ignorewarnings
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclasseswithmembernames class * { # 保持native方法不被混淆
    native <methods>;
}

-keepclassmembers enum * {  # 使用enum类型时需要注意避免以下两个方法混淆，因为enum类的特殊性，以下两个方法会被反射调用，
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

################support###############
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-dontwarn android.support.**

-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**

################glide###############
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

# 保留自定义控件(继承自View)不能被混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(***);
    *** get* ();
}
-dontwarn com.kingja.loadsir.**
-keep class com.kingja.loadsir.** {*;}


-keep class **.*_SnakeProxy
-keep @com.youngfeng.snake.annotations.EnableDragToClose public class *



# DataBinding ViewBinding 反射混淆
################ ViewBinding & DataBinding ###############
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
  public static * inflate(android.view.LayoutInflater);
  public static * inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
  public static * bind(android.view.View);
}
#所有GSON生成的对象类不能被混淆
-keep class com.zeekrlife.market.data.response.**{*;}
-keep class com.zeekrlife.market.data.request.**{*;}

-keep class com.zeekrlife.market.app.**{*;}
-keep class com.zeekrlife.market.data.**{*;}
-keep class com.zeekrlife.net.**{*;}
-keep class com.socks.library.**{*;}
-keep class com.zeekrlife.market.net.**{*;}
#埋点SDK
-keep class com.zeekr.sdk.**{*;}
-keep class * implements android.os.Parcelable{*;}
-keep interface * extends android.os.IInterface{*;}
-keep class * extends android.os.Binder{*;}
-keep class com.sensorsdata.analytics.android.** { *; }
#支付宝卡片调用so文件不能混淆
-keep class com.antfin.cube.** { *; }
-keep class com.alipay.wasm.** { *; }
