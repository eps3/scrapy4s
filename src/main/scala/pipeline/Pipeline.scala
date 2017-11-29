package pipeline

import http.Response


/**
  * 数据管道
  */
trait Pipeline[T] {
  def pipe(t: T, response: Response)

  def close()
}
