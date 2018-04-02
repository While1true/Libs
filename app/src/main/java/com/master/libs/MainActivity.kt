package com.master.libs

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

import java.io.File

class MainActivity : AppCompatActivity(),AsyncIn{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tv = findViewById<TextView>(R.id.tv)
        Async {
            NetUtils.download("http://sw.bos.baidu.com/sw-search-sp/software/d4e97ccd4bd9f/jdk-8u144-windows-i586_8.0.1440.1.exe", File(filesDir,"123.exe"),object :NetUtils.ProgressListener{
                override fun call(current: Long, total: Long, speed: Int, index: String) {
                    UI {
                        tv.text = (String.format("%.2f", speed / 1024f) + "Mb/s" + "文件名: " + index + "----" + current * 100 / total + "%")
                    }

                }
            })
        }
    }

    fun go(v: View) {
        startActivity(Intent(this, this.javaClass))
    }

    override fun onDestroy() {
        super.onDestroy()
        removeAllCallBack()
    }
}
