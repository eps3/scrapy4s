package com.scrapy4s.pipeline

import java.io.{File, FileOutputStream}

import com.scrapy4s.http.Response
import com.scrapy4s.util.HashUtil
import org.slf4j.LoggerFactory


/**
  * Created by sheep3 on 2017/11/30.
  */
class FileDumpPipeline(fileDir: String, fileNamePaser: Response => String) extends Pipeline {
  val logger = LoggerFactory.getLogger(classOf[FileDumpPipeline])

  override def pipe(response: Response): Unit = {
    val file = new File(fileDir, fileNamePaser(response))
    val outStream = new FileOutputStream(file)
    val buffer = new Array[Byte](response.inputStream.available())
    response.inputStream.read(buffer)
    outStream.write(buffer)
    outStream.close()
    logger.info(s"save to -> ${file.getAbsolutePath} ")
  }

}

object FileDumpPipeline {
  def apply(fileDir: String)
           (implicit p: Response => String = r => HashUtil.getHash(r.request.toString)): FileDumpPipeline = {
    new FileDumpPipeline(fileDir, p)
  }

}