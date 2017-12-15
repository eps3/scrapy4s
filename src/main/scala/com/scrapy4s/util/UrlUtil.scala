package com.scrapy4s.util

import com.scrapy4s.extractor.Extractor

/**
  * Created by sheep3 on 2017/12/4.
  */
object UrlUtil {

  def getRealUrl(href: String, url: String) = {
    if (href.startsWith("http")) {
      href
    } else if (href.startsWith("""//""")) {
      val _url = Extractor.regex("""^(http[s]*?:)//.*?[/\?]""".r, url).head.head
      s"${_url}$href"
    } else if (href.startsWith("/")) {
      val head = Extractor.regex("""^(http[s]*?://.*?)[/\?]""".r, url).head.head
      s"$head$href"
    } else {
      val _url = Extractor.regex("""^(http[s]*?://.*)[^/]*$""".r, url).head.head
      s"${_url}$href"
    }
  }

}
