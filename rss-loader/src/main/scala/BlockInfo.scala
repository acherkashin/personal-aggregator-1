import org.joda.time.DateTime

/**
 * Created by Александр on 05.02.2015.
 */
class BlockInfo(val author:String,val title:String,val description:String, val url:String, val dateOfWriting : String,
                val dateLoad : DateTime) {
  override def toString():String={
    val str =author+"\n"+title+"\n"+description+"\n"+url+"\n"+dateOfWriting+"\n  "+dateLoad.toString()
    str
  }
}
