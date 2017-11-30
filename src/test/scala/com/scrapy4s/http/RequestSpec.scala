package com.scrapy4s.http

import org.scalatest.FunSuite

/**
  * Created by sheep3 on 2017/11/30.
  */
class RequestSpec extends FunSuite {
  test("request content") {
    val _match = Request(s"http://esf.fang.com/newsecond/map/NewMapDetail.aspx?newcode=1010111145&isrent=Y&width=678&height=355")
      .withHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36")
      .execute()
      .regex("""px:"(.*?)",py:"(.*?)"""".r)
      .head
    println(_match)
    assert("116.395645".equals(_match(0)))
  }
}
