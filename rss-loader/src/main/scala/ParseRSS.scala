import java.net.URL
import java.util

import com.sun.syndication.feed.synd.{SyndEntry, SyndFeed}
import com.sun.syndication.io.{SyndFeedInput, XmlReader}
import org.joda.time.DateTime

import scala.collection.mutable.ArrayBuffer


/**
  * Created by Александр on 04.02.2015.
  */
class ParseRSS() {

   def getInfo(entry:SyndEntry):BlockInfo={
     val dateLoad=new DateTime()
     val author=entry.getAuthor
     val title=entry.getTitle
     val description=entry.getDescription.getValue
     val url=entry.getLink
     val dateOfWriting=entry.getPublishedDate.toString

     val Info:BlockInfo=new BlockInfo(author,title,description,url,dateOfWriting,dateLoad)
     Info//return Info
   }

   def getBlockInfo(strURL:String): (Array[BlockInfo],Boolean) = {
     try {
     val url:URL=new URL(strURL:String)
     var reader:XmlReader = new XmlReader(url)
     val feed:SyndFeed = new SyndFeedInput().build(reader)
     val entries:util.List[_]=feed.getEntries//List of Entries
     val itEntries:util.Iterator[_]=entries.iterator()

     val arrbufBlockInfo=ArrayBuffer[BlockInfo]()//Array is empty

     while(itEntries.hasNext){
       val n=itEntries.next()//get
       val entry:SyndEntry=n.asInstanceOf[SyndEntry]//convert
       val oneInfo:BlockInfo=getInfo(entry)
       arrbufBlockInfo+=oneInfo
     }
   val arrBlockInfo=arrbufBlockInfo.toArray
     (arrBlockInfo,true)
     }
     catch {
       case _ : Exception => (new Array[BlockInfo](1), false)
     }
   }

 }


