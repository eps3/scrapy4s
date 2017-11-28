package http

import scalaj.http.HttpResponse

/**
  * Created by sheep3 on 2017/11/28.
  */
case class Response(request: Request, response: HttpResponse[String]) {
}
