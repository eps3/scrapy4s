### scrapy4s

>  易用的scala爬虫框架



#### 1. 组件

- scheduler -> 调度器，用于url调度与去重
- pipeline -> 管道，用于数据的后续处理
- http -> http组件，用于http相关封装
- spider -> 爬虫核心类，用于构建爬虫



#### 2. 使用

```scala
import com.scrapy4s.http.Request
import com.scrapy4s.pipeline.HtmlSavePipeline
import com.scrapy4s.spider.SimpleSpider
import com.scrapy4s.util.FileUtil

object ExampleSpider {
  def main(args: Array[String]): Unit = {
    SimpleSpider()
      .withStartUrl(Seq(
          "https://segmentfault.com",
          "https://segmentfault.com",
          "https://segmentfault.com/q/1010000012185894"
        ).map(Request(_)))
      .withPipeline(HtmlSavePipeline[String]("~/data/"))
      .start()
  }
}
```



#### 3 pipeline 

> 是爬虫数据处理核心，对于多个pipeline，每个请求的数据都会在每个pipeline里执行

##### 2.1.1 LineFilePipeline 行数据Pipeline

```scala
import com.scrapy4s.pipeline.LineFilePipeline
import com.scrapy4s.http.Response
// 第一个参数是目标文件
// 第二个参数是需要存的行数据解析函数
val lineFilePipeline = LineFilePipeline("~/data/line.txt")((body: String, response: Response) => {
  s"${response.url} ${response.statusCode}"
})
```



##### 2.1.2 FileDumpPipeline 文件下载Pipeline

```scala
import com.scrapy4s.pipeline.FileDumpPipeline
import com.scrapy4s.http.Response
// 第一个参数是存放的文件夹
// 第二个参数是文件名生成函数
val fileDumpPipeline = FileDumpPipeline[String]("~/data/")((body: String, r: Response) => {
  val splitArr = r.url.split("/")
  splitArr(splitArr.length - 1)
})
```



##### 2.1.3 Other Pileline

- LoggerPipeline 打印日志的Pipeline
- MultiThreadPipeline 多线程Pipeline
- SingleThreadPipeline 单线程Pipeline



#### Update Log

- 2017-11-27
  - 完成了简单初版 例子代码 -> example.ExampleSpider
- 2017-11-30
  - 完善了大部分功能


#### TODO

- v1: 完成基于传统Java并发API版本的Scrapy
  - 完善Request的封装
  - 添加代理支持
  - 添加MySqlPipeline
- v2: 添加异步io以及Akka调度机制
- v3: 添加web监控管理器