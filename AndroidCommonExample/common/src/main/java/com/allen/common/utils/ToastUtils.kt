package com.allen.common.utils

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Handler
import android.os.Looper
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v4.widget.TextViewCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import java.lang.ref.WeakReference

/**
 * Create By hHui on 2017/09/10
 *
 * [Toast] 相关工具类
 */
class ToastUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {
        private val DEFAULT_COLOR = 0xFEFFFFFF.toInt()
        private val HANDLER = Handler(Looper.getMainLooper())
        private var sToast: Toast? = null
        private var sViewWeakReference: WeakReference<View>? = null
        private var sLayoutId = -1
        private var gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        private var xOffset = 0
        private var yOffset = (64 * Utils.getApp().resources.displayMetrics.density + 0.5).toInt()
        private var bgColor = DEFAULT_COLOR
        private var bgResource = -1
        private var msgColor = DEFAULT_COLOR
        /**
         * 设置吐司位置

         * @param gravity 位置
         * *
         * @param xOffset x偏移
         * *
         * @param yOffset y偏移
         */
        fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
            ToastUtils.gravity = gravity
            ToastUtils.xOffset = xOffset
            ToastUtils.yOffset = yOffset
        }

        /**
         * 设置背景颜色

         * @param backgroundColor 背景色
         */
        fun setBackgroundColor(@ColorInt backgroundColor: Int) {
            ToastUtils.bgColor = backgroundColor
        }

        /**
         * 设置背景资源

         * @param bgResource 背景资源
         */
        fun setBabkgroundResource(@DrawableRes bgResource: Int) {
            ToastUtils.bgResource = bgResource
        }

        /**
         * 设置消息颜色

         * @param msgColor 颜色
         */
        fun setMessageColor(@ColorInt msgColor: Int) {
            ToastUtils.msgColor = msgColor
        }

        /**
         * 安全地显示短时吐司

         * @param text 文本
         */
        fun showShortText(text: CharSequence) = show(text, Toast.LENGTH_SHORT)

        /**
         * 安全地显示短时吐司

         * @param resId 资源Id
         */
        fun showShortRes(@StringRes resId: Int) = show(resId, Toast.LENGTH_SHORT)

        /**
         * 安全地显示短时吐司

         * @param resId 资源Id
         * *
         * @param args  参数
         */
        fun showShortResWithArgs(@StringRes resId: Int, vararg args: Any) = show(resId, Toast.LENGTH_SHORT, *args)

        /**
         * 安全地显示短时吐司

         * @param format 格式
         * *
         * @param args   参数
         */
        fun showShort(format: String, vararg args: Any) = show(format, Toast.LENGTH_SHORT, *args)

        /**
         * 安全地显示长时吐司

         * @param text 文本
         */
        fun showLongText(text: CharSequence) = show(text, Toast.LENGTH_LONG)

        /**
         * 安全地显示长时吐司

         * @param resId 资源Id
         */
        fun showLongRes(@StringRes resId: Int) = show(resId, Toast.LENGTH_LONG)

        /**
         * 安全地显示长时吐司

         * @param resId 资源Id
         * *
         * @param args  参数
         */
        fun showLongWithArgs(@StringRes resId: Int, vararg args: Any) = show(resId, Toast.LENGTH_LONG, *args)

        /**
         * 安全地显示长时吐司

         * @param format 格式
         * *
         * @param args   参数
         */
        fun showLong(format: String, vararg args: Any) = show(format, Toast.LENGTH_LONG, *args)

        /**
         * 安全地显示短时自定义吐司(注：最外层layout不能设置background，否则不能改变 [Toast] 的宽高)
         */
        fun showCustomShort(@LayoutRes layoutId: Int): View {
            val view = getView(layoutId)
            show(view, Toast.LENGTH_SHORT)
            return view
        }

        /**
         * 安全地显示长时自定义吐司(注：最外层layout不能设置background，否则不能改变 [Toast] 的宽高)
         */
        fun showCustomLong(@LayoutRes layoutId: Int): View {
            val view = getView(layoutId)
            show(view, Toast.LENGTH_LONG)
            return view
        }

        /**
         * 取消吐司显示
         */
        fun cancel() {
            if (sToast != null) {
                sToast!!.cancel()
                sToast = null
            }
        }

        private fun show(@StringRes resId: Int, duration: Int) = show(Utils.getApp().resources.getText(resId).toString(), duration)

        private fun show(@StringRes resId: Int, duration: Int, vararg args: Any) = show(String.format(Utils.getApp().resources.getString(resId), *args), duration)

        private fun show(format: String, duration: Int, vararg args: Any) {
            show(String.format(format, *args), duration)
        }

        private fun show(text: CharSequence, duration: Int) {
            HANDLER.post {
                cancel()
                sToast = Toast.makeText(Utils.getApp(), text, duration)
                // solve the font of toast
                val tvMessage = sToast!!.view.findViewById(android.R.id.message) as TextView
                TextViewCompat.setTextAppearance(tvMessage, android.R.style.TextAppearance)
                tvMessage.setTextColor(msgColor)
                setBgAndGravity()
                sToast!!.show()
            }
        }

        private fun show(view: View, duration: Int) {
            HANDLER.post {
                cancel()
                sToast = Toast(Utils.getApp())
                sToast!!.view = view
                sToast!!.duration = duration
                setBgAndGravity()
                sToast!!.show()
            }
        }

        private fun setBgAndGravity() {
            val toastView = sToast!!.view
            if (bgResource != -1) {
                toastView.setBackgroundResource(bgResource)
            } else if (bgColor != DEFAULT_COLOR) {
                val background = toastView.background
                background.colorFilter = PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_IN)
            }
            sToast!!.setGravity(gravity, xOffset, yOffset)
        }

        private fun getView(@LayoutRes layoutId: Int): View {
            if (sLayoutId == layoutId) {
                if (sViewWeakReference != null) {
                    val toastView = sViewWeakReference!!.get()
                    if (toastView != null) return toastView
                }
            }
            val inflate = Utils.getApp().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val toastView = inflate.inflate(layoutId, null)
            sViewWeakReference = WeakReference(toastView)
            sLayoutId = layoutId
            return toastView
        }
    }
}
