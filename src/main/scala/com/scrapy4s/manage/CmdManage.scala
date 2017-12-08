package com.scrapy4s.manage

import java.util.concurrent.LinkedBlockingQueue

import com.scrapy4s.http.proxy.ProxyResource
import com.scrapy4s.spider.Spider
import com.scrapy4s.thread.{DefaultThreadPool, ThreadPool}

/**
  * 命令行管理器
  */
case class CmdManage(
                      var spiders: Seq[Spider] = Seq.empty[Spider],
                      var history: Option[Boolean] = None,
                      var threadCount: Int = Runtime.getRuntime.availableProcessors() * 2,
                      var currentThreadPool: Option[ThreadPool] = None,
                      var proxyResource: Option[ProxyResource] = None
                    ) extends Manage {

  def setProxyResource(proxy: ProxyResource) = {
    this.proxyResource = Some(proxy)
    this
  }

  def setThreadPool(tp: DefaultThreadPool) = {
    this.currentThreadPool = Option(tp)
    this
  }

  def setHistory(h: Boolean) = {
    this.history = Option(h)
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

  lazy private val threadPool = {
    currentThreadPool match {
      case Some(tp) =>
        tp
      case _ =>
        new DefaultThreadPool(
          "cmdManage",
          threadCount,
          new LinkedBlockingQueue[Runnable]()
        )
    }
  }

  override def start(): Unit = {
    spiders
      .map(_.setThreadPool(threadPool))

    history match {
      case Some(h) =>
        spiders.map(_.setHistory(h))
      case _ =>
    }

    proxyResource match {
      case Some(pr) =>
        spiders
          .map(_.setProxyResource(pr))
      case _ =>
    }

    spiders.foreach(_.start())
  }

}
