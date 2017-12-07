package com.scrapy4s.http

import java.io.InputStream

import com.scrapy4s.extractor.Extractor
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

  def body(charSet: String): String = new String(_response.getResponseBodyAsBytes, charSet)

  def url: String = _response.getUri.toUrl

  def method: String = request.method

  def inputStream: InputStream = _response.getResponseBodyAsStream

  def statusCode: Int = _response.getStatusCode

  def regex(r: Regex) = {
    Extractor.regex(r: Regex, body)
  }

  def regex(r: String) = {
    Extractor.regex(r.r: Regex, body)
  }

  def xpath(path: String) = {
    Extractor.xpath(path, body)
  }
}
