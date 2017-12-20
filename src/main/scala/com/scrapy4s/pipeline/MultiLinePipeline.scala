package com.scrapy4s.pipeline

import java.io.FileWriter

import com.scrapy4s.http.Response
import org.slf4j.LoggerFactory

/**
  * Created by sheep3 on 2017/12/20.
  */
class MultiLinePipeline(filePath: String,
                        linePaser: Response => Seq[String]
                       ) extends Pipeline {
  val logger = LoggerFactory.getLogger(classOf[MultiLinePipeline])

  val writer = new FileWriter(filePath)

  def pipe(response: Response): Unit = {
    val line = linePaser(response)
    line.foreach { l =>
      this.synchronized {
        writer.write(s"$l\n")
      }
    }
  }

  override def close(): Unit = {
    logger.info(s"save to -> $filePath")
    writer.close()
  }
}

object MultiLinePipeline {
  def apply(filePath: String)
           (implicit linePaser: Response => Seq[String] = r => Seq(s"${r.body}")): Pipeline = {
    new MultiLinePipeline(filePath, linePaser = linePaser)
  }
}