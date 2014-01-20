package uk.co.samstokes.hibi

import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView.OnEditorActionListener
import android.view.KeyEvent
import android.widget.TextView

object Implicits {
    implicit def onClick(handler: View => Unit) = new OnClickListener() {
      override def onClick(source: View) = handler(source)
    }
    
    implicit def onEditorAction(handler: (TextView, Int, KeyEvent) => Boolean) = new OnEditorActionListener() {
      override def onEditorAction(view: TextView, actionId: Int, event: KeyEvent) =
        handler(view, actionId, event)
    }
}