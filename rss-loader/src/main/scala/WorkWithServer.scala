import java.net.URL
import java.util
import sun.net.www.protocol.http.HttpURLConnection

import io.Source._
import com.google.gson.Gson
import org.apache.http._
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair

class WorkWithServer(val url:String="http://188.226.178.169:3000") {

  def CreateJSONFile(elem:BlockInfo):String={
    val jsonFromElem = new Gson().toJson(elem)
    jsonFromElem
  }

  def CreateArrayJSONFile(elem:Array[BlockInfo]):Array[String] ={
    val map=elem.map(i=>CreateJSONFile(i))
    map
  }

  def PostRequest(json:String):Unit={

    // add name value pairs to a post object
    val post = new HttpPost(url)
    val nameValuePairs = new util.ArrayList[NameValuePair]()
    nameValuePairs.add(new BasicNameValuePair("JSON", json))
    post.setEntity(new UrlEncodedFormEntity(nameValuePairs))

    // send the post request
    val client = new DefaultHttpClient
    val response = client.execute(post)
    response.getAllHeaders
  }

}
