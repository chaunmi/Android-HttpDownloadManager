package com.chaunmi.downloader

import android.annotation.SuppressLint
import android.content.Context

/**
 * 使用前必须init
 * 注意为全局单例持有context
 */
object DownloadManagerDefaultImp {
    @SuppressLint("StaticFieldLeak")
    private lateinit var downloadManager: DownloadManager


    fun getDownloadManager(): DownloadManager {
        if(!::downloadManager.isInitialized) {
            throw Exception("DownloadManager has not init, please call init first!!!")
        }
        return downloadManager
    }

    fun init(context: Context, log: Logger? = null) {
        downloadManager = DownloadManager.Builder().context(context)
            .logger { message -> log?.log(message) }
            .build()
    }

}