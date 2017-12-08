package com.scrapy4s.monitor

import java.util.concurrent.atomic.AtomicInteger

/**
  * 监控接口
  */
trait Monitor {

  val successCount = new AtomicInteger()

  val errorCount = new AtomicInteger()

  val startCount = new AtomicInteger()

  val allCount = new AtomicInteger()

  /**
    * 通知触发条件
    */
  def noticeCheck()

  /**
    * 通知
    */
  def notice()


  /**
    * 爬虫节点停止
    */
  def stopHook()


  /**
    * 爬虫节点开始
    */
  def startHook()


  /**
    * 爬取成功
    */
  def requestSuccessHook() = {
    successCount.incrementAndGet()
  }

  /**
    * 爬取失败
    */
  def requestErrorHook() = {
    errorCount.incrementAndGet()
  }

  /**
    * 开始请求
    */
  def requestStartHook() = {
    startCount.incrementAndGet()
  }


  /**
    * 加入队列
    */
  def requestPutHook() = {
    allCount.incrementAndGet()
  }

}
