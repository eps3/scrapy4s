package com.scrapy4s.util

import org.scalatest.FunSuite

/**
  * Created by sheep3 on 2017/12/4.
  */
class UrlUtilSpec  extends FunSuite{
  test("url util test") {
    val url = UrlUtil.getRealUrl("/xiaoqu/xicheng/", "https://bj.lianjia.com/xiaoqu/dongcheng/")
    println(url)
    assert("https://bj.lianjia.com/xiaoqu/xicheng/".equals(url))

    val url2 = UrlUtil.getRealUrl("xiaoqu/xicheng/", "https://bj.lianjia.com/xiaoqu/dongcheng/")
    println(url2)
    assert("https://bj.lianjia.com/xiaoqu/dongcheng/xiaoqu/xicheng/".equals(url2))

    val url3 = UrlUtil.getRealUrl("//www.ziroom.com/z/nl/z3-d23008614.html", "http://www.ziroom.com/z/nl/z3.html")
    println(url3)
    assert("http://www.ziroom.com/z/nl/z3-d23008614.html".equals(url3))
  }

}
