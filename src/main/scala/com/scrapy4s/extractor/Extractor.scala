package com.scrapy4s.extractor

import org.htmlcleaner.HtmlCleaner

import scala.util.matching.Regex

/**
  * Created by sheep3 on 2017/11/28.
  */
object Extractor {
  def regex(r: Regex, content: String) =
    r.findAllMatchIn(content).map(m => for (i <- 1 to m.groupCount) yield m.group(i)).toList


  def xpath(path: String, content: String) = {
    val hc = new HtmlCleaner()
    val html = hc.clean(content)
    html.evaluateXPath(path).map(_.toString).toList
  }
}
