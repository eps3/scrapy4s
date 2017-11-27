package util

import java.net.Proxy

import scalaj.http.{BaseHttp, HttpConstants}

class UAHttp(userAgent: String)  extends BaseHttp(
      proxyConfig = None,
      options = HttpConstants.defaultOptions,
      charset = HttpConstants.utf8,
      sendBufferSize = 4096,
      userAgent = userAgent,
      compress = true
)
object UAHttp {
  def apply(): UAHttp = new UAHttp(userAgent="")

  def main(args: Array[String]): Unit = {
    UAHttp()
  }
}
