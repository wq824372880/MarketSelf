package com.zeekrlife.market.sensors

import android.app.Activity
import android.os.Bundle

open class ActivityLifecycleTrack {

    open fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    open fun onActivityStarted(activity: Activity) {}

    open fun onActivityResumed(activity: Activity) {}

    open fun onActivityPaused(activity: Activity) {}

    open fun onActivityStopped(activity: Activity) {}

    open fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    open fun onActivityDestroyed(activity: Activity) {}
}