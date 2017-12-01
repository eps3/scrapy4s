package com.scrapy4s.example

import com.scrapy4s.http.Request
import com.scrapy4s.pipeline.FileDumpPipeline
import com.scrapy4s.spider.Spider
import com.scrapy4s.util.FileUtil

/**
  * Created by sheep3 on 2017/11/29.
  */
object ImageDownloadSpider {
  def main(args: Array[String]): Unit = {
    Spider()
      .setStartUrl(Seq(
        "https://www.retail-week.com/pictures/1180xany/7/3/9/1357739_Asda.jpg"
      ).map(Request(_)))
      .pipe(
        FileDumpPipeline(FileUtil.pathWithHome(Seq("data", "spider", "image")))(r => {
          val splitArr = r.url.split("/")
          splitArr(splitArr.length - 1)
        })
      )
      .start()
  }
}
