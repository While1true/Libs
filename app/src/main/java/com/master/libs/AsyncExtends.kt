package com.master.libs

import android.os.Handler
import android.os.Looper

/**
 * Created by 不听话的好孩子 on 2018/4/2.
 */
val handler = Handler(Looper.getMainLooper())
var list = mutableListOf<Thread>()

fun Any?.UI(block: () -> Unit) {
    if (Thread.currentThread() == Looper.getMainLooper().thread) {
        block()
    } else {
        handler.post(block)
    }
}

fun removeAllCallBack() {
    handler.removeCallbacksAndMessages(null)
    for (thread in list) {
        try {
            thread.interrupt()
        } catch (e: Exception) {
        }
    }
    list.clear()
}

fun Any?.Async(block: () -> Unit) {
    if (Thread.currentThread() != Looper.getMainLooper().thread) {
        block()
    } else {
        val thread = Thread(block)
        list.add(thread)
        thread.start()
    }

}