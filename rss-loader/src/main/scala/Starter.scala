import java.io.File
import java.nio.file.Paths
import org.joda.time.DateTime

object Starter {
  private val postUrl    = "http://localhost:3000/insert-document"
  private val actionsLog = new Logger("actions")
  private val errorLog   = new Logger("errors")
  private val existsLog  = new Logger("exists")
  private val sentLog    = new Logger("sent")
  private val reloadDelaySec = 30*60

  def main(args: Array[String]): Unit = {
    try{
      val rssSettings = loadRssSettings
      val loaders = rssSettings.map(x => new Loader(x))

      infiniteLoop(loaders)
    }catch {
      case ex: Exception => logError(ex.getMessage, common.formatException(ex))
    }
  }
  
  private def infiniteLoop(loaders: Array[Loader]): Unit = {
    var lastLoad: Option[DateTime] = None
    while(true){
      if (shouldLoad(lastLoad)){
        loaders.foreach(load)
        lastLoad = Some(DateTime.now)
      }
      Thread.sleep(10000)
    }
  }
  
  private def shouldLoad(lastLoad: Option[DateTime]) = {
    lastLoad match {
      case None    => true
      case Some(x) => DateTime.now.isAfter(x.plusSeconds(reloadDelaySec))
    }
  }
  
  private def load(loader: Loader): Unit = {
    val loaderName = loader.toString
    actionsLog.write(s"Loading $loaderName")
    loader.links.foreach(link => {
      try{
        val items = RssParser.loadItems(link)
        items.foreach(item => {
          if (!loader.isLoaded(item.url)){
            loader.markAsLoaded(item.url)
            postItem(item)
          }
        })
      }catch {
        case ex: Exception =>
          logError(s"Exception in loader $loaderName\n${ex.getMessage}", common.formatException(ex))
      }
    })
  }

  private def postItem(item: RSSItem) {
    try{
      val res = PostRequest.send(postUrl, item.toJson)
      res match {
        case x: DocExists => existsLog.write(item.url)
        case x: OK        => sentLog.write(item.url)
      }
    }catch {
      case ex: Exception => logError(ex.getMessage, common.formatException(ex))
    }
  }

  private def loadRssSettings = {
    val homeDir = System.getProperty("user.dir")
    val path    = Paths.get(homeDir, "RSS").toString
    val catalog = new File(path)
    
    catalog.listFiles.filter(x => x.isFile).map(x => x.getAbsolutePath)
  }
  
  private def logError(msg: String, stackTrace: String): Unit = {
    errorLog.write(s"$msg\n$stackTrace")
  }
}