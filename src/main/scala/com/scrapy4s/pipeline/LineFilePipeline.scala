package com.scrapy4s.pipeline

import java.io.FileWriter

import com.scrapy4s.http.Response
import org.slf4j.LoggerFactory


class LineFilePipeline(
                        filePath: String,
                        linePaser: Response => String
                      ) extends Pipeline  {
  val logger = LoggerFactory.getLogger(classOf[LineFilePipeline])

  val writer = new FileWriter(filePath)

  def pipe(response: Response): Unit = {
    val line = linePaser(response)
    this.synchronized {
      writer.write(s"$line\n")
    }
  }

  override def close(): Unit = {
    logger.info(s"save to -> $filePath")
    writer.close()
  }
}

object LineFilePipeline {
  def apply[T](filePath: String)
              (implicit linePaser: Response => String = r => s"${r.body}"): Pipeline = {
    SingleThreadPipeline(new LineFilePipeline(filePath, linePaser = linePaser))
  }
}