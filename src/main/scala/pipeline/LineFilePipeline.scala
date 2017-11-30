package pipeline

import java.io.FileWriter

import http.Response

class LineFilePipeline[T](
                           filePath: String,
                           threadCount: Int = 1,
                           linePaser: (T, Response) => String = (t: T, _: Response) => s"$t"
                         ) extends MultiThreadPipeline[T](threadCount) {

  val writer = new FileWriter(filePath)

  override def execute(t: T, response: Response): Unit = {
    val line = linePaser(t, response)
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
  def apply[T](filePath: String, threadCount: Int = 1)(implicit linePaser: (T, Response) => String = (t: T, _: Response) => s"$t"): LineFilePipeline[T] = {
    new LineFilePipeline[T](filePath, threadCount = threadCount, linePaser = linePaser)
  }
}