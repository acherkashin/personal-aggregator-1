import scala.collection.mutable

class Loader(settingsPath: String) {
  private val loadedUrls = new mutable.HashSet[String]
  val links  = common.loadStringFromFile(settingsPath).split("\n").map(x => x.trim)
  
  def isLoaded(url: String) = loadedUrls.contains(url)
   
  def markAsLoaded(url: String): Unit = loadedUrls += url
  
  override def toString = settingsPath
}
