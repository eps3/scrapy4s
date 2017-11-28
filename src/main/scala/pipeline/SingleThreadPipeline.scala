package pipeline

/**
  * Created by sheep3 on 2017/11/28.
  */
object SingleThreadPipeline {
  def apply[T](p: T => Unit): MultiThreadPipeline[T] = MultiThreadPipeline(1)(p)
}
