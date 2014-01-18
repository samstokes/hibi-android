package uk.co.samstokes.hibi;

import android.support.v4.app.Fragment;

public class TaskListActivity extends SingleFragmentActivity
	implements TaskListFragment.Callbacks {

	@Override
	protected Fragment createFragment() {
		return TaskListFragment.newInstance();
	}

}
