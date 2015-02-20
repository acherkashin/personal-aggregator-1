import java.io.{FileReader, BufferedReader, File, PrintWriter}

import org.apache.log4j.BasicConfigurator

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import org.joda.time.DateTime

object ScalaRSSParse {
  def main(args: Array[String]): Unit = {
    val ONE = 1
    val SECOND = 1000
    var dt = new DateTime()
    var dtOld = new DateTime()
    var plusPeriod = dt.plusHours(ONE)
    writeInFile("Error.log", "Список ошибок:")
    val arraybufURL = new ArrayBuffer[String]()
    val arrayFilesRSS = getNameFiles("RSS")
    for(i <- 0 until arrayFilesRSS.length){
      arraybufURL ++= Source.fromFile(arrayFilesRSS(i).getPath, "UTF-8").getLines().toArray.toBuffer
    }
    val arrayURL = arraybufURL.toArray
    var mapWebSites = new scala.collection.mutable.HashMap[String , scala.collection.mutable.HashMap[String, BlockInfo]]
    mapWebSites = update(arrayURL, mapWebSites)
    while (true) {
      if (timeDifferenceMoreHour(dt, dtOld)) {
        mapWebSites = update(arrayURL, mapWebSites)
        dtOld = new DateTime()
      }
      Thread.sleep(SECOND)
      dt = new DateTime()
    }
  }

  def update(arrayURL: Array[String],mapWebSites : scala.collection.mutable.HashMap[String , scala.collection.mutable.HashMap[String, BlockInfo]]): (scala.collection.mutable.HashMap[String , scala.collection.mutable.HashMap[String, BlockInfo]]) = {
    for (i <- 0 until arrayURL.length) {
      var mapArticle = new scala.collection.mutable.HashMap[String,BlockInfo]
      val url = arrayURL(i)
      if(mapWebSites.contains(url))
        mapArticle = mapWebSites(url)
      val collection = scalaRSSParser(url, mapArticle)
      if (!collection._1)
        updateFile("Error.log","Ошибка обновления "+(new DateTime().toString)+ ". Адресс сайта: " + arrayURL(i).toString)
      mapWebSites += ( url.toString -> collection._2 )
    }
    println("Обновление заверншено.")
    (mapWebSites)
  }

  def timeDifferenceMoreHour(dt: DateTime, dtOld: DateTime): Boolean = {
    val ONE = 1
    if (dt.minusHours(ONE).getHourOfDay >= dtOld.getHourOfDay && dt.minusHours(ONE).getDayOfYear >= dtOld.getDayOfYear
      && dt.minusHours(ONE).getYear >= dtOld.getYear)
      true
    else
      false
  }

  def scalaRSSParser(url: String, oldMap : scala.collection.mutable.HashMap[String , BlockInfo]):
  (Boolean,scala.collection.mutable.HashMap[String , BlockInfo]) = {
    val temp = new ParseRSS()
    val arrayPlus = temp.getBlockInfo(url)
    val arrayArticle = arrayPlus _1
    val mapArticle = new scala.collection.mutable.HashMap[String , BlockInfo]
    try {
      for (i <- 0 until arrayArticle.length) {
        mapArticle += ((arrayArticle(i).url.toString) -> arrayArticle(i))
      }
      if (arrayPlus._2) {
        for (i <- 0 until arrayArticle.length)
          if (!oldMap.contains(arrayArticle(i).url.toString)) {
            //отправка на сервер
            BasicConfigurator.configure
            val  workWithServer = new WorkWithServer()
            val arrayJSON = workWithServer.CreateArrayJSONFile(arrayArticle)
            for( i <- 0 until arrayJSON.length)
              workWithServer.PostRequest(arrayJSON(i))
            println("отправлено")
          }
        (true, mapArticle)
      }
      else {
        (false, oldMap)
      }
    }
    catch {
      case _ : Exception => (false, oldMap)
    }
  }

  def readFromFile(fileName: String): String = {
    val sourse = Source.fromFile(fileName,"UTF-8")
    sourse.mkString
  }

  def updateFile(nameFile: String, newText: String) {
      val oldText = readFromFile(nameFile)
      val out = new PrintWriter(nameFile)
      out.println(oldText)
      out.print(newText)
      out.close
  }

  def writeInFile(nameFile : String, newText : String) = {
    val out = new PrintWriter(nameFile)
    out.print(newText)
    out.close
  }

  def getNameFiles(way: String): Array[File] = {
    val F: File = new File(way)
    val fList: Array[File] = F.listFiles()
    fList
  }

}