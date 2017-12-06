package com.scrapy4s.exception

/**
  * Created by sheep3 on 2017/12/6.
  */
class QueueTimeOutException (private val message: String = "queue time out",
                             private val cause: Throwable = None.orNull)
  extends Exception(message, cause) {
}
