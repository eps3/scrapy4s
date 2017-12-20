package com.scrapy4s.monitor

import java.util.concurrent.atomic.AtomicInteger

import com.scrapy4s.spider.Spider
import org.slf4j.LoggerFactory

/**
  * Created by sheep3 on 2017/12/19.
  */
class CountLogMonitor extends Monitor {
  val logger = LoggerFactory.getLogger(classOf[CountLogMonitor])
  val successCount = new AtomicInteger()

  val errorCount = new AtomicInteger()

  val startCount = new AtomicInteger()

  val allCount = new AtomicInteger()

  /**
    * 爬取成功
    */
  override def requestSuccessHook(spider: Spider) = {
    successCount.incrementAndGet()
  }

  /**
    * 爬取失败
    */
  override def requestErrorHook(spider: Spider) = {
    errorCount.incrementAndGet()
  }

  /**
    * 开始请求
    */
  override def requestStartHook(spider: Spider) = {
    startCount.incrementAndGet()
  }


  /**
    * 加入队列
    */
  override def requestPutHook(spider: Spider) = {
    allCount.incrementAndGet()
  }

  override def requestEndHook(spider: Spider): Unit = {
    startCount.decrementAndGet()
    printLog(spider)
  }

  def printLog(spider: Spider) = logger.info(s"[monitor-${spider.name}] 所有任务: ${allCount.get()}, 完成: ${successCount.get()}, 失败: ${errorCount.get()}, 正在执行: ${startCount.get()}")
}

object CountLogMonitor {
  def apply(): CountLogMonitor = new CountLogMonitor()
}