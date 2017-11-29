package http

/**
  * Created by sheep3 on 2017/11/29.
  */
case class RequestConfig(
                          tryCount: Int = 10,
                          test_func: Response => Boolean = r => r.response.isSuccess
                        )
object RequestConfig {
  val default = new RequestConfig()
}