import org.apache.log4j.BasicConfigurator
;
/**
 * Created by Александр on 15.02.2015.
 */


object Example {
  def main(args: Array[String]): Unit = {
    BasicConfigurator.configure();
    val l=new WorkWithServer()
    val a:ParseRSS=new ParseRSS()
    val array=a.getBlockInfo("http://www.gazeta.ru/export/rss/politics.xml")
    val p=l.CreateJSONFile(array._1)
//    val LOG:Logger=Logger.getLogger(Example.getClass);
//    Logger.getLogger("dfdf").info("Post reauest")
//    p.foreach(i=>l.PostRequest(i))
    l.PostRequest(p(1))
    l.get();
  }
}
