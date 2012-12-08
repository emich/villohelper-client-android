package be.emich.labs.villohelper.fragment;

import be.emich.labs.villohelper.activity.MainActivity;
import be.emich.labs.villohelper.adapter.SimpleStationCursorAdapter;
import be.emich.labs.villohelper.adapter.StationCursorAdapter;
import be.emich.labs.villohelper.exception.ErrorType;
import be.emich.labs.villohelper.task.GetAllStationsTask.OnStationsTaskCompletedListener;
import be.emich.labs.villohelper.task.GetAllStationsTask;
import be.emich.labs.villohelper.task.GetSpecificStationsTask;

public class FavoriteStationsListFragment extends StationsListFragment implements OnStationsTaskCompletedListener {
	@Override
	protected SimpleStationCursorAdapter getAdapter() {
		return new StationCursorAdapter(getActivity(), null);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		GetSpecificStationsTask task = new GetSpecificStationsTask(getApp().getCurrentSystem(), getApp().getLanguage(), getApp().getDataHelper().getFavorites(getApp().getCurrentSystem()));
		task.setOnStationsTaskCompletedListener(this);
		task.execute();
	}
	
	@Override
	public void onStationsTaskCompleted(GetAllStationsTask task) {
		
	}
	
	@Override
	public void onStationsTaskFailed(GetAllStationsTask task,
			ErrorType errorType) {
		if(getActivity()!=null){((MainActivity)getActivity()).onStationsTaskFailed(task, errorType);}
	}
	
	
	
}
