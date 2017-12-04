package com.scrapy4s.http


import com.scrapy4s.exception.TooManyRetriesException
import com.scrapy4s.http.proxy.ProxyModel
import com.scrapy4s.spider.Spider
import com.scrapy4s.util.HttpUtil
import org.asynchttpclient
import org.asynchttpclient.Dsl.{asyncHttpClient, _}
import org.asynchttpclient.proxy.ProxyServer
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

case class Request(
                    url: String,
                    method: String = Method.GET,
                    data: Map[String, Seq[String]] = Map.empty,
                    header: Map[String, Seq[String]] = Map.empty
                  ) {
  val logger = LoggerFactory.getLogger(classOf[Request])

  override def equals(obj: scala.Any) = {
    obj match {
      case _obj: Request =>
        if (
          _obj.method.equals(this.method) ||
            _obj.url.equals(this.url)
        ) {
          true
        } else {
          false
        }
      case _ =>
        false
    }
  }

  def headerAsJava = {
    header.map(m => m._1 -> m._2.asJava).asJava
  }

  def dataAsJava = {
    data.map(m => m._1 -> m._2.asJava).asJava
  }

  def withHeader(key: String, value: String) = {
    Request(
      url = url,
      method = method,
      data = data,
      header = header + (key -> Seq(value))
    )
  }

  /**
    * 执行请求
    *
    * @return 返回Response对象
    */
  def execute(spider: Spider): Response = execute(spider.requestConfig)

  def execute(config: RequestConfig = RequestConfig.default): Response = {
    logger.info(s"crawler -> ${this.method}: ${this.url}")

    var error_count = 0
    var proxyErrorCount = 0
    var proxy: Option[ProxyModel] = None

    while (error_count <= config.tryCount) {
      val client = asyncHttpClient()
      // 代理重试次数
      if (proxyErrorCount >= 2) {
        proxyErrorCount = 0
        proxy = None
      }
      try {
        // 获取代理
        if (config.proxyResource.isDefined && proxy.isEmpty) {
          if (proxy.isEmpty) {
            proxy = Option(config.proxyResource.get.get)
          }
        }
        val response: asynchttpclient.Response = client.prepareRequest {
          val req = request(this.method, this.url)
            .setFormParams(this.dataAsJava)
            .setHeaders(this.headerAsJava)
            .setHeader("User-Agent", HttpUtil.randomUserAgent)
            .setRequestTimeout(config.timeOut)
            .setMethod(this.method)
            .setFollowRedirect(true)
          // 设置代理
          (if (proxy.isDefined) {
            req.setProxyServer(new ProxyServer.Builder(proxy.get.ip, proxy.get.port))
          } else req).build()
        }.execute().get()
        val _res = Response(this, response)
        // 判断是否成功
        if (config.test_func(_res)) {
          if (proxy.isDefined) {
            config.proxyResource.get.returnProxy(proxy.get)
          }
          return _res
        } else {
          throw new Exception("test function return false")
        }
      } catch {
        case e: Exception =>
          error_count += 1
          proxyErrorCount += 1
          if (error_count > config.tryCount) {
            throw new TooManyRetriesException(s"try count is max -> ${config.tryCount}, url -> $url", e)
          } else {
            logger.warn(s"request error: ${e.getMessage}, try $error_count times .... $method -> $url")
          }
      } finally {
        client.close()
      }
    }
    throw new Exception("unknown exception")
  }
}
