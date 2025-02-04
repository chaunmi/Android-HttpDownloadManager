package com.chaunmi.downloader

/**
 * This will used to mark the state of download request.
 *
 * @author Vincent Cheung (coolingfall@gmail.com)
 */
enum class DownloadState {
  INVALID,  /* State invalid(the request is not in queue). */

  PENDING,  /* State when the download is currently pending. */

  RUNNING,  /* State when the download is currently running. */

  SUCCESSFUL,  /* State when the download is successful. */

  FAILURE,  /* State when the download is failed. */
}