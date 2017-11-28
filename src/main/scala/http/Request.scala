package http

case class Request(
                    url: String,
                    method: String = Method.GET,
                    param: Map[String, String] = Map.empty,
                    data: Map[String, String] = Map.empty,
                    tryCount: Int = 10,
                    test_func: Response => Boolean = r => r.response.isSuccess
                  ) {
  override def equals(obj: scala.Any) = {
    obj match {
      case _obj: Request =>
        if (
          _obj.method.equals(this.method) ||
          _obj.url.equals(this.url)
        ){
          true
        } else {
          false
        }
      case _ =>
        false
    }
  }

  /**
    * 执行请求
    * @return 返回Response对象
    */
  def execute(): Response ={
    var error_count = 0
    while (error_count <= tryCount) {
      try {
        val _res = Response(this, UAHttp(this.url).method(this.method).asString)
        if (test_func(_res)) {
          return _res
        } else {
          throw new Exception("test function return false")
        }
      } catch {
        case e:Exception =>
          error_count += 1
          if (error_count > tryCount) {
            throw new Exception(s"try count is max -> $tryCount", e)
          }
      }
    }
    throw new Exception("unknown exception")
  }
}
