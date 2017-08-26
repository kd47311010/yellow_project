//   내위치 터치시 전환되는 액티비티
// 
//   카메라의 서페이스 뷰(카메라를 처리하는 전용 뷰)를 등록하고
//   
//   서페이스 뷰 위에 보여질 커스텀 뷰(카메라 오버레이 뷰)를 등록함
//   
//   현재위치를 찾고 찾은 현재위치를 카메라 오버레이 뷰에 알려줌
//   
//   이때 GPS, 네트워크를 이용하여 현재 위치를 찾음
//   
//   GPS가 이용가능하면 GPS 이용
//   
//   불가능하면 네트워크를 이용하여 현재위치를 찾음
//   
//   둘다 불가능하면 위치 정보 설정으로 이동을 물어보는 액티비티로 이동

package seoultour.project.team;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

public class CameraActivity extends MapActivity {
	/** Called when the activity is first created. */
	private CameraPreview mCameraPreview;
	private CameraOverlayView mOverlayView = null;
	private LocationListener mGpsLocationListener;
	private LocationListener mNetworkLocationListener;
	private LocationManager mGpsLocationManager;
	private LocationManager mNetworkLocationManager;
	private GeoPoint mCurrentGpsGeoPoint;
	private GeoPoint mCurrentNetworkGeoPoint;
	private Geocoder mGpsGeoCoder;
	private Geocoder mNetworkGeoCoder;
	private int mGpsStatus = LocationProvider.OUT_OF_SERVICE;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		overridePendingTransition(android.R.anim.slide_in_left, 0);

		// 제목 표시줄, 상태표시줄을 없애 전체화면으로 보여줌
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// 카메라 프리뷰와 카메라 프리뷰 위에 보여줄 카메라 오버레이뷰 생성
		mCameraPreview = new CameraPreview(this);
		mOverlayView = new CameraOverlayView(this);

		Display display = ((WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int height = display.getHeight();

		// 2:3 비율로 카메라 화면을 설정(보통의 카메라 사진 크기)
		mOverlayView.setOverlaySize((int) (height * 1.5), height);

		setContentView(mCameraPreview, new LayoutParams((int) (height * 1.5),
				height));
		addContentView(mOverlayView, new LayoutParams((int) (height * 1.5),
				height));

		// 위치 서비스를 관리할 메니져 2개
		// GPS, NETWORK 두가지 위치 정보를 따로 관리

		mGpsLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		// GPS 위치 리스너
		// 위도와 경도를 이용 주소를 알아냄

		mGpsLocationListener = new LocationListener() {

			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub

				try {

					if (location != null && mOverlayView != null) {
						double latitude, longitude;
						latitude = location.getLatitude();
						longitude = location.getLongitude();

						mGpsGeoCoder = new Geocoder(getBaseContext(),
								Locale.KOREA);

						List<Address> addresses = mGpsGeoCoder.getFromLocation(
								latitude, longitude, 1);
						Address address = addresses.get(0);
						String addressString = address.getAddressLine(0);

						mCurrentGpsGeoPoint = new GeoPoint(
								(int) (latitude * 1E6), (int) (longitude * 1E6));

						// 현재 위치와 현재 선택된 프로바이더를 카메라 오버레이 뷰에 알려줌
						mOverlayView.setCurrentGeoPoint(mCurrentGpsGeoPoint,
								addressString);
						mOverlayView.setCurrentProvider("G");
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				mGpsStatus = status;
			}

			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

		};

		// NETWORK 위치 리스너
		// GPS STATUS가 이용가능하지 않을때만 위치 최신화
		// GPS가 켜져있지 않거나, 건물안에 들어왔을때 등 일시적으로 GPS 신호가 잡히지 않을때는 NETWORK 신호 이용
		// 위도와 경도를 이용 주소를 알아냄

		mNetworkLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		mNetworkLocationListener = new LocationListener() {

			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub

				try {

					if (location != null && mOverlayView != null
							&& mGpsStatus != LocationProvider.AVAILABLE) {

						double latitude, longitude;
						latitude = location.getLatitude();
						longitude = location.getLongitude();

						mNetworkGeoCoder = new Geocoder(getBaseContext(),
								Locale.KOREA);

						List<Address> addresses = mNetworkGeoCoder
								.getFromLocation(latitude, longitude, 1);
						Address address = addresses.get(0);
						String addressString = address.getAddressLine(0);
						mCurrentNetworkGeoPoint = new GeoPoint(
								(int) (latitude * 1E6), (int) (longitude * 1E6));

						// 현재 위치와 현재 선택된 프로바이더를 카메라 오버레이 뷰에 알려줌
						mOverlayView.setCurrentGeoPoint(
								mCurrentNetworkGeoPoint, addressString);
						mOverlayView.setCurrentProvider("N");
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

		// 현재 허용된 위치 프로바이더중에서
		// GPS, NETWORK 중 허용된 프로바이더를 등록함
		// 둘다 허용되어있지 않다면 설정을 물어보는 액티비티로 이동

		List<String> providers = mGpsLocationManager.getProviders(true);

		if (providers.contains("gps") && providers.contains("network")) {
			// GPS, NETWORK 위치 프로바이더 둘다 있을때
			mGpsLocationManager
					.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
							3, mGpsLocationListener);
			mNetworkLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 1000, 3,
					mNetworkLocationListener);
			mOverlayView.setProviderString("GN");

		} else if (providers.contains("gps")) {
			// GPS 위치 프로바이더만 있을때
			mGpsLocationManager
					.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
							3, mGpsLocationListener);
			mOverlayView.setProviderString("G");

		} else if (providers.contains("network")) {
			// NETWORK 위치 프로바이더만 있을때
			mNetworkLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 1000, 3,
					mNetworkLocationListener);
			mOverlayView.setProviderString("N");

		} else {
			// GPS, NETWORK 위치 프로바이더 둘다 없을때

			startActivity(new Intent(this, LocationSettingRequest.class));
			finish();

		}

	}

	// 액티비티가 소멸될때 위치 리스너와 오버레이 뷰의 자원을 해제해줌
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mOverlayView.viewDestory();
		mGpsLocationManager.removeUpdates(mGpsLocationListener);
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

}
