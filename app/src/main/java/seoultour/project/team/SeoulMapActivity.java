//	구글맵을 이용하여 지도를 보여주는 액티비티
//
//	터치된 테마의 값을 꺼내와서 db를 열어 위도, 경도의 좌표값, 정보, 링크 등을 읽어
//
//	오버레이 아이템들을 만들고 오버레이를 생성하여 등록함
//
//	현재위치를 찾음, 버튼을 이용한 현재 위치로 이동 기능

package seoultour.project.team;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SeoulMapActivity extends MapActivity {
	private List<Overlay> overlays;
	private LocationListener mNetworkLocationListener;
	private LocationListener mGpsLocationListener;
	private LocationManager mGpsLocationManager;
	private LocationManager mNetworkLocationManager;
	private int mGpsStatus = LocationProvider.OUT_OF_SERVICE;
	private MapView mapView;
	private MapController mapController;
	private GeoPoint mCurrentGeoPoint;
	private Geocoder mGeoCoder;
	private boolean findCurrentLocation = false;
	private List<DBRecord> mDBRecordList = null;
	private DBRecord mDBRecord;
	private String mName;
	// private String mImageURL;
	private Iterator<DBRecord> mDBRecordIterator;
	private DBHandler mDBHandler;
	private String mTheme;
	private double mLongitude;
	private double mLatitude;
	private SeoulMapOverlay mMapOverlay;
	private String mAbout;
	protected String mAddressString;
	protected Context mContext;
	private MyLocationOverlay mMyLocationOverlay;
	private boolean mNoProvider = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seoulmap);

		overridePendingTransition(android.R.anim.slide_in_left, 0);

		mContext = getBaseContext();
		mTheme = getIntent().getExtras().getString("theme");

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		// 위치 서비스를 관리할 메니져 2개
		// GPS, NETWORK 두가지 위치 정보를 따로 관리
		mGpsLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		mNetworkLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		// NETWORK 위치 리스너
		// GPS STATUS가 이용가능하지 않을때만 위치 최신화
		// GPS가 켜져있지 않거나, 건물안에 들어왔을때 등 일시적으로 GPS 신호가 잡히지 않을때는 NETWORK 신호 이용
		// 위도와 경도를 이용 주소를 알아냄
		mNetworkLocationListener = new LocationListener() {

			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub

				if (location != null
						&& !(mGpsStatus == LocationProvider.AVAILABLE)) {
					double latitude, longitude;
					latitude = location.getLatitude();
					longitude = location.getLongitude();

					mGeoCoder = new Geocoder(getBaseContext(), Locale.KOREA);
					try {
						List<Address> addresses = mGeoCoder.getFromLocation(
								latitude, longitude, 1);
						Address address = addresses.get(0);
						mAddressString = address.getAddressLine(0);
						// Toast.makeText(getBaseContext(), "현재 위치 : " +
						// mAddressString , Toast.LENGTH_LONG).show();
						mCurrentGeoPoint = new GeoPoint((int) (latitude * 1E6),
								(int) (longitude * 1E6));

						findCurrentLocation = true;

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

		};

		// GPS 위치 리스너
		// 위도와 경도를 이용 주소를 알아냄
		mGpsLocationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				if (location != null) {
					double latitude, longitude;
					latitude = location.getLatitude();
					longitude = location.getLongitude();

					mGeoCoder = new Geocoder(getBaseContext(), Locale.KOREA);
					try {
						List<Address> addresses = mGeoCoder.getFromLocation(
								latitude, longitude, 1);
						Address address = addresses.get(0);
						mAddressString = address.getAddressLine(0);
						// Toast.makeText(getBaseContext(), "현재 위치 : " +
						// mAddressString , Toast.LENGTH_LONG).show();
						mCurrentGeoPoint = new GeoPoint((int) (latitude * 1E6),
								(int) (longitude * 1E6));

						findCurrentLocation = true;

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				mGpsStatus = status;
			}

		};

		// 현재 허용된 위치 프로바이더중에서
		// GPS, NETWORK 중 허용된 프로바이더를 등록함
		// 둘다 허용되어있지 않다면 현재 위치를 확인하려 할 때 설정을 물어보는 액티비티로 이동
		List<String> providers = mGpsLocationManager.getProviders(true);

		if (providers.contains("gps") && providers.contains("network")) {
			// GPS, NETWORK 위치 프로바이더 둘다 있을때
			mGpsLocationManager
					.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
							3, mGpsLocationListener);
			mNetworkLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 5000, 3,
					mNetworkLocationListener);
			mNoProvider = false;

		} else if (providers.contains("gps")) {
			// GPS 위치 프로바이더만 있을때
			mGpsLocationManager
					.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
							3, mGpsLocationListener);
			mNoProvider = false;

		} else if (providers.contains("network")) {
			// NETWORK 위치 프로바이더만 있을때
			mNetworkLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 5000, 3,
					mNetworkLocationListener);
			mNoProvider = false;

		} else {
			// GPS, NETWORK 위치 프로바이더 둘다 없을때

			mNoProvider = true;

		}

		// mGpsLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		// 5000, 5, mNetworkLocationListener);

		mapController = mapView.getController();
		mapController.setZoom(14);

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.marker2);
		Bitmap resized = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
		Drawable drawable = new BitmapDrawable(resized);

		// 커스텀 오버레이 생성
		mMapOverlay = new SeoulMapOverlay(drawable, this, mapView);

		// DB 핸들러 초기화
		initDBHandler();
		// DB 해석
		interpretDB();

		Button moveCurrentLocation = (Button) findViewById(R.id.movecurrentlocation);
		moveCurrentLocation.setText("현재 위치로 이동");

		// GPS, NETWORK 프로바이더, 둘 다 허용되어있지 않다면 현재 위치를 확인하려 할 때 설정을 물어보는 액티비티로 이동
		moveCurrentLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (findCurrentLocation == true) {
					Toast.makeText(getBaseContext(),
							"현재 위치 : " + mAddressString, Toast.LENGTH_SHORT)
							.show();
					mapController.animateTo(mCurrentGeoPoint);
				}

				if (mNoProvider == true) {
					startActivity(new Intent(mContext,
							LocationSettingRequest.class));
					finish();
				}
			}

		});

		// 나침반과 현재 위치 오버레이추가(구글에서 제공하는 API)
		mMyLocationOverlay = new MyLocationOverlay(this, mapView);
		mMyLocationOverlay.enableCompass();
		mMyLocationOverlay.enableMyLocation();

		// 커스텀 오버레이 추가
		overlays = mapView.getOverlays();
		overlays.add(mMapOverlay);
		overlays.add(mMyLocationOverlay);
	}

	private void initDBHandler() {
		// DB 핸들러 초기화
		// 저장된 DB를 읽음
		mDBHandler = new DBHandler(this);
		mDBHandler.setTheme(mTheme);
		mDBHandler.readDB();
		mDBRecordList = mDBHandler.getmDBRecordList();
	}

	private void interpretDB() {
		// DB 해석
		// DB 레코드를 하나씩 읽으면서 오버레이 아이템을 만들고 오버레이에 추가
		mDBRecordIterator = mDBRecordList.iterator();

		for (int i = 0; i < mDBRecordList.size(); i++) {
			mDBRecord = mDBRecordIterator.next();

			mName = mDBRecord.getName();
			mLatitude = mDBRecord.getLatitude();
			mLongitude = mDBRecord.getLongitude();
			mAbout = mDBRecord.getAbout();
			// mImageURL = mDBRecord.getImageurl();

			String tTitle = mName + "splitspace" + "\n" + mAbout;

			GeoPoint gp = new GeoPoint((int) (mLatitude * 1E6),
					(int) (mLongitude * 1E6));
			OverlayItem overlayItem = new OverlayItem(gp, tTitle, mTheme);

			mMapOverlay.addOverlayItem(overlayItem);

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		mGpsLocationManager.removeUpdates(mNetworkLocationListener);
		mNetworkLocationManager.removeUpdates(mNetworkLocationListener);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onBackPressed() {
		this.finish();
		overridePendingTransition(0, android.R.anim.slide_out_right);
	}

	public GeoPoint getCurrentLocation() {
		// TODO Auto-generated method stub
		return mCurrentGeoPoint;
	}

}