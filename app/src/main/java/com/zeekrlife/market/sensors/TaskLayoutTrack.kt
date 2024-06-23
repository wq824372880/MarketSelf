package com.zeekrlife.market.sensors

import com.zeekrlife.common.ext.logStackTrace
import com.zeekrlife.market.data.response.AppItemInfoBean
import com.zeekrlife.task.base.constant.TaskState
import com.zeekrlife.task.base.widget.TaskLayout

/**
 * @author Lei.Chen29
 * @date 2022/6/6 10:53
 * description：TaskLayout的埋点扩展
 */
/**
 * 用于追踪分类列表、精品推荐列表 中 Item TaskLayout的下载事件
 */
fun TaskLayout.trackDownload(item: AppItemInfoBean) {
    val viewActionClickListener = onViewActionClickListener
    setOnViewActionClickListener { taskInfo, taskState ->
        try {
            val fromPage = TrackActivityLifecycleCallbacks.getFromPage()
            when (taskInfo.state) {
                TaskState.DOWNLOADABLE -> {
                    SensorsTrack.onAppDownload(fromPage, item)
                }
                TaskState.UPDATABLE -> {
                    SensorsTrack.onAppUpdate(fromPage,item.apkName ?: "", 1, 3)
                }
            }
        } catch (e: NullPointerException) {
            // 处理空指针异常
            e.logStackTrace()
        } catch (e: ClassCastException) {
            // 处理类型转换异常
            e.logStackTrace()
        } catch (e: IllegalStateException) {
            // 处理非法状态异常
            e.logStackTrace()
        } catch (e: Exception) {
            // 处理其他未知异常
            e.logStackTrace()
        }
        return@setOnViewActionClickListener viewActionClickListener?.onViewActionClick(taskInfo, taskState) ?: false
    }
}