package com.scrapy4s.monitor

/**
  * 监控接口
  */
trait Monitor {

  def requestSuccessHook()

  def requestErrorHook()

  def requestStartHook()

  def requestPutHook()

}
