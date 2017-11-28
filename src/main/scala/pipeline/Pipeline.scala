package pipeline

/**
  * 数据管道
  */
trait Pipeline[T] {
  def pipe(t: T)

  def close()
}
