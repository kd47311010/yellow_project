//	등록된 커스텀 오버레이 아이템을 관리함
//
//	오버레이 터치시 이미지와 정보가 포함된 레이아웃을 띄움
//
//	레이아웃 터치시 정보 액티비티로 전환됨

package seoultour.project.team;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class SeoulMapOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays;
	private SeoulMapActivity mContext;
	private MapView mMapView;
	private LinearLayout mLinearLayout;
	private String itemTitle;
	private LinearLayout mFirstChildLinearLayout;
	private LinearLayout mSecondChildLinearLayout;
	private LinearLayout mHoriFirstLinearLayout;
	private LinearLayout mHoriSecondLinearLayout;
	private Bitmap mPalaceIconBitmap;
	private Bitmap mCultureIconBitmap;
	private Bitmap mLandscapesIconBitmap;
	private Bitmap mMuseumIconBitmap;
	private Bitmap mShoppingIconBitmap;
	private Bitmap closeButtonBitmap;

	public SeoulMapOverlay(Drawable defaultMarker, Context context,
			MapView mapView) {
		super(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub

		mContext = (SeoulMapActivity) context;
		mMapView = mapView;
		mOverlays = new ArrayList<OverlayItem>();

		mLinearLayout = new LinearLayout(mContext); // 오버레이 아이템 터치시 나타나는 레이아웃

		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.pop4);
		Drawable drawable = new BitmapDrawable(bitmap);

		// 오버레이 아이템 터치시 나타나는 레이아웃에 들어갈 BITMAP
		mPalaceIconBitmap = BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.palaceicon);
		mPalaceIconBitmap = Bitmap.createScaledBitmap(mPalaceIconBitmap, 50,
				50, true);

		mCultureIconBitmap = BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.cultureicon);
		mCultureIconBitmap = Bitmap.createScaledBitmap(mCultureIconBitmap, 50,
				50, true);

		mLandscapesIconBitmap = BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.landscapesicon);
		mLandscapesIconBitmap = Bitmap.createScaledBitmap(
				mLandscapesIconBitmap, 50, 50, true);

		mMuseumIconBitmap = BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.museumicon);
		mMuseumIconBitmap = Bitmap.createScaledBitmap(mMuseumIconBitmap, 50,
				50, true);

		mShoppingIconBitmap = BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.shoppingicon);
		mShoppingIconBitmap = Bitmap.createScaledBitmap(mShoppingIconBitmap,
				50, 50, true);

		closeButtonBitmap = BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.closebutton);

		mLinearLayout.setBackgroundDrawable(drawable);
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);

		// 오버레이 아이템 터치시 나타나는 레이아웃을 터치했을때 정보페이지로 이동
		mLinearLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				Toast.makeText(mContext, "정보 페이지로 이동중", Toast.LENGTH_SHORT)
						.show();

				Intent intent = new Intent(mContext, InfoActivity.class);

				String[] tStrings = itemTitle.split("splitspace");

				intent.putExtra("name", tStrings[0]);
				mContext.startActivity(intent);

			}
		});
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();
	}

	public void addOverlayItem(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	// 오버레이 아이템을 터치했을때 레이아웃을 보여줌
	@Override
	public boolean onTap(int index) {
		// 터치 한 아이템
		OverlayItem item = mOverlays.get(index);

		// TODO Auto-generated method stub

		// 현재 나타나는 오버레이 아이템 터치시 나타나는 레이아웃을 지움
		mLinearLayout.removeAllViews();
		mMapView.removeView(mLinearLayout);
		mLinearLayout.setVisibility(View.GONE);

		// 현재 위치를 알 수 있을때 현재 위치와 랜드마크와의 거리를 측정
		GeoPoint itemGP = item.getPoint();
		GeoPoint currentGP = mContext.getCurrentLocation();

		String tDistance = "";

		if (currentGP != null) {
			Location itemLocation = new Location("item");
			Location currentLocation = new Location("current");

			itemLocation.setLatitude(itemGP.getLatitudeE6() / 1E6);
			itemLocation.setLongitude(itemGP.getLongitudeE6() / 1E6);

			currentLocation.setLatitude(currentGP.getLatitudeE6() / 1E6);
			currentLocation.setLongitude(currentGP.getLongitudeE6() / 1E6);

			int distance = (int) itemLocation.distanceTo(currentLocation);

			if (distance < 1000) {
				tDistance = distance + "m";
			} else if (distance >= 1000) {
				distance = distance / 1000;
				tDistance = distance + "Km";
			}

			tDistance = "\t거리 : " + tDistance;
		}

		// 레이아웃의 파라미터 설정
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				item.getPoint(), 0, -15, MapView.LayoutParams.BOTTOM_CENTER);

		params.mode = MapView.LayoutParams.MODE_MAP;

		// 레이아웃에 하위 위젯 추가
		TextView textView = new TextView(mContext);
		itemTitle = item.getTitle();
		String[] tSplits = itemTitle.split("splitspace");

		String tString = "[" + tSplits[0] + "]" + tDistance + tSplits[1]
				+ "\n자세히 보기 - 터치";
		textView.setText(tString);
		textView.setTextColor(Color.BLACK);

		ImageView firstImageView = new ImageView(mContext);
		ImageView closeImageView = new ImageView(mContext);

		// try {
		// URL url;
		// url = new URL(item.getSnippet());
		// URLConnection conn = url.openConnection();
		// conn.connect();
		// BufferedInputStream bis = new BufferedInputStream(
		// conn.getInputStream());
		// BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inSampleSize = 8;
		// Bitmap bm = BitmapFactory.decodeStream(bis, null, options);
		// bis.close();
		//
		// Bitmap resized = Bitmap.createScaledBitmap(bm, 70, 70, true);
		// firstImageView.setImageBitmap(resized);
		//
		// // TODO Auto-generated method stub
		//
		//
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// 테마에 맞는 이미지 설정
		String theme = item.getSnippet();

		if (theme.equals(DBHandler.CULTURE)) {
			firstImageView.setImageBitmap(mCultureIconBitmap);
		} else if (theme.equals(DBHandler.LANDSCAPE)) {
			firstImageView.setImageBitmap(mLandscapesIconBitmap);
		} else if (theme.equals(DBHandler.MUSEUM)) {
			firstImageView.setImageBitmap(mMuseumIconBitmap);
		} else if (theme.equals(DBHandler.PALACE)) {
			firstImageView.setImageBitmap(mPalaceIconBitmap);
		} else if (theme.equals(DBHandler.SHOPPING)) {
			firstImageView.setImageBitmap(mShoppingIconBitmap);
		}

		// 레이아웃에 하위 위젯 추가
		// 레이아웃 보여줌
		closeImageView.setImageBitmap(closeButtonBitmap);

		mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mLinearLayout.setPadding(30, 30, 30, 70);
		mLinearLayout.setVisibility(View.VISIBLE);
		mMapView.addView(mLinearLayout, params);
		mFirstChildLinearLayout = new LinearLayout(mContext);
		mSecondChildLinearLayout = new LinearLayout(mContext);

		mHoriFirstLinearLayout = new LinearLayout(mContext);
		mHoriSecondLinearLayout = new LinearLayout(mContext);

		mLinearLayout.addView(mFirstChildLinearLayout);
		mLinearLayout.addView(mSecondChildLinearLayout);

		mFirstChildLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		mFirstChildLinearLayout.addView(mHoriFirstLinearLayout);
		mFirstChildLinearLayout.addView(mHoriSecondLinearLayout);

		mHoriFirstLinearLayout.addView(firstImageView);
		mHoriSecondLinearLayout.addView(closeImageView);
		mHoriSecondLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		mHoriSecondLinearLayout.setGravity(Gravity.RIGHT);

		textView.setGravity(Gravity.LEFT);
		mLinearLayout.addView(textView);

		// 닫기 이미지 클릭시 레이아웃 없앰
		closeImageView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				mLinearLayout.removeAllViews();
				mMapView.removeView(mLinearLayout);
				mLinearLayout.setVisibility(View.GONE);
			}

		});

		// mLinearLayout.getWidth(), mLinearLayout.getHeight()

		mMapView.getController().animateTo(item.getPoint());

		return true;
	}

}
