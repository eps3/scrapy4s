package com.scrapy4s.manage

import java.util.concurrent.LinkedBlockingQueue

import com.scrapy4s.http.Response
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
                      var test_func: Option[Response => Boolean] = None,
                      var proxyResource: Option[ProxyResource] = None
                    ) extends Manage {

  def setThreadPool(tp: DefaultThreadPool) = {
    this.currentThreadPool = Option(tp)
    this
  }

  def setThreadCount(count: Int) = {
    this.threadCount = count
    this
  }


  def setHistory(h: Boolean) = {
    this.history = Option(h)
    this
  }

  def setProxyResource(proxy: ProxyResource) = {
    this.proxyResource = Some(proxy)
    this
  }

  def setTestFunc(newTestFunc: Response => Boolean) = {
    this.test_func = Some(newTestFunc)
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
      .foreach(s => {
        s.setThreadPool(threadPool)

        if (test_func.isDefined) {
          s.setTestFunc(test_func.get)
        }
        if (history.isDefined) {
          s.setHistory(history.get)
        }
        if (proxyResource.isDefined) {
          s.setProxyResource(proxyResource.get)
        }

      })

    spiders.foreach(_.start())
  }

}
