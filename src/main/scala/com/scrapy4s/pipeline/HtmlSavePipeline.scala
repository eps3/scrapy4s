package com.scrapy4s.pipeline

import java.io.{File, FileWriter}

import com.scrapy4s.http.Response
import com.scrapy4s.util.HashUtil


/**
  * Created by sheep3 on 2017/11/28.
  */
abstract class HtmlSavePipeline[T](fileDir: String) extends SingleThreadPipeline[T] {

//  val dir = new File(fileDir).getCanonicalPath

  def fileName(t: T, response: Response): String

  override def execute(t: T, response: Response): Unit = {
    val file = new File(fileDir , fileName(t, response))
    val writer = new FileWriter(file)
    writer.write(response.body)
    writer.close()
    logger.info(s"save to -> ${file.getAbsolutePath} ")
  }
}

object HtmlSavePipeline {
  def apply[T](fileDir: String)(implicit p: (T, Response) => String = (_: T, r: Response) => HashUtil.getHash(r.request.toString)): HtmlSavePipeline[T] = {
    new HtmlSavePipeline[T](fileDir) {
      override def fileName(t: T, response: Response) = {
        p(t, response)
      }
    }
  }

}