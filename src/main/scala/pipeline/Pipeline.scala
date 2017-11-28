package pipeline

import scalaj.http.HttpResponse

/**
  * 数据管道
  */
trait Pipeline[T] {
  def pipe(t: T, response: HttpResponse[String])

  def close()
}
