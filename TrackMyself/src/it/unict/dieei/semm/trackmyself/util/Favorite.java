package it.unict.dieei.semm.trackmyself.util;

import java.io.Serializable;

public class Favorite implements Serializable{

    private String favoriteName;
    private long fromTime;
    private long toTime;
    public Favorite(){}
	public Favorite(String favoriteName, long fromTime, long toTime) {
		super();
		this.favoriteName = favoriteName;
		this.fromTime = fromTime;
		this.toTime = toTime;
	}
	public String getFavoriteName() {
		return favoriteName;
	}
	public void setFavoriteName(String favoriteName) {
		this.favoriteName = favoriteName;
	}
	public long getFromTime() {
		return fromTime;
	}
	public void setFromTime(long fromTime) {
		this.fromTime = fromTime;
	}
	public long getToTime() {
		return toTime;
	}
	public void setToTime(long toTime) {
		this.toTime = toTime;
	}
	@Override
	public String toString() {
		return "Favorite [favoriteName=" + favoriteName + ", fromTime=" + fromTime + ", toTime=" + toTime + "]";
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Favorite)) return false;
		else{
		 Favorite f = (Favorite) o;
		 if(f.getFavoriteName().equals(this.getFavoriteName())) return true;
		 
		}
		return false;
	}
    
	
}