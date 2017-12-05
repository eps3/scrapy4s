package com.scrapy4s.pipeline

import java.io.FileWriter

import com.scrapy4s.http.Response
import org.slf4j.LoggerFactory


class LineFilePipeline(
                        filePath: String,
                        linePaser: Response => Option[String]
                      ) extends Pipeline  {
  val logger = LoggerFactory.getLogger(classOf[LineFilePipeline])

  val writer = new FileWriter(filePath)

  def pipe(response: Response): Unit = {
    val line = linePaser(response)
    line match {
      case Some(l) =>
        this.synchronized {
          writer.write(s"$l\n")
        }
      case _ =>
    }
  }

  override def close(): Unit = {
    logger.info(s"save to -> $filePath")
    writer.close()
  }
}

object LineFilePipeline {
  def apply[T](filePath: String)
              (implicit linePaser: Response => Option[String] = r => Some(s"${r.body}")): Pipeline = {
    new LineFilePipeline(filePath, linePaser = linePaser)
  }
}