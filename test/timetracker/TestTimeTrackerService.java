package timetracker;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.BeforeClass;
import org.junit.Test;

import timetracker.TimeTrackerServer;
import timetracker.generated.EntryType;
import timetracker.generated.TimeTrackerService;
import timetracker.generated.TrackerEntry;
import timetracker.generated.timetrackerConstants;

public class TestTimeTrackerService {
	
	@BeforeClass
	@SuppressWarnings({ "static-access"})
	public static void startServer() throws URISyntaxException, IOException {
		new Thread(new TimeTrackerServer()).start();
		try{
			Thread.sleep(100);
		} catch (InterruptedException e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testExample() throws TTransportException, TException{
		TTransport transport = new TSocket("localhost", timetrackerConstants.PORT);
		TProtocol protocol = new TBinaryProtocol(transport);
		TimeTrackerService.Client client = new TimeTrackerService.Client(protocol);
		transport.open();
		
		//Keep track of how much play time we should have
		long playTimeMillis = 0;
		Date testStart = new Date();
		Date now = testStart;
		
		List<String> actions = new ArrayList<String>();
		actions.add("R" + 25*60*1000);
		actions.add("P" + 10*60*1000);
		actions.add("P" + 5*60*1000);
		actions.add("P" + 20*60*1000);
		actions.add("R" + 2*1000);
		actions.add("P" + 5*1000);
		actions.add("R" + 11*60*1000);
		
		
		for(String s : actions){
			EntryType type = (s.charAt(0) == 'R'? EntryType.READING:EntryType.PLAYING);
			long time = Long.parseLong(s.substring(1));
			createEntry(type, client, now, playTimeMillis, time);
			if(type.equals(EntryType.READING)){
				playTimeMillis += time;
			}
			else{
				playTimeMillis -= time;
			}
			now = new Date(now.getTime() + time);
			
		}
		transport.close();
	}
	
	void createEntry(EntryType type, TimeTrackerService.Client client, Date now, long currentPlayTime, long duration) throws TException{	
		TrackerEntry entry = new TrackerEntry(type, now.getTime());
		
		//Check that play time is what we expect
		assertEquals(currentPlayTime, client.getAvailablePlaytime());
		
		//Try to add a start entry
		client.saveEntry(entry);
		
		//Retrieve the current set of entries and match with the one we sent
		List<TrackerEntry> entries = client.getEntries();
		assertEquals(entry, entries.get(entries.size() - 1));

		//Check that play time is same as when we started
		assertEquals(currentPlayTime, client.getAvailablePlaytime());
		
		//Finish after the given duration
		entry.stopTimestampMillis = entry.startTimestampMillis + duration;
		assertTrue(client.saveEntry(entry));

		//Retrieve the current set of entries and match with the one we sent
		assertEquals(entry, entries.get(entries.size() - 1));
		
		//Check that the play time was updated
		if(type.equals(EntryType.READING)){
			assertEquals(currentPlayTime + duration, client.getAvailablePlaytime());
		}
		else if(type.equals(EntryType.PLAYING)){
			assertEquals(currentPlayTime - duration, client.getAvailablePlaytime());
		}
	}
}
