package http

import scala.util.Random
import scalaj.http._

/**
  * Created by sheep3 on 2017/11/28.
  */
object UAHttp extends BaseHttp {
  def apply(url: String, userAgent: String=randomUserAgent): HttpRequest = HttpRequest(
    url = url,
    method = "GET",
    connectFunc = DefaultConnectFunc,
    params = Nil,
    headers = Seq(
      "User-Agent" -> userAgent
    ),
    options = HttpConstants.defaultOptions,
    proxyConfig = None,
    charset = HttpConstants.utf8,
    sendBufferSize = 4096,
    urlBuilder = QueryStringUrlFunc,
    compress = true
  )

  val userAgentList: Array[String] = Array[String](
    "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0",
    "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; .NET4.0C; .NET4.0E; .NET CLR 2.0.50727; .NET CLR 3.0.30729; .NET CLR 3.5.30729; InfoPath.3; rv:11.0) like Gecko",
    "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0;",
    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)",
    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)",
    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)"
  )
  def randomUserAgent: String = userAgentList(Random.nextInt(userAgentList.length))
}