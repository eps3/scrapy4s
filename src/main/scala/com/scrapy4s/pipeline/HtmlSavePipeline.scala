package com.scrapy4s.pipeline

import java.io.{File, FileWriter}

import com.scrapy4s.http.Response
import com.scrapy4s.util.HashUtil


/**
  * Created by sheep3 on 2017/11/28.
  */
abstract class HtmlSavePipeline(fileDir: String) extends SingleThreadPipeline {

//  val dir = new File(fileDir).getCanonicalPath

  def fileName(response: Response): String

  override def execute(response: Response): Unit = {
    val file = new File(fileDir , fileName(response))
    val writer = new FileWriter(file)
    writer.write(response.body)
    writer.close()
    logger.info(s"save to -> ${file.getAbsolutePath} ")
  }
}

object HtmlSavePipeline {
  def apply(fileDir: String)(implicit p: Response => String = r => HashUtil.getHash(r.request.toString)): HtmlSavePipeline = {
    new HtmlSavePipeline(fileDir) {
      override def fileName(response: Response) = {
        p(response)
      }
    }
  }

}