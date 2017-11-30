package com.scrapy4s.extractor

import org.scalatest.FunSuite

/**
  * Created by sheep3 on 2017/11/28.
  */
class ExtractorSpec extends FunSuite{
  test("regex value => 123") {
    val matchData = Extractor.regex(""">(.*?)<""".r, "a>123<d").head(0)
    assert(matchData.equals("123"))
  }
}
