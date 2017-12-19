package com.scrapy4s.monitor

import java.util.concurrent.atomic.AtomicInteger

import org.slf4j.LoggerFactory

/**
  * Created by sheep3 on 2017/12/19.
  */
class CountLogMonitor(name: String) extends Monitor {
  val logger = LoggerFactory.getLogger(classOf[CountLogMonitor])
  val successCount = new AtomicInteger()

  val errorCount = new AtomicInteger()

  val startCount = new AtomicInteger()

  val allCount = new AtomicInteger()

  /**
    * 爬取成功
    */
  def requestSuccessHook() = {
    successCount.incrementAndGet()
    startCount.decrementAndGet()
  }

  /**
    * 爬取失败
    */
  def requestErrorHook() = {
    errorCount.incrementAndGet()
    startCount.decrementAndGet()
  }

  /**
    * 开始请求
    */
  def requestStartHook() = {
    startCount.incrementAndGet()
    printLog()
  }


  /**
    * 加入队列
    */
  def requestPutHook() = {
    allCount.incrementAndGet()
  }


  def printLog() = logger.info(s"[monitor-$name] 所有任务: ${allCount.get()}, 完成: ${successCount.get()}, 失败: ${errorCount.get()}, 正在执行: ${startCount.get()}")
}

object CountLogMonitor {
  def apply(name: String): CountLogMonitor = new CountLogMonitor(name)
}