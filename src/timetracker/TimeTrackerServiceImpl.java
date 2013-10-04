package timetracker;

import java.util.List;

import org.apache.thrift.TException;

import timetracker.generated.TimeTrackerService.Iface;
import timetracker.generated.TrackerEntry;

public class TimeTrackerServiceImpl implements Iface {

	@Override
	public boolean saveEntry(TrackerEntry entry) throws TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<TrackerEntry> getEntries() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getAvailablePlaytime() throws TException {
		// TODO Auto-generated method stub
		return 0;
	}

}
