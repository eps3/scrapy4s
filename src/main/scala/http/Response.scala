package http

import extractor.Extractor

import scala.util.matching.Regex
import scalaj.http.HttpResponse

/**
  * Created by sheep3 on 2017/11/28.
  */
case class Response(request: Request, response: HttpResponse[String]) {
  def regex(r: Regex) ={
    Extractor.regex(r: Regex,response.body)
  }
}
