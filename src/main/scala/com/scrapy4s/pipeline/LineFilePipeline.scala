package com.scrapy4s.pipeline

import java.io.FileWriter

import com.scrapy4s.http.Response


class LineFilePipeline(
                        filePath: String,
                        threadCount: Int = 1,
                        linePaser: Response => String
                      ) extends MultiThreadPipeline(threadCount) {

  val writer = new FileWriter(filePath)

  override def execute(response: Response): Unit = {
    val line = linePaser(response)
    this.synchronized {
      writer.write(s"$line\n")
    }
  }

  override def shutdownHook(): Unit = {
    logger.info(s"save to -> $filePath")
    writer.close()
  }
}

object LineFilePipeline {
  def apply[T](filePath: String, threadCount: Int = 1)
              (implicit linePaser: Response => String = r => s"${r.body}"): LineFilePipeline = {
    new LineFilePipeline(filePath, threadCount = threadCount, linePaser = linePaser)
  }
}