namespace java timetracker.generated

enum EntryType{
	READING = 1
	PLAYING = 2
}

struct TrackerEntry{
	1: EntryType type
	2: i64 startTimestampMillis
	3: optional i64 stopTimestampMillis
	4: optional i16 multiplier
}

service TimeTrackerService{
	bool saveEntry(1: TrackerEntry entry)
	list<TrackerEntry> getEntries()
	i64 getAvailablePlaytime()
}

const i16 PORT = 7911 