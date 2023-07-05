package com.zrh.log

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import java.io.File
import kotlin.system.exitProcess

/**
 *
 * @author zrh
 * @date 2023/7/5
 *
 */
class App : Application(), ActivityLifecycleCallbacks {

    override fun onCreate() {
        super.onCreate()
        Logger.addPrinter(LogcatPrinter(true))
        Logger.addPrinter(DiskPrinter(File(cacheDir, "logs")).apply {
            // 目录下的文件总大小设置为20kb
            setMaxFileLength(4*1024)
            setMaxFileCount(5)
        })

        registerActivityLifecycleCallbacks(this)

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            Logger.getPrinter(DiskPrinter::class.java).printCrash("Logger", e)
            exitProcess(0)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Logger.i("Logger", "onActivityCreated:" + activity.localClassName)
    }

    override fun onActivityStarted(activity: Activity) {
        Logger.i("Logger", "onActivityStarted:" + activity.localClassName)
    }

    override fun onActivityResumed(activity: Activity) {
        Logger.i("Logger", "onActivityResumed:" + activity.localClassName)
    }

    override fun onActivityPaused(activity: Activity) {
        Logger.i("Logger", "onActivityPaused:" + activity.localClassName)
    }

    override fun onActivityStopped(activity: Activity) {
        Logger.i("Logger", "onActivityStopped:" + activity.localClassName)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Logger.i("Logger", "onActivitySaveInstanceState:" + activity.localClassName)
    }

    override fun onActivityDestroyed(activity: Activity) {
        Logger.i("Logger", "onActivityDestroyed:" + activity.localClassName)
    }
}