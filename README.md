### Scala 版本Scrapy

#### 1. 组件

- scheduler -> 调度器，用于url调度与去重
- pipeline -> 管道，用于数据的后续处理
- http -> http组件，用于http相关封装
- spider -> 爬虫核心类，用于构建爬虫



#### 2. 使用

```scala
import http.Request
import pipeline.HtmlSavePipeline
import spider.SimpleSpider
import util.FileUtil

object ExampleSpider {
  def main(args: Array[String]): Unit = {
    SimpleSpider()
      .withStartUrl(Seq(
          "https://segmentfault.com",
          "https://segmentfault.com",
          "https://segmentfault.com/q/1010000012185894"
        ).map(Request(_)))
      .withPipeline(HtmlSavePipeline[String](FileUtil.pathWithHome(Seq("data", "spider", "example"))))
      .start()
  }
}
```

#### 3 pipeline 

> 是爬虫数据处理核心，对于多个pipeline，每个请求的数据都会在每个pipeline里执行

##### 2.1.1 LineFilePipeline





 

#### Update Log

- 2017-11-27
  - 完成了简单初版 例子代码 -> example.ExampleSpider
- 2017-11-30
  - 完善了大部分功能


#### TODO

- v1: 完成基于传统Java并发API版本的Scrapy
  - 完善Request的封装
- v2: 添加异步io以及Akka调度机制
- v3: 添加web监控管理器