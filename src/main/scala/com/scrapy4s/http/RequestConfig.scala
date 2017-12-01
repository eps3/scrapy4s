package com.scrapy4s.http

/**
  * Created by sheep3 on 2017/11/29.
  */
case class RequestConfig(
                          tryCount: Int,
                          timeOut: Int,
                          test_func: Response => Boolean
                        ) {
  def withTestFunc(tf: Response => Boolean) = {
    new RequestConfig(
      tryCount = this.tryCount,
      timeOut = this.timeOut,
      test_func = tf
    )
  }

  def withTryCount(newTryCount: Int) = {
    new RequestConfig(
      tryCount = newTryCount,
      timeOut = this.timeOut,
      test_func = this.test_func
    )
  }

  def withTimeOut(newTimeOut: Int) = {
    new RequestConfig(
      tryCount = this.tryCount,
      timeOut = newTimeOut,
      test_func = this.test_func
    )
  }
}

object RequestConfig {
  val default = RequestConfig()

  def apply(
             tryCount: Int = 10,
             timeOut: Int = 50 * 1000,
             test_func: Response => Boolean = r => r._response.getStatusCode < 400
           ): RequestConfig = new RequestConfig(tryCount, timeOut, test_func)

}