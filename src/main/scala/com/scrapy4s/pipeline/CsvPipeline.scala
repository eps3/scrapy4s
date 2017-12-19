package com.scrapy4s.pipeline

import com.scrapy4s.http.Response

/**
  * 存入Csv文件，其实Csv和行数据的区别只在于是否有标题栏
  *
  * Created by sheep3 on 2017/12/19.
  */
class CsvPipeline(header: String,
                  filePath: String,
                  linePaser: Response => Option[String]) extends LineFilePipeline(filePath, linePaser) {
  writer.write(s"$header\n")
}

object CsvPipeline {
  def apply(header: String,
            filePath: String)
           (implicit linePaser: Response => Option[String] = r => Some(s"${r.body}")): CsvPipeline = new CsvPipeline(header, filePath, linePaser)
}