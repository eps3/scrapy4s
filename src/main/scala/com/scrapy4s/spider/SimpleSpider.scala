package com.scrapy4s.spider

import com.scrapy4s.http.Response


class SimpleSpider(linePaser: Response => String) extends Spider[String](linePaser) {

}

object SimpleSpider {
  def apply(linePaser: Response => String = r => r.body): SimpleSpider = new SimpleSpider(linePaser)
}
