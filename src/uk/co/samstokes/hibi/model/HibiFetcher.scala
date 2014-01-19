package uk.co.samstokes.hibi.model

import java.net.URL
import javax.net.ssl.HttpsURLConnection
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import org.json.JSONArray
import org.json.JSONTokener
import javax.net.ssl.X509TrustManager
import javax.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import uk.co.samstokes.hibi.FoolishTrustManager
import android.util.Log
import android.net.Uri
import java.net.Authenticator
import java.net.PasswordAuthentication
import android.util.Base64
import org.json.JSONException
import java.io.IOException

object HibiFetcher {
    val ENDPOINT = "https://hibi.samstokes.co.uk/api"
    val RESOURCE_TASKS = "/tasks"
      
    val HACKY_HARDCODED_USERNAME = "TODO_EDIT_ME"
    val HACKY_HARDCODED_PASSWORD = "TODO_EDIT_ME"
}

class HibiFetcher {
  
	val TAG = classOf[HibiFetcher].getSimpleName()
	
    private def getUrlBytes(urlSpec: String): Either[String, Array[Byte]] = {
        val url = new URL(urlSpec)
        val connection = foolishlyOpenConnection(url)
        val authz = HibiFetcher.HACKY_HARDCODED_USERNAME + ":" + HibiFetcher.HACKY_HARDCODED_PASSWORD
        connection.setRequestProperty("Authorization", "Basic " +
            new String(Base64.encode(authz.getBytes("UTF-8"), Base64.NO_WRAP), "UTF-8"))
        
        try {
        	val out = new ByteArrayOutputStream()
          
        	if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
        		Log.e(TAG, "Failed to fetch URL: response code " + connection.getResponseCode())
        		return Left("got %d response" format connection.getResponseCode())
        	}
        	val in = connection.getInputStream()
          
			var bytesRead = Integer.MAX_VALUE
			var buffer = new Array[Byte](1024)
			while (bytesRead > 0) {
				bytesRead = in.read(buffer)
				if (bytesRead > 0) out.write(buffer, 0, bytesRead)
			}
        	out.close()
        	Right(out.toByteArray())
        } catch {
          case ioe: IOException => Left("Error talking to server: " + ioe)
        } finally {
        	connection.disconnect()
        }
    }
    
    private def getUrlJson(urlSpec: String): Either[String, JSONArray] =
      getUrlBytes(urlSpec).right.flatMap {bytes => try { Right(
    	new JSONTokener(new String(bytes, "UTF-8")).nextValue().asInstanceOf[JSONArray]
      )} catch {
        case j: JSONException => Left("got invalid JSON: " + j)
        case cce: ClassCastException => Left("didn't get the JSON I expected: " + cce)
	  }}
    
    private def getTasksJson(): Either[String, JSONArray] =	getUrlJson(hibiTasksUrl)
    
    def getTasks(): Either[String, Seq[Task]] = {
    	val tasksArrayOpt = getTasksJson()
    	val taskObjectsOpt = tasksArrayOpt.right.flatMap {tasksArray => try { Right(
    	  	for (i <- 0 to tasksArray.length - 1)
    	  		yield tasksArray.getJSONObject(i)
    	)} catch {
    	  case j: JSONException => Left("something odd in the JSON: " + j)
    	}}
    	taskObjectsOpt.right.flatMap {taskObjects => try { Right (
    	    taskObjects.map(Task.fromJson(_))
    	)} catch {
    	  case j: JSONException => Left("couldn't parse task from JSON: " + j)
    	}}
    }
  
    private def foolishlyOpenConnection(url: URL): HttpsURLConnection = {
    	val sslContext = SSLContext.getInstance("TLS")
		sslContext.init(null, Array(new FoolishTrustManager), null)
		val connection = url.openConnection().asInstanceOf[HttpsURLConnection]
		connection.setSSLSocketFactory(sslContext.getSocketFactory())
		connection
    }
  
    private def hibiTasksUrl: String = HibiFetcher.ENDPOINT + HibiFetcher.RESOURCE_TASKS

  
}