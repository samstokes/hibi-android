package uk.co.samstokes.hibi

import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import uk.co.samstokes.hibi.model.Task

class TaskAdapter(fragment: Fragment, tasks: java.util.List[Task])
	extends ArrayAdapter[Task](fragment.getActivity(), 0x0, tasks) {
  
  override def getView(position: Int, convertView: View, parent: ViewGroup) = {
      val v = if (convertView == null) {
        fragment.getActivity().getLayoutInflater().inflate(R.layout.list_item_task, parent, false)
      } else convertView
      
      val task = getItem(position)
      
      val titleText = v.findViewById(R.id.task_list_item_title).asInstanceOf[TextView]
      titleText.setText(task.title)
      val doneCheck = v.findViewById(R.id.task_list_item_doneCheckBox).asInstanceOf[CheckBox]
      doneCheck.setChecked(task.isDone)
      
      v
    }

}