package pipeline
import java.io.FileWriter

import http.Response

class LineFilePipeline[T](filePath: String) extends SingleThreadPipeline[T] {

  val writer = new FileWriter(filePath)

  override def execute(t: T, response: Response): Unit = {
    writer.write(s"$t\n")
  }

  override def shutdownHook(): Unit = {
    writer.close()
  }
}
object LineFilePipeline {
  def apply[T](filePath: String): LineFilePipeline[T] = new LineFilePipeline[T](filePath)
}