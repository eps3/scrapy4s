package com.scrapy4s.example

import com.scrapy4s.http.Request
import com.scrapy4s.pipeline.FileDumpPipeline
import com.scrapy4s.spider.SimpleSpider
import com.scrapy4s.util.{FileUtil, HashUtil}

/**
  * Created by sheep3 on 2017/11/30.
  */
object MeiziSpider {
  def main(args: Array[String]): Unit = {
    /**
      * 下载器
      */
    val downloader = SimpleSpider().withPipeline(
      FileDumpPipeline(FileUtil.pathWithHome(Seq("data", "spider", "meizi")))((t, r) => {
        HashUtil.getHash(r.url) + ".jpg"
      })
    )

    /**
      * 详情页
      */
    val detail = SimpleSpider(r => {
      r.regex("""<img alt=".*?src="(.*?)" /><br />""").map(_ (0)).foreach(url => downloader.execute(Request(url)))
      r.url
    })

    /**
      * 列表页
      */
    SimpleSpider(r => {
      r.regex("""<a target='_blank' href="(.*?)">""")
        .map(_ (0))
        .foreach(url => detail.execute(Request(url)))
      r.url
    }).withStartUrl((1 to 72).map(i => Request(s"http://www.meizitu.com/a/more_$i.html")))
      .start()

    /**
      * 按任务顺序挨个等待关闭
      */
    detail.waitForShop()
    downloader.waitForShop()
  }
}
