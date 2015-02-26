import org.apache.commons.lang3.StringEscapeUtils
import org.joda.time.DateTime

class RSSItem(val url: String, val title: String, val description: String,
              val pubDate: Option[DateTime], val loadDate: DateTime) {
  override def toString =
    s"url: $url\ntitle: $title\npub date: $pubDate\nload date: $loadDate\ndescription: $description"
  
  def toJson = {
    val sb = new StringBuilder
    sb.append("{\n")
    sb.append("  \"url\": \"" + StringEscapeUtils.escapeJson(url) + "\",\n")
    sb.append("  \"title\": \"" + StringEscapeUtils.escapeJson(title) + "\",\n")
    sb.append("  \"snippet\": \"" + StringEscapeUtils.escapeJson(description) + "\",\n")
    val time = pubDate match {
      case Some(date) => date
      case None       => loadDate
    }
    sb.append("  \"time\": \"" + time.toString + "\"\n")
    sb.append("}")
    
    sb.toString
  }
}
