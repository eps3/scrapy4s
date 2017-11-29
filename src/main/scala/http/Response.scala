package http

import java.io.InputStream

import extractor.Extractor
import org.asynchttpclient

import scala.util.matching.Regex

/**
  * Created by sheep3 on 2017/11/28.
  */
case class Response(
                     request: Request,
                     _response: asynchttpclient.Response
                   ) {
  def body: String = _response.getResponseBody
  def inputStream: InputStream = _response.getResponseBodyAsStream

  def regex(r: Regex) = {
    Extractor.regex(r: Regex, body)
  }

  def regex(r: String) = {
    Extractor.regex(r.r: Regex, body)
  }
}
