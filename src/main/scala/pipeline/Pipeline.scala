package pipeline

import http.Response

import scalaj.http.HttpResponse

/**
  * 数据管道
  */
trait Pipeline[T] {
  def pipe(t: T, response: Response)

  def close()
}
