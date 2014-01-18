package uk.co.samstokes.hibi

import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.app.Activity

class TaskListFragment extends Fragment {
  
  trait Callbacks {}
  
  private var mListener: Option[Callbacks] = None
  
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