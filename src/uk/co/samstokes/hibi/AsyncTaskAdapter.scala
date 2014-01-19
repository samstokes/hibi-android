package uk.co.samstokes.hibi

import android.os.AsyncTask

abstract class AsyncTaskAdapter[Params, Progress, Result] extends AsyncTask[AnyRef, Progress, Result] {

  final override def doInBackground(params: AnyRef*): Result = {
    val typedParams = params.map(_.asInstanceOf[Params])
    return pleaseDoInBackground(typedParams: _*)
  }
  
  def pleaseDoInBackground(params: Params*): Result
  
}