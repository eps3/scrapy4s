package com.scrapy4s.pipeline

import java.io.{File, FileOutputStream}

import com.scrapy4s.http.Response
import com.scrapy4s.util.HashUtil


/**
  * Created by sheep3 on 2017/11/30.
  */
abstract class FileDumpPipeline [T](fileDir: String) extends SingleThreadPipeline[T] {

  def fileName(t: T, response: Response): String

  override def execute(t: T, response: Response): Unit = {
    val file = new File(fileDir , fileName(t, response))
    val outStream = new FileOutputStream(file)
    val buffer = new Array[Byte](response.inputStream.available())
    response.inputStream.read(buffer)
    outStream.write(buffer)
    outStream.close()
    logger.info(s"save to -> ${file.getAbsolutePath} ")
  }

}

object FileDumpPipeline {
  def apply[T](fileDir: String)(implicit p: (T, Response) => String = (_: T, r: Response) => HashUtil.getHash(r.request.toString)): FileDumpPipeline[T] = {
    new FileDumpPipeline[T](fileDir) {
      override def fileName(t: T, response: Response) = {
        p(t, response)
      }
    }
  }

}