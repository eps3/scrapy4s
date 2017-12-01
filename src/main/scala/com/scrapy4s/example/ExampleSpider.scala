package com.scrapy4s.example

import com.scrapy4s.http.Request
import com.scrapy4s.pipeline.HtmlSavePipeline
import com.scrapy4s.scheduler.HashSetScheduler
import com.scrapy4s.spider.Spider
import com.scrapy4s.util.FileUtil


object ExampleSpider {
  def main(args: Array[String]): Unit = {
    Spider()
      // 设置超时时间
      .setTimeOut(1000 * 5)
      // 设置线程数
      .setThreadCount(10)
      // 设置调度器
      .setScheduler(HashSetScheduler())
      // 设置请求成功的测试方法
      .setTestFunc(_.statusCode == 200)
      // 设置请求重试次数
      .setTryCount(3)
      // 设置起始Url
      .setStartUrl(Seq(
        "https://segmentfault.com",
        "https://segmentfault.com",
        "https://segmentfault.com/q/1010000012185894"
      ).map(Request(_)))
      // 设置数据处理器
      .pipe(HtmlSavePipeline(FileUtil.pathWithHome(Seq("data", "spider", "example"))))
      .start()
  }
}
