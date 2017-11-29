package pipeline
import java.io.FileWriter

import http.Response

class LineFilePipeline[T](
                           filePath: String,
                           linePaser: (T, Response) => String = (t: T,_: Response) => s"$t"
                         ) extends SingleThreadPipeline[T] {

  val writer = new FileWriter(filePath)

  override def execute(t: T, response: Response): Unit = {
    writer.write(s"${linePaser(t, response)}\n")
  }

  override def shutdownHook(): Unit = {
    writer.close()
  }
}
object LineFilePipeline {
  def apply[T](filePath: String)(implicit linePaser: (T, Response) => String = (t: T,_: Response) => s"$t"): LineFilePipeline[T] = {
    new LineFilePipeline[T](filePath, linePaser)
  }
}