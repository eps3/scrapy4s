package com.scrapy4s.exception

/**
  * Created by sheep3 on 2017/12/4.
  */
class TooManyRetriesException(private val message: String = "try time max",
                              private val cause: Throwable = None.orNull)
  extends Exception(message, cause) {
}

