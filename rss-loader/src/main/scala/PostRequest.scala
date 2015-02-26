import java.io.{BufferedReader, InputStreamReader}
import java.net.URL
import sun.net.www.protocol.http.HttpURLConnection

abstract class RequestResult
case class DocExists(url: String) extends RequestResult
case class OK(url: String) extends RequestResult

object PostRequest {
  def send(url: String, json: String) = {
    val data   = json.getBytes("UTF8")
    val urlObj = new URL(url)
    val conn   = urlObj.openConnection.asInstanceOf[HttpURLConnection]

    try {
      init(conn)
      writeRequest(conn, data)
      val resp = getResponse(conn)
      resp match {
        case "exists" => DocExists(url)
        case "ok"     => OK(url)
        case x        => throw new Exception(x)
      }
    } finally {
      conn.disconnect
    }
  }
  
  private def init(conn: HttpURLConnection): Unit = {
    conn.setDoInput(true)
    conn.setDoOutput(true)
    conn.setUseCaches(false)
    conn.setInstanceFollowRedirects(false)
    conn.addRequestProperty("Content-Type", "application/json")
    conn.setRequestMethod("POST")
  }
  
  private def writeRequest(conn: HttpURLConnection, data: Array[Byte]): Unit = {
    conn.setRequestProperty("Content-Length", data.length.toString)
    common.using(conn.getOutputStream) {
      stream =>
        stream.write(data)
        stream.flush
    }
  }
  
  private def getResponse(conn: HttpURLConnection): String = {
    common.usage(conn.getInputStream) {
      is => common.usage(new InputStreamReader(is)) {
        isr => common.usage(new BufferedReader(isr)) {
          br =>
            val resp = new StringBuffer
            var line = br.readLine
            while (line != null) {
              if (resp.length > 0) 
                resp.append('\r')
              resp.append(line)
              line = br.readLine
            }

            resp.toString
        }
      }
    }
  }
}
