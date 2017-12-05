package com.scrapy4s.manage

import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

import com.scrapy4s.http.proxy.ProxyResource
import com.scrapy4s.spider.Spider

/**
  * 命令行管理器
  */
case class CmdManage(
                      var spiders: Seq[Spider] = Seq.empty[Spider],
                      var threadCount: Int = Runtime.getRuntime.availableProcessors() * 2,
                      var currentThreadPool: Option[ThreadPoolExecutor] = None,
                      var proxyResource: Option[ProxyResource] = None
                    ) extends Manage {

  def setProxyResource(proxy: ProxyResource) = {
    this.proxyResource = Some(proxy)
    this
  }

  def setThreadPool(tp: ThreadPoolExecutor) = {
    this.currentThreadPool = Option(tp)
    this
  }

  def setThreadCount(count: Int) = {
    this.threadCount = count
    this
  }

  def register(spider: Spider) = {
    this.spiders = spiders :+ spider
    this
  }

  override def start(): Unit = {
    currentThreadPool match {
      case Some(tp) =>
        spiders
          .map(_.setThreadPool(tp))
      case _ =>
    }
    proxyResource match {
      case Some(pr) =>
        spiders
          .map(_.setProxyResource(pr))
      case _ =>
    }
    spiders.foreach(_.run())
  }

}
