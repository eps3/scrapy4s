package com.scrapy4s.http

import org.asynchttpclient

/**
  * Created by sheep3 on 2017/12/13.
  */
class ResponseWithData[T](request: Request,
                          _response: asynchttpclient.Response, val message: T) extends Response(request, _response) {

}