package it.unict.dieei.semm.trackmyself.util;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

public class Point implements Serializable{
	private double longitude;
	private double latitude;
	public Point(double lat, double lon){
		this.latitude=lat;
		this.longitude=lon;
	}
	public Point(){}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	
	public LatLng getLatLng(){
		return new LatLng(latitude,longitude);
	}
	
	public void setLatLng(LatLng p){
		latitude=p.latitude;
		longitude=p.longitude;
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (Double.doubleToLongBits(latitude) != Double
				.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double
				.doubleToLongBits(other.longitude))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Point [longitude=" + longitude + ", latitude=" + latitude + "]";
	}
	public synchronized void setPoint(double lat, double lon){
		this.latitude=lat;
		this.longitude=lon;
	}
	public synchronized double[] getPoint(){
		return new double[]{latitude,longitude};
	}
	
}
