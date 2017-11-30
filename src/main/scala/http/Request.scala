package http

import java.util.concurrent.Future

import org.asynchttpclient
import spider.Spider
import org.asynchttpclient.Dsl.{asyncHttpClient, _}
import util.HttpUtil
import scala.collection.JavaConverters._

case class Request(
                    url: String,
                    method: String = Method.GET,
                    data: Map[String, Seq[String]] = Map.empty,
                    header: Map[String, Seq[String]] = Map.empty
                  ) {
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
  def execute(spider: Spider[_]): Response = execute(spider.requestConfig)

  def execute(config: RequestConfig = RequestConfig.default): Response = {
    var error_count = 0
    while (error_count <= config.tryCount) {
      val client = asyncHttpClient()
      try {
        val response: asynchttpclient.Response  = client.prepareRequest(
          request(this.method, this.url)
            .setFormParams(this.dataAsJava)
            .setHeaders(this.headerAsJava)
            .setHeader("User-Agent", HttpUtil.randomUserAgent)
            .setMethod(this.method)
            .build()
        ).execute().get()
        val _res = Response(this, response)
        if (config.test_func(_res)) {
          return _res
        } else {
          throw new Exception("test function return false")
        }
      } catch {
        case e: Exception =>
          error_count += 1
          if (error_count > config.tryCount) {
            throw new Exception(s"try count is max -> ${config.tryCount}, url -> $url", e)
          }
      } finally {
        client.close()
      }
    }
    throw new Exception("unknown exception")
  }
}
