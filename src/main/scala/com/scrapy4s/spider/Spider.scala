package com.scrapy4s.spider

import java.util.concurrent.LinkedBlockingQueue

import com.scrapy4s.http.proxy.ProxyResource
import com.scrapy4s.http.{Request, RequestConfig, Response}
import com.scrapy4s.monitor.Monitor
import com.scrapy4s.pipeline.{MultiThreadPipeline, Pipeline, RequestPipeline}
import com.scrapy4s.scheduler.{HashSetScheduler, Scheduler}
import com.scrapy4s.thread.{DefaultThreadPool, ThreadPool}
import org.slf4j.LoggerFactory


/**
  * 爬虫核心类，用于组装爬虫
  */
class Spider(
              var name: String,
              var history: Boolean = false,
              var threadCount: Int = Runtime.getRuntime.availableProcessors() * 2,
              var requestConfig: RequestConfig = RequestConfig.default,
              var startUrl: Seq[Request] = Seq.empty[Request],
              var pipelines: Seq[Pipeline] = Seq.empty[Pipeline],
              var monitors: Seq[Monitor] = Seq.empty[Monitor],
              var scheduler: Scheduler = HashSetScheduler(),
              var currentThreadPool: Option[ThreadPool] = None
            ) {
  val logger = LoggerFactory.getLogger(classOf[Spider])

  lazy private val threadPool = {
    currentThreadPool match {
      case Some(tp) =>
        tp
      case _ =>
        new DefaultThreadPool(
          name,
          threadCount,
          new LinkedBlockingQueue[Runnable](),
        )
    }
  }

  def setThreadPool(tp: ThreadPool) = {
    this.currentThreadPool = Option(tp)
    this
  }

  def setRequestConfig(rc: RequestConfig) = {
    this.requestConfig = rc
    this
  }

  def setHistory(history: Boolean) = {
    this.history = history
    this
  }

  def setTestFunc(test_func: Response => Boolean) = {
    setRequestConfig(requestConfig.withTestFunc(test_func))
  }

  def setTimeOut(timeOut: Int) = {
    setRequestConfig(requestConfig.withTimeOut(timeOut))
  }

  def setTryCount(tryCount: Int) = {
    setRequestConfig(requestConfig.withTryCount(tryCount))
  }

  def setProxyResource(proxyResource: ProxyResource) = {
    setRequestConfig(requestConfig.withProxyResource(proxyResource))
  }


  def setStartRequest(urls: Request *): Spider = {
    this.startUrl = startUrl ++ urls
    this
  }


  def setStartUrl(urls: String *): Spider = {
    this.startUrl = startUrl ++ urls.map(u => Request(u))
    this
  }

  def setThreadCount(count: Int) = {
    this.threadCount = count
    this
  }


  /**
    * 设置监控
    *
    * @param monitor 添加的监控
    * @return
    */
  def setMonitor(monitor: Monitor): Spider = {
    this.monitors = monitors :+ monitor
    this
  }

  /**
    * 添加数据管道
    *
    * @param pipeline 添加新的数据管道
    * @return
    */
  @unchecked
  def pipe(pipeline: Pipeline): Spider = {
    this.pipelines = pipelines :+ pipeline
    this
  }

  def pipeForRequest(request: Response => Seq[Request]): Spider = {
    pipe(RequestPipeline(request))
  }

  /**
    * 将数据丢入一个新的线程池处理
    *
    * @param pipeline    执行的数据操作
    * @param threadCount 池大小线程数
    * @return
    */
  def fork(pipeline: Pipeline)(implicit threadCount: Int = Runtime.getRuntime.availableProcessors() * 2): Spider = {
    pipe(MultiThreadPipeline(pipeline)(threadCount))
  }

  def setScheduler(s: Scheduler) = {
    this.scheduler = s
    this
  }

  /**
    * 初始化爬虫设置，并将初始url倒入任务池中
    */
  def start() = {
    if (history) {
      if (name == null || name.isEmpty) {
        throw new Exception("spider name could not be empty when history is true")
      }
      scheduler.load(this)
    }
    startUrl.foreach(request => {
      execute(request)
    })
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      pipelines.foreach(p => {
        p.close()
      })
      if (history) {
        threadPool.shutdown()
        threadPool.waitForStop()
        scheduler.save(this)
      }
      logger.info(s"[$name] spider done !")
    }))
    this
  }


  /**
    * 提交请求任务到线程池
    *
    * @param request 等待执行的请求
    */
  def execute(request: Request): Unit = {
    // TODO: 这些hook需要单独抽出来
    /**
      * 判断是否已经爬取过
      */
    if (scheduler.check(request)) {
      /**
        * 监控投入任务的hook
        */
      monitors.foreach(_.requestPutHook(this))

      threadPool.execute(() => {
        try {
          /**
            * 开始抓取的hook
            */
          logger.info(s"[$name] START -> ${request.print}")
          monitors.foreach(_.requestStartHook(this))
          val response = request.execute(this)

          /**
            * 执行数据操作
            */
          pipelines.foreach(p => {
            try {
              p.pipeForRequest(response).foreach(request => this.execute(request))
            } catch {
              case e: Exception =>
                logger.error(s"[$name] pipe error, pipe: $p, request: ${request.print}", e)
            }
          })

          /**
            * 成功抓取的hook
            */
          monitors.foreach(_.requestSuccessHook(this))
          logger.info(s"[$name] SUCCESS -> ${request.print}")
        } catch {
          case e: Exception =>

            /**
              * 抓取失败的hook
              */
            monitors.foreach(_.requestErrorHook(this))
            logger.error(s"[$name] ERROR -> request: ${request.print}", e)
        }
        if (history) {
          // 保存成功信息
          scheduler.ok(request)
        }

        /**
          * 抓取结束的hook
          */
        monitors.foreach(_.requestEndHook(this))
      })
    } else {
      logger.debug(s"[$name] ${request.print} has bean spider !")
    }
  }
}

object Spider {
  def apply(name: String = ""): Spider = new Spider(name)
}