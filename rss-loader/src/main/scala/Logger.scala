import java.io.FileWriter
import java.nio.file.Paths

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object Logger {
  val displayDateTimeFormat = "yyyy.MM.dd HH:mm:ss"
  val displayDateTimeFormatter = DateTimeFormat.forPattern(displayDateTimeFormat)
}

class Logger(name: String) {
  private val homeDir    = System.getProperty("user.dir")
  private val fileName   = s"$name.log"
  private val pathToFile = Paths.get(homeDir, fileName).toString
  private val locker     = new Object
  
  def write(msg: String): Unit = {
    locker.synchronized{
      common.using(new FileWriter(pathToFile, true)){
        fw => formatMessage(fw, msg)
      }
    }
  }
  
  private def formatMessage(fw: FileWriter, msg: String) = {
    val time = DateTime.now.toString(Logger.displayDateTimeFormatter)
    fw.write(s"$time\n$msg\n\n")
  }
  
  override def toString = name
}
