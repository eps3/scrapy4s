package com.scrapy4s.monitor

import com.scrapy4s.spider.Spider

/**
  * 监控接口
  */
trait Monitor {

  def requestSuccessHook(spider: Spider)

  def requestErrorHook(spider: Spider)

  def requestStartHook(spider: Spider)

  def requestEndHook(spider: Spider)

  def requestPutHook(spider: Spider)

}
