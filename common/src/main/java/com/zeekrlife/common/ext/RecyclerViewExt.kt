package com.zeekrlife.common.ext

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zeekrlife.common.util.decoration.DefaultDecoration
import com.zeekrlife.common.util.decoration.builder.XDividerOrientation
import com.zeekrlife.common.util.decoration.builder.XGridBuilder
import com.zeekrlife.common.util.decoration.builder.XLinearBuilder
import com.zeekrlife.common.util.decoration.builder.XStaggeredGridBuilder


/**
 * 纵向recyclerview
 * @receiver RecyclerView
 * @param baseAdapter BaseQuickAdapter<*, *>
 * @return RecyclerView
 */
fun RecyclerView.vertical(): RecyclerView {
    layoutManager = LinearLayoutManager(this.context)
    setHasFixedSize(true)
    return this
}

/**
 * 横向 recyclerview
 * @receiver RecyclerView
 * @return RecyclerView
 */
fun RecyclerView.horizontal(): RecyclerView {
    layoutManager = LinearLayoutManager(this.context).apply {
        orientation = RecyclerView.HORIZONTAL
    }
    setHasFixedSize(true)
    return this
}

/**
 * grid recyclerview
 * @receiver RecyclerView
 * @return RecyclerView
 */
fun RecyclerView.grid(count: Int): RecyclerView {
    layoutManager = GridLayoutManager(this.context, count)
    setHasFixedSize(true)
    return this
}

/**
 * 配置万能分割线
 * @receiver RecyclerView
 * @param block [@kotlin.ExtensionFunctionType] Function1<DefaultDecoration, Unit>
 * @return RecyclerView
 */
fun RecyclerView.divider(block: DefaultDecoration.() -> Unit): RecyclerView {
    val itemDecoration = DefaultDecoration(context).apply(block)
    addItemDecoration(itemDecoration)
    return this
}

/**
 * 配置万能分割线
 * @receiver RecyclerView
 * @param block [@kotlin.ExtensionFunctionType] Function1<DefaultDecoration, Unit>
 * @return RecyclerView
 *  positiveAction: () -> Unit = {},
 */
fun RecyclerView.divider2(orientation:XDividerOrientation = XDividerOrientation.Linear, block: XLinearBuilder.() -> Unit={}, blockGrid: XGridBuilder.() -> Unit= {}, blockStagger: XStaggeredGridBuilder.() -> Unit= {}): RecyclerView {
    val itemDecoration:RecyclerView.ItemDecoration = when(orientation){
        XDividerOrientation.Linear -> {XLinearBuilder(context).apply(block).build()}
        XDividerOrientation.GRID -> {XGridBuilder(context).apply(blockGrid).build()}
        XDividerOrientation.STAGGERED -> {XStaggeredGridBuilder(context).apply(blockStagger).build()}
    }
    addItemDecoration(itemDecoration)
    return this
}

