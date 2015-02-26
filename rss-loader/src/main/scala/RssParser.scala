import java.net.URL
import com.sun.syndication.feed.synd.SyndEntry
import com.sun.syndication.io.{SyndFeedInput, XmlReader}
import org.joda.time.DateTime
import scala.collection.mutable.ListBuffer

object RssParser {
  def loadItems(rssFeedUrl: String) = {
    val url = new URL(rssFeedUrl)
    common.usage(new XmlReader(url)){
      reader =>
        val feed  = new SyndFeedInput().build(reader)
        val items = new ListBuffer[RSSItem]
        val it    = feed.getEntries.iterator
        while(it.hasNext){
          val entry = it.next.asInstanceOf[SyndEntry]
          val item  = getRSSItem(entry)
          items += item
        }
        
        items
    }
  }

  private def getRSSItem(entry: SyndEntry) = {
    val url = entry.getLink
    val title = entry.getTitle
    val publishedDate = entry.getPublishedDate
    val pubDate = publishedDate match {
      case null => None
      case x    => Some(new DateTime(x))
    }
    val loadDate = new DateTime
    val description = entry.getDescription match {
      case null => ""
      case x    => x.getValue
    }

    new RSSItem(url, title, description, pubDate, loadDate)
  }
}