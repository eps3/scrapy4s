package http

/**
  * Created by sheep3 on 2017/11/29.
  */
case class RequestConfig(
                          tryCount: Int = 10,
                          test_func: Response => Boolean = r => r._response.getStatusCode == 200
                        ){
  def withTestFunc(tf: Response => Boolean) = {
    new RequestConfig(
      tryCount = this.tryCount,
      test_func = tf
    )
  }
}
object RequestConfig {
  val default = new RequestConfig()
}