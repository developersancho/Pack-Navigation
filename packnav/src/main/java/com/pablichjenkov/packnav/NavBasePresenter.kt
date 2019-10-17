package com.pablichjenkov.packnav

import android.os.Handler
import android.os.Looper
import androidx.annotation.CallSuper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel


abstract class NavBasePresenter<T> {

    protected val scope = CoroutineScope(Dispatchers.IO)

    protected var isActive = false

    private val handler = Handler(Looper.getMainLooper())

    private var listener: ((event: T) -> Unit)? = null


    fun subscribe(
        listener: ((event: T) -> Unit)
    ) {

        this.listener = listener

        this.isActive = true

    }

    abstract fun startWithInput(inputMessage: NavigationMessage.Input?)

    abstract fun startFromResult(resultMessage: NavigationMessage.Result)

    @CallSuper
    open fun unsubscribe() {

        isActive = false

        scope.cancel()

        listener = null
    }

    protected fun dispatch(event: T) = handler.post { listener?.invoke(event) }

}
