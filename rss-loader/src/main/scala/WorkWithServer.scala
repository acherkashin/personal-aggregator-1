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

/**
 * Created by Александр on 15.02.2015.
 */


class WorkWithServer(val url:String="http://localhost:3000/search") {

  def CreateJSONFile(elem:BlockInfo):String={
    val jsonFromElem = new Gson().toJson(elem)
    jsonFromElem
  }

  def CreateJSONFile(elem:Array[BlockInfo]):Array[String] ={
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
  def get(url: String=this.url,
          connectTimeout:Int =5000,
          readTimeout:Int =5000,
          requestMethod: String = "GET") = {
    val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(connectTimeout)
    connection.setReadTimeout(readTimeout)
    connection.setRequestMethod(requestMethod)
    val inputStream = connection.getInputStream
    val content = fromInputStream(inputStream).mkString
    if (inputStream != null) inputStream.close
    content
  }



  def GetRequest():Unit={
   // val request: http.HttpRequest = Http("http://localhost:3000/search")
   // println(request.asParamMap)
  }
}
