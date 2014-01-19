package uk.co.samstokes.hibi

import java.util.Date

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import uk.co.samstokes.hibi.model.Task

class TaskListFragment extends ListFragment {
  
  trait Callbacks {}
  
  private var mListener: Option[Callbacks] = None
  private var mTasks: Array[Task] = Array(
      Task("foo", None), Task("bar", Some(new Date)))
  
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
	setListAdapter(new TaskAdapter(this, mTasks))
  }
  
  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle) =
    inflater.inflate(R.layout.fragment_task_list, container, false)
    
  override def onAttach(activity: Activity) {
    super.onAttach(activity)
    try {
      mListener = Some(activity.asInstanceOf[Callbacks])
    } catch {
      case e: ClassCastException => throw new ClassCastException(
          activity.toString() + " must implement " + classOf[Callbacks].getName())
    }
  }
  
  override def onDetach() {
    super.onDetach()
    mListener = None
  }

}

object TaskListFragment {
  def newInstance(): TaskListFragment = new TaskListFragment()
}