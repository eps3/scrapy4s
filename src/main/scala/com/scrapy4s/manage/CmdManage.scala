package com.scrapy4s.manage

import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

import com.scrapy4s.http.proxy.ProxyResource
import com.scrapy4s.spider.Spider

/**
  * 命令行管理器
  */
case class CmdManage(
                      spiders: Seq[Spider] = Seq.empty[Spider],
                      threadCount: Int = Runtime.getRuntime.availableProcessors() * 2,
                      currentThreadPool: Option[ThreadPoolExecutor] = None,
                      proxyResource: Option[ProxyResource] = None
                    ) extends Manage {

  def setProxyResource(proxy: ProxyResource) = {
    CmdManage(
      spiders = spiders,
      threadCount = threadCount,
      currentThreadPool = currentThreadPool,
      proxyResource = Some(proxy)
    )
  }

  def setThreadPool(tp: ThreadPoolExecutor) = {
    CmdManage(
      spiders = spiders,
      threadCount = threadCount,
      currentThreadPool = Option(tp),
      proxyResource = proxyResource
    )
  }

  def setThreadCount(count: Int) = {
    CmdManage(
      spiders = spiders,
      threadCount = count,
      currentThreadPool = currentThreadPool,
      proxyResource = proxyResource
    )
  }

  def register(spider: Spider) = {
    CmdManage(
      spiders = spiders :+ spider,
      threadCount = threadCount,
      currentThreadPool = currentThreadPool,
      proxyResource = proxyResource
    )
  }

  lazy private val threadPool = {
    currentThreadPool match {
      case Some(tp) =>
        tp
      case _ =>
        new ThreadPoolExecutor(threadCount, threadCount,
          0L, TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue[Runnable](),
          new CallerRunsPolicy())
    }
  }

  override def start(): Unit = {
    spiders
      .map(_.setProxyResource(proxyResource.get))
      .map(_.setThreadPool(threadPool))
      .foreach(_.run())
  }

}
