package com.chaunmi.downloadersample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.SparseIntArray
import android.view.View
import android.view.View.OnClickListener
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chaunmi.downloader.DownloadCallback
import com.chaunmi.downloader.DownloadManager
import com.chaunmi.downloader.DownloadManager.Builder
import com.chaunmi.downloader.DownloadRequest
import com.chaunmi.downloader.OkHttpDownloader.Companion.create
import com.chaunmi.downloader.Priority.HIGH
import com.chaunmi.downloader.Priority.NORMAL
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit.SECONDS

class MainActivity : AppCompatActivity(), OnClickListener {
  private val ids = SparseIntArray()
  private var progressBar: ProgressBar? = null
  private var progressBar1: ProgressBar? = null
  private var progressBar2: ProgressBar? = null
  private var progressBar3: ProgressBar? = null
  private var progressBar4: ProgressBar? = null
  private var textSpeed: TextView? = null
  private var textSpeed1: TextView? = null
  private var textSpeed2: TextView? = null
  private var textSpeed3: TextView? = null
  private var textSpeed4: TextView? = null
  private var downloadManager: DownloadManager? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    findViewById<View>(R.id.download_btn_start).setOnClickListener(this)
    findViewById<View>(R.id.download_btn_start1).setOnClickListener(this)
    findViewById<View>(R.id.download_btn_start2).setOnClickListener(this)
    findViewById<View>(R.id.download_btn_start3).setOnClickListener(this)
    findViewById<View>(R.id.download_btn_start4).setOnClickListener(this)
    progressBar = findViewById(R.id.download_progress)
    progressBar1 = findViewById(R.id.download_progress1)
    progressBar2 = findViewById(R.id.download_progress2)
    progressBar3 = findViewById(R.id.download_progress3)
    progressBar4 = findViewById(R.id.download_progress4)
    textSpeed = findViewById(R.id.download_tv_speed0)
    textSpeed1 = findViewById(R.id.download_tv_speed1)
    textSpeed2 = findViewById(R.id.download_tv_speed2)
    textSpeed3 = findViewById(R.id.download_tv_speed3)
    textSpeed4 = findViewById(R.id.download_tv_speed4)
    val client: OkHttpClient = OkHttpClient.Builder()
        .build()
    downloadManager = Builder().context(this)
        .downloader(create(client))
        .threadPoolSize(3)
        .logger { message -> Log.d(TAG, message) }
        .build()
  }

  override fun onDestroy() {
    super.onDestroy()
    downloadManager!!.release()
  }

  override fun onClick(v: View) {
    var index = 0
    when (v.id) {
      R.id.download_btn_start -> {
        index = INDEX_0
      }
      R.id.download_btn_start1 -> {
        index = INDEX_1
      }
      R.id.download_btn_start2 -> {
        index = INDEX_2
      }
      R.id.download_btn_start3 -> {
        index = INDEX_3
      }
      R.id.download_btn_start4 -> {
        index = INDEX_4
      }
    }
    val id = ids[index, -1]
    if (downloadManager!!.isDownloading(id)) {
      downloadManager!!.cancel(id)
    } else {
      val request = DownloadRequest.Builder()
          .url(
              URL[index]
          )
          .downloadCallback(Listener())
          .retryTime(5)
          .retryInterval(5, SECONDS)
          .progressInterval(1, SECONDS)
          .relativeDirectory("/test")
          .priority(if (index == 4) HIGH else NORMAL)
          .build()
      val downloadId = downloadManager!!.add(request)
      ids.put(index, downloadId)
    }
  }

  private fun queryIndex(id: Int): Int {
    for (i in 0 until ids.size()) {
      if (ids.valueAt(i) == id) {
        return ids.keyAt(i)
      }
    }
    return 0
  }

  private inner class Listener : DownloadCallback {
    private var startTimestamp: Long = 0
    private var startSize: Long = 0
    override fun onStart(
      downloadId: Int,
      totalBytes: Long
    ) {
      Log.d(TAG, "start download: $downloadId")
      Log.d(TAG, "totalBytes: $totalBytes")
      startTimestamp = System.currentTimeMillis()
    }

    override fun onRetry(downloadId: Int) {
      Log.d(TAG, "retry downloadId: $downloadId")
    }

    @SuppressLint("SetTextI18n") override fun onProgress(
      downloadId: Int,
      bytesWritten: Long,
      totalBytes: Long
    ) {
      val progress = (bytesWritten * 100f / totalBytes).toInt()
      val currentTimestamp = System.currentTimeMillis()
      Log.d(TAG, "progress: $progress")
      val speed: Int
      val deltaTime = (currentTimestamp - startTimestamp + 1).toInt()
      speed = ((bytesWritten - startSize) * 1000 / deltaTime).toInt() / 1024
      startSize = bytesWritten
      when (queryIndex(downloadId)) {
        INDEX_0 -> {
          progressBar!!.progress = progress
          textSpeed!!.text = speed.toString() + "kb/s"
        }
        INDEX_1 -> {
          progressBar1!!.progress = progress
          textSpeed1!!.text = speed.toString() + "kb/s"
        }
        INDEX_2 -> {
          progressBar2!!.progress = progress
          textSpeed2!!.text = speed.toString() + "kb/s"
        }
        INDEX_3 -> {
          progressBar3!!.progress = progress
          textSpeed3!!.text = speed.toString() + "kb/s"
        }
        INDEX_4 -> {
          progressBar4!!.progress = progress
          textSpeed4!!.text = speed.toString() + "kb/s"
        }
        else -> {}
      }
    }

    override fun onSuccess(
      downloadId: Int,
      filepath: String
    ) {
      Log.d(TAG, "success: " + downloadId + " size: " + File(filepath).length() + ", filepath: $filepath")
      when (queryIndex(downloadId)) {
        INDEX_0 -> {
          progressBar!!.progress = 100
        }
        INDEX_1 -> {
          progressBar1!!.progress = 100
        }
        INDEX_2 -> {
          progressBar2!!.progress = 100
        }
        INDEX_3 -> {
          progressBar3!!.progress = 100
        }
        INDEX_4 -> {
          progressBar4!!.progress = 100
        }
        else -> {}
      }

      val result = downloadManager!!.copyToPublicDownloadDir(filepath)
//      Log.i(TAG, "result of copying file: $result")
    }

    override fun onFailure(
      downloadId: Int,
      statusCode: Int,
      errMsg: String?
    ) {
      Log.d(TAG, "fail: $downloadId $statusCode $errMsg")
    }
  }

  companion object {
    private const val TAG = "HttpDownloadManager"
    private val URL = arrayOf(
        "https://cdn.llscdn.com/yy/files/xs8qmxn8-lls-LLS-5.8-800-20171207-111607.apk",
        "https://cdn.llscdn.com/yy/files/tkzpx40x-lls-LLS-5.7-785-20171108-111118.apk",
        "http://dldir1.qq.com/weixin/android/weixin703android1400.apk",
        "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk",
        "http://wap.dl.pinyin.sogou.com/wapdl/hole/201512/03/SogouInput_android_v7.11_sweb.apk"
    )
    private const val INDEX_0 = 0
    private const val INDEX_1 = 1
    private const val INDEX_2 = 2
    private const val INDEX_3 = 3
    private const val INDEX_4 = 4
  }
}