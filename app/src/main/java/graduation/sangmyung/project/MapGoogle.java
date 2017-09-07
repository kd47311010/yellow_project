package graduation.sangmyung.project;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Iterator;
import java.util.List;

public class MapGoogle extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private DBHandler mDBHandler;
    private List<DBRecord> mDBRecordList = null;
    private Iterator<DBRecord> mDBRecordIterator;
    private DBRecord mDBRecord;
    private String mName;
    private double mLongitude;
    private double mLatitude;
    private String mAbout;
    private String mTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        overridePendingTransition(android.R.anim.slide_in_left, 0);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initDBHandler();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sangmyung = new LatLng(37.602638, 126.955241);
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(18);
        MarkerOptions marker = new MarkerOptions();
        marker.position(sangmyung)
                .title("Sangmyung")
                .snippet("상명대학교");
        googleMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sangmyung));
        googleMap.animateCamera(zoom);
        CreateMarkers(googleMap);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 마커 클릭시 호출되는 콜백 메서드
                Toast.makeText(getApplicationContext(),
                        marker.getTitle()
                        , Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void initDBHandler() {
        // TODO Auto-generated method stub

        mDBHandler = new DBHandler(this);
        mDBHandler.setTheme(DBHandler.ALL);
        // mDBHandler.copyDB();
        mDBHandler.readDB();
        mDBRecordList = mDBHandler.getmDBRecordList();
    }

    private void CreateMarkers(GoogleMap googleMap){
        mDBRecordIterator = mDBRecordList.iterator();
        for (int i = 0; i < mDBRecordList.size(); i++){
            MarkerOptions markers = new MarkerOptions();
            mDBRecord = mDBRecordIterator.next();
            mName = mDBRecord.getName();
            mLatitude = mDBRecord.getLatitude();
            mLongitude = mDBRecord.getLongitude();
            mAbout = mDBRecord.getAbout();
            mTheme = mDBRecord.getTheme();
            markers.position(new LatLng(mLatitude, mLongitude)).title(mName);
            if(mTheme.equals("편의점"))
                markers.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            if(mTheme.equals("카페"))
                markers.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            if(mTheme.equals("학교건물"))
                markers.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            if(mTheme.equals("문구류"))
                markers.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
            if(mTheme.equals("음식점"))
                markers.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            googleMap.addMarker(markers);
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker markers) {
                    // 마커 클릭시 호출되는 콜백 메서드
                    Toast.makeText(getApplicationContext(),
                            markers.getTitle()
                            , Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
    }

    public void onBackPressed() {
        this.finish();
        overridePendingTransition(0, android.R.anim.slide_out_right);
    }
}