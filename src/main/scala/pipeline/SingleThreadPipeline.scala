package pipeline

import scalaj.http.HttpResponse

/**
  * Created by sheep3 on 2017/11/28.
  */
abstract class SingleThreadPipeline[T] extends MultiThreadPipeline[T](1) {
}

object SingleThreadPipeline {
  def apply[T](p: (T, HttpResponse[String]) => Unit): SingleThreadPipeline[T] = {
    (t: T, response: HttpResponse[String]) => p(t, response)
  }
}
