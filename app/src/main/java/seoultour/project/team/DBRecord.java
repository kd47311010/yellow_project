//  db 각 하나의 레코드 클래스
// 
//  db에서 읽은 레코드에 대응 됨

package seoultour.project.team;

//
// setter로 필드들을 저장
// 참조할때는 getter로 필드들을 참조
public class DBRecord {
	private String theme;
	private String name;
	private double latitude;
	private double longitude;
	private String location;
	private String imageurl;
	private String infourl;
	private String about;
	private String etc;

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	public String getInfourl() {
		return infourl;
	}

	public void setInfourl(String infourl) {
		this.infourl = infourl;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getEtc() {
		return etc;
	}

	public void setEtc(String etc) {
		this.etc = etc;
	}
}