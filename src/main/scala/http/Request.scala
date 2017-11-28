package http

case class Request(
                    url: String,
                    method: String = Method.GET,
                    param: Map[String, String] = Map.empty,
                    data: Map[String, String] = Map.empty
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

  def execute() ={
    Response(this, UAHttp(this.url).method(this.method).asString)
  }
}
