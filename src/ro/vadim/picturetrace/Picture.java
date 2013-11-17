package ro.vadim.picturetrace;

public class Picture {
	public String url = "";
	public String description = "";
	public double latitude = 0.0;
	public double longitude = 0.0;
	
	public Picture(String newUrl, String newDescription, double newLatitude, double newLongitude) {
		this.url = newUrl;
		this.description = newDescription;
		this.latitude = newLatitude;
		this.longitude = newLongitude;
	}
}
