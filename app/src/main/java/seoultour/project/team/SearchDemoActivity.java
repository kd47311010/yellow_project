// 다음지도 서치 액티비티
package seoultour.project.team;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import seoultour.project.team.search.MapApiConst;
import seoultour.project.team.search.Item;
import seoultour.project.team.search.OnFinishSearchListener;
import seoultour.project.team.search.Searcher;
import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPoint.GeoCoordinate;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SearchDemoActivity extends FragmentActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener {

	private static final String LOG_TAG = "SearchDemoActivity";

	private static final MapPoint DEFAULT_MARKER_POINT = MapPoint.mapPointWithGeoCoord(37.602638, 126.955241);	// 상명대학교 좌표


	private MapView mMapView;
	private MapPOIItem mDefaultMarker;
	private DBHandler mDBHandler;
	private List<DBRecord> mDBRecordList = null;
	private Iterator<DBRecord> mDBRecordIterator;
	private DBRecord mDBRecord;
	private String mName;
	private double mLongitude;
	private double mLatitude;
	private String mAbout;

	private EditText mEditTextQuery;

	private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.daumsearch);

		mMapView = (MapView)findViewById(R.id.map_view);
		mMapView.setDaumMapApiKey("2d59790d9415de7bac504f1ac6949b73");
		mMapView.setMapViewEventListener(this);
		mMapView.setPOIItemEventListener(this);
		mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());

		initDBHandler();

	}

	class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {

		private final View mCalloutBalloon;

		public CustomCalloutBalloonAdapter() {
			mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
		}

		@Override
		public View getCalloutBalloon(MapPOIItem poiItem) {
			if (poiItem == null) return null;
			Item item = mTagItemMap.get(poiItem.getTag());
			if (item == null) return null;
			ImageView imageViewBadge = (ImageView) mCalloutBalloon.findViewById(R.id.badge);
			TextView textViewTitle = (TextView) mCalloutBalloon.findViewById(R.id.title);
			textViewTitle.setText(item.title);
			TextView textViewDesc = (TextView) mCalloutBalloon.findViewById(R.id.desc);
			textViewDesc.setText(item.address);
			imageViewBadge.setImageDrawable(createDrawableFromUrl(item.imageUrl));
			return mCalloutBalloon;
		}

		@Override
		public View getPressedCalloutBalloon(MapPOIItem poiItem) {
			return null;
		}

	}

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditTextQuery.getWindowToken(), 0);
	}

	public void onMapViewInitialized(MapView mapView) {
		Log.i(LOG_TAG, "MapView had loaded. Now, MapView APIs could be called safely");

		mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.602638, 126.955241), 1, true);

		CreateMarkers(mMapView);
		createDefaultMarker(mMapView);

	}

	private void createDefaultMarker(MapView mapView) {
		mDefaultMarker = new MapPOIItem();
		String name = "상명대학교";
		mDefaultMarker.setItemName(name);
		mDefaultMarker.setTag(0);
		mDefaultMarker.setMapPoint(DEFAULT_MARKER_POINT);
		mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
		mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

		mapView.addPOIItem(mDefaultMarker);
		mapView.selectPOIItem(mDefaultMarker, true);
		mapView.setMapCenterPoint(DEFAULT_MARKER_POINT, false);
	}

	private void initDBHandler() {
		// TODO Auto-generated method stub

		mDBHandler = new DBHandler(this);
		mDBHandler.setTheme(DBHandler.ALL);
		// mDBHandler.copyDB();
		mDBHandler.readDB();
		mDBRecordList = mDBHandler.getmDBRecordList();
	}

	private void CreateMarkers(MapView mapView)
	{
		mDBRecordIterator = mDBRecordList.iterator();

		for (int i = 0; i < mDBRecordList.size(); i++){
			mDBRecord = mDBRecordIterator.next();
			mName = mDBRecord.getName();
			mLatitude = mDBRecord.getLatitude();
			mLongitude = mDBRecord.getLongitude();
			mAbout = mDBRecord.getAbout();
			MapPoint MARKER_POINT=MapPoint.mapPointWithGeoCoord(mLatitude,mLongitude);
			mDefaultMarker = new MapPOIItem();
			String name=mName;
			mDefaultMarker.setItemName(name);
			mDefaultMarker.setTag(0);
			mDefaultMarker.setMapPoint(MARKER_POINT);
			mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
			mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
			mapView.addPOIItem(mDefaultMarker);
			mapView.selectPOIItem(mDefaultMarker, true);
			mapView.setMapCenterPoint(MARKER_POINT, false);
		}
	}

	private Drawable createDrawableFromUrl(String url) {
		try {
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}

	@Override
	public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
		Item item = mTagItemMap.get(mapPOIItem.getTag());
		StringBuilder sb = new StringBuilder();
		sb.append("title=").append(item.title).append("\n");
		sb.append("imageUrl=").append(item.imageUrl).append("\n");
		sb.append("address=").append(item.address).append("\n");
		sb.append("newAddress=").append(item.newAddress).append("\n");
		sb.append("zipcode=").append(item.zipcode).append("\n");
		sb.append("phone=").append(item.phone).append("\n");
		sb.append("category=").append(item.category).append("\n");
		sb.append("longitude=").append(item.longitude).append("\n");
		sb.append("latitude=").append(item.latitude).append("\n");
		sb.append("distance=").append(item.distance).append("\n");
		sb.append("direction=").append(item.direction).append("\n");
		Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
	}

	@Override
	@Deprecated
	public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
	}

	@Override
	public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
	}

	@Override
	public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
	}

	@Override
	public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {
	}

	@Override
	public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
	}

	@Override
	public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
	}

	@Override
	public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
	}

	@Override
	public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
	}

	@Override
	public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
	}

	@Override
	public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
	}

	@Override
	public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
	}

}
