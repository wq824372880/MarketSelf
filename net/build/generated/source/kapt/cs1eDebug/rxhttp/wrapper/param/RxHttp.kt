package rxhttp.wrapper.`param`

import com.zeekrlife.net.api.ResponseParser
import kotlin.Any
import kotlin.Deprecated
import kotlin.Int
import kotlin.Unit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import rxhttp.onEachProgress
import rxhttp.toFlow
import rxhttp.toFlowProgress
import rxhttp.toParser
import rxhttp.wrapper.BodyParamFactory
import rxhttp.wrapper.CallFactory
import rxhttp.wrapper.entity.Progress
import rxhttp.wrapper.parse.SimpleParser
import rxhttp.wrapper.utils.javaTypeOf

public inline fun <reified T> RxHttp<*, *>.executeList() = executeClass<List<T>>()

public inline fun <reified T> RxHttp<*, *>.executeClass() =
    execute(SimpleParser<T>(javaTypeOf<T>()))

/**
 * 调用此方法监听上传进度                                                    
 * @param coroutine  CoroutineScope对象，用于开启协程回调进度，进度回调所在线程取决于协程所在线程
 * @param progress 进度回调  
 *
 *
 * 此方法已废弃，请使用Flow监听上传进度，性能更优，且更简单，如：
 *
 * ```
 * RxHttp.postForm("/server/...")
 *     .addFile("file", File("xxx/1.png"))
 *     .toFlow<T> {   //这里也可选择你解析器对应的toFlowXxx方法
 *         val currentProgress = it.progress //当前进度 0-100
 *         val currentSize = it.currentSize  //当前已上传的字节大小
 *         val totalSize = it.totalSize      //要上传的总字节大小    
 *     }.catch {
 *         //异常回调
 *     }.collect {
 *         //成功回调
 *     }
 * ```                   
 */
@Deprecated(message = "please use 'toFlow(progressCallback)' instead", 
level = DeprecationLevel.ERROR)
public fun <P : AbstractBodyParam<P>, R : RxHttpAbstractBodyParam<P, R>>
    RxHttpAbstractBodyParam<P, R>.upload(coroutine: CoroutineScope,
    progressCallback: suspend (Progress) -> Unit): R {
  param.setProgressCallback { progress, currentSize, totalSize ->
      coroutine.launch { progressCallback(Progress(progress, currentSize, totalSize)) }
  }
  @Suppress("UNCHECKED_CAST")
  return this as R
}

public inline fun <reified T : Any> CallFactory.toResponse() =
    toParser(ResponseParser<T>(javaTypeOf<T>()))

public inline fun <reified T : Any> CallFactory.toFlowResponse() = toFlow(toResponse<T>())

public inline fun <reified T : Any> BodyParamFactory.toFlowResponse(capacity: Int = 1, noinline
    progress: suspend (Progress) -> Unit) = 
    toFlowProgress(toResponse<T>(), capacity)
        .onEachProgress(progress)

public inline fun <reified T : Any> BodyParamFactory.toFlowResponseProgress(capacity: Int = 1) =
    toFlowProgress(toResponse<T>(), capacity)
