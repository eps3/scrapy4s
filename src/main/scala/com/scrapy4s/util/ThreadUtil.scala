package com.scrapy4s.util

/**
  * Created by sheep3 on 2017/12/5.
  */
object ThreadUtil {
  def core = {
    Runtime.getRuntime.availableProcessors()
  }
}
