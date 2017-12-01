package com.scrapy4s.example

import com.scrapy4s.http.Request
import com.scrapy4s.pipeline.FileDumpPipeline
import com.scrapy4s.spider.Spider
import com.scrapy4s.util.{FileUtil, HashUtil}

/**
  * Created by sheep3 on 2017/11/30.
  */
object MeiziSpider {
  def main(args: Array[String]): Unit = {
    /**
      * 下载器
      */
    val downloader = Spider().pipe(
      FileDumpPipeline(FileUtil.pathWithHome(Seq("data", "spider", "meizi")))(r => {
        HashUtil.getHash(r.url) + ".jpg"
      })
    )

    /**
      * 详情页
      */
    val detail = Spider().pipe(_.regex("""<img alt=".*?src="(.*?)" /><br />""").map(_ (0)).foreach(url => downloader.execute(Request(url))))

    /**
      * 列表页
      */
    Spider().pipeForRequest(r => {
      /**
        * 获取详情页
        */
      r.regex("""<a target='_blank' href="(.*?)">""")
        .map(_ (0))
        .foreach(url => detail.execute(Request(url)))

      /**
        * 获取新的列表页
        */
      r.regex("""<li><a href='(.*?)'>""")
        .map(_ (0))
        .map(i => Request(s"http://www.meizitu.com/a/$i"))
    }).setStartUrl(Request(s"http://www.meizitu.com/a/more_1.html"))
      .run()
  }
}
