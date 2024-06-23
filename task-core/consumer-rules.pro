-keep class com.zeekrlife.market.task.bean.** {*;}
-keep class com.zeekrlife.market.task.data.** {*;}
-keep class com.liulishuo.okdownload.** {*;}

# ------- because of we using okhttp on sample ----
# https://github.com/square/okhttp/#proguard
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# ------- end okhttp proguard rules ----

# ------- because of we using com.liulishuo.okdownload:okhttp on sample ----
-keepnames class com.liulishuo.okdownload.core.connection.DownloadOkHttp3Connection
# ------- end com.liulishuo.okdownload:okhttp proguard rules ----

# ------- because of we using com.liulishuo.okdownload:sqlite on sample ----
-keep class com.liulishuo.okdownload.core.breakpoint.BreakpointStoreOnSQLite {
        public com.liulishuo.okdownload.core.breakpoint.DownloadStore createRemitSelf();
        public com.liulishuo.okdownload.core.breakpoint.BreakpointStoreOnSQLite(android.content.Context);
}
# ------- end com.liulishuo.okdownload:sqlite proguard rules ----

# don't warn SuppressFBWarnings annotation used in com.liulishuo.okdownload.DownloadContex.java
-dontwarn edu.umd.cs.findbugs.annotations.SuppressFBWarnings

-dontwarn kotlinx.coroutines.**