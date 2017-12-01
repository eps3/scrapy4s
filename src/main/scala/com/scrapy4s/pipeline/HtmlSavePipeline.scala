package com.scrapy4s.pipeline

import java.io.{File, FileWriter}

import com.scrapy4s.http.Response
import com.scrapy4s.util.HashUtil


/**
  * Created by sheep3 on 2017/11/28.
  */
class HtmlSavePipeline(fileDir: String, fileNamePaser: Response => String) extends Pipeline {

  override def pipe(response: Response): Unit = {
    val file = new File(fileDir, fileNamePaser(response))
    val writer = new FileWriter(file)
    writer.write(response.body)
    writer.close()
  }

}

object HtmlSavePipeline {
  def apply(fileDir: String)
           (implicit p: Response => String = r => HashUtil.getHash(r.request.toString)): HtmlSavePipeline = new HtmlSavePipeline(fileDir, p)

}