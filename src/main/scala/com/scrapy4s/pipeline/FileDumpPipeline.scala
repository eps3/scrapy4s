package com.scrapy4s.pipeline

import java.io.{File, FileOutputStream}

import com.scrapy4s.http.Response
import com.scrapy4s.util.HashUtil


/**
  * Created by sheep3 on 2017/11/30.
  */
abstract class FileDumpPipeline(fileDir: String) extends SingleThreadPipeline {

  def fileName(response: Response): String

  override def execute(response: Response): Unit = {
    val file = new File(fileDir , fileName(response))
    val outStream = new FileOutputStream(file)
    val buffer = new Array[Byte](response.inputStream.available())
    response.inputStream.read(buffer)
    outStream.write(buffer)
    outStream.close()
    logger.info(s"save to -> ${file.getAbsolutePath} ")
  }

}

object FileDumpPipeline {
  def apply(fileDir: String)(implicit p: Response => String = r => HashUtil.getHash(r.request.toString)): FileDumpPipeline = {
    new FileDumpPipeline(fileDir) {
      override def fileName(response: Response) = {
        p(response)
      }
    }
  }

}