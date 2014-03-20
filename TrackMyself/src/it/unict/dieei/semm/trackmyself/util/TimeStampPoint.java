/**
 * 
 */
package it.unict.dieei.semm.trackmyself.util;

public class TimeStampPoint extends Point {

	private long timestamp;

	public TimeStampPoint(double lat, double lon, long timestamp) {
		super(lat, lon);
		this.timestamp = timestamp;
	}

	public TimeStampPoint() {
		super();
	}

	public TimeStampPoint(double lat, double lon) {
		super(lat, lon);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeStampPoint other = (TimeStampPoint) obj;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimeStampPoint [timestamp=" + timestamp + "] " + super.toString();
	}
	
	
	
}
