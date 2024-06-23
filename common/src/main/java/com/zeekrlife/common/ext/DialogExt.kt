package com.zeekrlife.common.ext

import android.app.Dialog
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.zeekrlife.common.R

/*****************************************loading框********************************************/
private var loadingDialog: Dialog? = null

/**
 * 打开等待框
 */
fun AppCompatActivity.showLoadingExt(message: String = "请求网络中...") {
    dismissLoadingExt()
    if (!this.isFinishing) {
        var tips : TextView? = null
        if (loadingDialog == null) {
            //弹出loading时 把当前界面的输入法关闭
            this.hideOffKeyboard()
            val loadingView = LayoutInflater.from(this@showLoadingExt)
                .inflate(R.layout.layout_zeekr_loading_view, null).apply {
                    tips = this.findViewById(R.id.loading_zeekr_tips)
                }
            loadingDialog = Dialog(this, R.style.loadingDialogTheme).apply {
                setCancelable(true)
                setCanceledOnTouchOutside(false)
                setContentView(loadingView)
            }
            loadingDialog?.setOnDismissListener {
                //设置dialog关闭时 置空 不然会出现 一个隐藏bug 这里就不细说了
                dismissLoadingExt()
            }
        }
        tips?.text = message
        loadingDialog?.show()
    }
}

/**
 * 打开等待框
 */
fun Fragment.showLoadingExt(message: String = "加载中...") {
    dismissLoadingExt()
    activity?.let {
        if (!it.isFinishing) {
            if (loadingDialog == null) {
                //弹出loading时 把当前界面的输入法关闭
                it.hideOffKeyboard()
                loadingDialog = Dialog(requireActivity(), R.style.loadingDialogTheme).apply {
                    setCancelable(true)
                    setCanceledOnTouchOutside(false)
                    setContentView(
                        LayoutInflater.from(it)
                            .inflate(R.layout.layout_zeekr_loading_view, null).apply {
                                this.findViewById<TextView>(R.id.loading_zeekr_tips).text = message
                            })
                }
                loadingDialog?.setOnDismissListener {
                    //设置dialog关闭时 置空 不然会出现 一个隐藏bug 这里就不细说了
                    dismissLoadingExt()
                }
            }
            loadingDialog?.show()
        }
    }
}

/**
 * 关闭等待框
 */
fun AppCompatActivity.dismissLoadingExt() {
    loadingDialog?.dismiss()
    loadingDialog = null
}

/**
 * 关闭等待框
 */
fun Fragment.dismissLoadingExt() {
    loadingDialog?.dismiss()
    loadingDialog = null
}


