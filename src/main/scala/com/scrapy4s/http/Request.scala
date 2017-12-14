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

/**
  * 请求体封装
  *
  * @param url    目标Url
  * @param method 请求方法
  * @param data   请求数据
  * @param header 请求的header
  * @param json   请求json数据
  */
case class Request(
                    var url: String,
                    var method: String = Method.GET,
                    var data: Map[String, Seq[String]] = Map.empty,
                    var header: Map[String, Seq[String]] = Map.empty,
                    var json: Option[String] = None,
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

  def setJson(newJson: String) = {
    json = Option(newJson)
    setHeader("Content-type", "application/json")
    this
  }


  def setHeader(key: String, value: String) = {
    header = header + (key -> Seq(value))
    this
  }

  private def headerAsJava = {
    header.map(m => m._1 -> m._2.asJava).asJava
  }

  private def dataAsJava = {
    data.map(m => m._1 -> m._2.asJava).asJava
  }

  def httpClient = {
    val req = request(this.method, this.url)
      .setFormParams(this.dataAsJava)
      .setHeaders(this.headerAsJava)
      .setHeader("User-Agent", HttpUtil.randomUserAgent)
      .setMethod(this.method)
      .setFollowRedirect(true)
    json match {
      case Some(j) =>
        req.setBody(j)
      case _ =>
        req
    }
  }

  /**
    * 执行请求
    *
    * @return 返回Response对象
    */
  def execute(spider: Spider): Response = execute(spider.requestConfig)

  def execute(config: RequestConfig = RequestConfig.default): Response = {
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
          val req = httpClient.setRequestTimeout(config.timeOut)
          // 设置代理
          (if (proxy.isDefined) {
            logger.debug(s"crawler -> ${this.method}: ${this.url} , with proxy: ${proxy.get.ip}:${proxy.get.port}")
            req.setProxyServer(new ProxyServer.Builder(proxy.get.ip, proxy.get.port))
          } else {
            logger.debug(s"crawler -> ${this.method}: ${this.url}")
            req
          }
            ).build()
        }.execute().get()
        val _res = packageResponse(response)
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

  def packageResponse(response: asynchttpclient.Response): Response = {
    Response(this, response)
  }
}
