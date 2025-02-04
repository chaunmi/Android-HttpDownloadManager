package com.chaunmi.downloader

/**
 * Download request will be processed from higher priorities to lower priorities.
 *
 * @author Vincent Cheung (coolingfall@gmail.com)
 */
enum class Priority {
  LOW,  /* The lowest priority. */

  NORMAL,  /* Normal priority(default). */

  HIGH, /* The highest priority. */
}