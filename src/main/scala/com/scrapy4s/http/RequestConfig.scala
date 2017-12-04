package com.scrapy4s.http

import com.scrapy4s.http.proxy.ProxyResource

/**
  * Created by sheep3 on 2017/11/29.
  */
case class RequestConfig(
                          tryCount: Int,
                          timeOut: Int,
                          test_func: Response => Boolean,
                          proxyResource: Option[ProxyResource] = None
                        ) {
  def withTestFunc(tf: Response => Boolean) = {
    new RequestConfig(
      tryCount = this.tryCount,
      timeOut = this.timeOut,
      test_func = tf,
      proxyResource = this.proxyResource
    )
  }

  def withTryCount(newTryCount: Int) = {
    new RequestConfig(
      tryCount = newTryCount,
      timeOut = this.timeOut,
      test_func = this.test_func,
      proxyResource = this.proxyResource
    )
  }

  def withProxyResource(newProxyResource: ProxyResource) = {
    new RequestConfig(
      tryCount = this.tryCount,
      timeOut = this.timeOut,
      test_func = this.test_func,
      proxyResource = Option(newProxyResource)
    )
  }

  def withTimeOut(newTimeOut: Int) = {
    new RequestConfig(
      tryCount = this.tryCount,
      timeOut = newTimeOut,
      test_func = this.test_func,
      proxyResource = this.proxyResource
    )
  }
}

object RequestConfig {
  val default = RequestConfig()

  def apply(
             tryCount: Int = 10,
             timeOut: Int = 50 * 1000,
             test_func: Response => Boolean = r => r._response.getStatusCode < 400,
             proxyResource: Option[ProxyResource] = None
           ): RequestConfig = new RequestConfig(tryCount, timeOut, test_func, proxyResource)

}