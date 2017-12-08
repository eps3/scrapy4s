package com.scrapy4s.monitor

class AbstractMonitor extends Monitor {
  /**
    * 通知触发条件
    */
  override def noticeCheck(): Unit = {}

  /**
    * 通知
    */
  override def notice(): Unit = {}

  /**
    * 爬虫节点停止
    */
  override def stopHook(): Unit = {}

  /**
    * 爬虫节点开始
    */
  override def startHook(): Unit = {}
}
