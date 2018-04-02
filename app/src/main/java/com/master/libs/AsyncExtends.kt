package com.master.libs

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.os.Handler
import android.os.Looper

/**
 * Created by 不听话的好孩子 on 2018/4/2.
 */
val handler = Handler(Looper.getMainLooper())
var threads = linkedMapOf<Any, ArrayList<Thread>>()
var runs = linkedMapOf<Any, ArrayList<Any>>()

fun AsyncIn.UI(block: () -> Unit) {
    if (Thread.currentThread() == Looper.getMainLooper().thread) {
        block()
    } else {
        handler.post(block)
    }
    var list = runs[hashCode()]
    if (list == null) {
        list = arrayListOf()
        list.add(block)
        runs[hashCode()] = list
    } else {
        list.add(block)
    }
}

fun AsyncIn.removeAllCallBack() {
    val arrayList = runs[hashCode()]
    if (arrayList != null) {
        for (a in arrayList) {
            handler.removeCallbacks { a }
        }
        arrayList.clear()
    }
    var listThreads = threads[hashCode()]
    if (listThreads != null) {
        for (thread in listThreads) {
            try {
                thread.interrupt()
            } catch (e: Exception) {
            }
        }
        listThreads.clear()
    }

}

fun AsyncIn.Async(block: () -> Unit) {
    if (Thread.currentThread() != Looper.getMainLooper().thread) {
        block()
    } else {
        val thread = Thread(block)
        var threadx = threads[hashCode()]
        if (threadx == null) {
            threadx = arrayListOf()
            threadx.add(thread)
            threads[hashCode()] = threadx
        } else {
            threadx.add(thread)
        }
        thread.start()
    }
}

interface AsyncIn {
    fun onDestroy()
//    fun getTraget(): Any
}