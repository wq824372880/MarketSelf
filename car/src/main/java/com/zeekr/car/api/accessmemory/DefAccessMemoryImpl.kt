package com.zeekr.car.api.accessmemory

import android.util.Log
import com.zeekr.car.util.SystemProperties

class DefAccessMemoryImpl : AccessMemory {

    override fun AccessMemorySize(): String {

        try {
            val memorySize = SystemProperties.get("persist.zeekr.device.total_memory_size", "32")
            return if (memorySize.contains("24") ) {
                "24"
            }else{
                "32"
            }
        } catch (e: Exception) {
            Log.e("AccessMemorySize", "get persist.zeekr.device.total_memory_size exception:${Log.getStackTraceString(e)}")
        }
        return "32"
    }

}