package com.scrapy4s.http.proxy

import com.alibaba.fastjson.JSON
import com.scrapy4s.http.{Request, Response}
import org.slf4j.LoggerFactory

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

/**
  * Created by sheep3 on 2017/12/4.
  */
class RestProxyResource(
                         url: String,
                         minRefreshTime: Long,
                         paser: Response => Seq[ProxyModel]
                       ) extends ProxyResource {

  val logger = LoggerFactory.getLogger(classOf[RestProxyResource])
  val threadLocalProxyStack = new ThreadLocal[java.util.Stack[ProxyModel]]()
  val threadLocalRefreshTime = new ThreadLocal[Long]()

  /**
    * 获取新的代理
    */
  private def acquireProxy(): Unit = {
    try {
      paser(Request(url).execute()).foreach { proxy =>
        threadLocalProxyStack.get().push(proxy)
      }
    } catch {
      case e: Exception => logger.warn("proxy api error", e)
    }
  }

  override def get: ProxyModel = {
    var proxy: ProxyModel = null
    while (proxy == null) {
      // 建立本地线程代理栈
      if (threadLocalProxyStack.get() == null) {
        threadLocalProxyStack.set(new java.util.Stack[ProxyModel])
      } else if (threadLocalProxyStack.get().empty()) {
        // 判断上次刷新时间
        val timeWait = threadLocalRefreshTime.get + minRefreshTime - System.currentTimeMillis()
        if (timeWait > 0) {
          logger.info(s"Refresh time si too early......, wait ${timeWait}ms for sleep.....")
          Thread.sleep(timeWait)
        }
        threadLocalRefreshTime.set(System.currentTimeMillis())
        // 刷新代理
        acquireProxy()
      } else {
        // 直接返回代理
        proxy = threadLocalProxyStack.get().pop()
      }
    }
    proxy
  }

  override def returnProxy(proxy: ProxyModel): Unit = {
    threadLocalProxyStack.get().push(proxy)
  }
}

object RestProxyResource {

  def apply(
             url: String,
             minRefreshTime: Long = 60 * 1000,
             paser: Response => Seq[ProxyModel] = RestProxyResource.defaultPaser
           ): RestProxyResource = new RestProxyResource(url, minRefreshTime, paser)

  def defaultPaser(response: Response): Seq[ProxyModel] = {
    val proxyString: String = response.body
    JSON.parseObject(proxyString)
      .getJSONObject("result")
      .getJSONArray("proxy_list")
      .toJavaList(classOf[DefaultProxyModel])
      .asScala
      .map(p => {
        ProxyModel(p.proxy.split(":")(0), p.proxy.split(":")(1).toInt)
      })
  }

  class DefaultProxyModel {
    @BeanProperty
    var proxy: String = _
    @BeanProperty
    var userAgent: String = _
  }

}