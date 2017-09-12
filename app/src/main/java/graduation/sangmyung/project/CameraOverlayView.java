package graduation.sangmyung.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import static graduation.sangmyung.project.R.mipmap.gs25img;
import static graduation.sangmyung.project.R.mipmap.toy;

public class CameraOverlayView extends View implements SensorEventListener {

    private static float mXCompassDegree;
    private static float mYCompassDegree;
    private SensorManager mSensorManager;
    private Sensor mOriSensor;
    private int mWidth;
    private int mHeight;
    private GeoPoint mCurrentGeoPoint = null;
    private boolean mCurrentGeoPointUpdated = false;
    private Paint mPaint;
    private Paint mTextColor;
    private Bitmap mPalaceIconBitmap;
    private String mAdressString;
    private String mProviders;
    private String mCurrentProvider;
    private List<PointF> mPointFList = null;
    private HashMap<Integer, String> mPointHashMap;
    private boolean mTouched = false;
    private int mTouchedItem;
    private CameraActivity mContext;
    private List<DBRecord> mDBRecordList;
    private DBHandler mDBHandler;
    private RectF mPopRect;
    private RectF mPalaceRect;
    private RectF mLandscapesRect;
    private RectF mShoppingRect;
    private RectF mCultureRect;
    private RectF mMuseumRect;
    private RectF mAllRect;
    private Paint mThemePaint;
    private Paint mSelectedThemePaint;
    private Paint mPopPaint;
    private RectF mVisibilitySettingRect;
    private int mVisibleDistance = 10;
    private float mTouchedY;
    private float mTouchedX;
    private boolean mScreenTouched = false;
    private int mCounter = 0;
    private Paint mTouchEffectPaint;
    private Paint mPointPaint1;
    private Paint mPointPaint2;
    private Bitmap mCultureIconBitmap;
    private Bitmap mLandscapesIconBitmap;
    private Bitmap mMuseumIconBitmap;
    private Bitmap mShoppingIconBitmap;
    private Paint mShadowPaint;
    private int mShadowXMargin;
    private int mShadowYMargin;
    private RectF mAllVisibleRect;
    private boolean mAllVisible = false;

    public CameraOverlayView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        mContext = (CameraActivity) context;

        // 비트맵, 센서, 페인트, DB 핸들러 초기화
        initBitamaps();
        initSensor(context);
        initPaints();
        initDBHandler();
    }

    // onSensorChanged에서 센서값 TYPE_ORIENTATION이 일정한 시간마다 INVALIDATE되어 실행됨
    // 정보를 계속 그리면서 표현, DB의 레코드를 해석하면서 아이템을 그림
    public void onDraw(Canvas canvas) {

        canvas.save();

        // 안드로이드 2.1이하에서는 카메라 화면이 오른쪽으로 90도 돌아간 화면으로 나옴
        // 화면을 돌리기 위하여 사용
        canvas.rotate(270, mWidth / 2, mHeight / 2);

        // 사각형 초기화
        initRectFs();

        // drawAxes(canvas);

        // 테마버튼 그림
        drawThemeButton(canvas);

        //MyView(canvas);

        // 보여지는 거리 셋팅 그림
        drawVisibilitySetting(canvas);

        // 보여지는 거리의 현재 세팅 그림
        //drawVisibilitySettingCurrentPoint(canvas);

        // 현재 위치 정보를 그림
        drawCurrentLocationInfo(canvas);

        // 아이템이 터치된 상태일때 팝업을 그림
		/*if (mTouched == true) {
			drawPopup(canvas);
		}*/

        // DB의 레코드를 읽어들이고, drawGrid를 실행시켜 랜드마크 아이템들을 그림
        interpretDB(canvas);

        // 회전된 카메라를 원상복귀함
        canvas.restore();

        // 스크린이 터치되었을때 효과를 그림
        if (mScreenTouched == true && mCounter < 15) {
            drawTouchEffect(canvas);
            mCounter++;
        } else {
            mScreenTouched = false;
            mCounter = 0;
        }
    }

    // 스크린이 터치될때의 효과를 그림 원 3개를 물결처럼 그림
    private void drawTouchEffect(Canvas pCanvas) {
        // TODO Auto-generated method stub
        pCanvas.drawCircle(mTouchedX, mTouchedY, mCounter * 1,
                mTouchEffectPaint);
        pCanvas.drawCircle(mTouchedX, mTouchedY, mCounter * 2,
                mTouchEffectPaint);
        pCanvas.drawCircle(mTouchedX, mTouchedY, mCounter * 3,
                mTouchEffectPaint);
    }

	/*
	// 보여지는 범위 현재 범위 동그라미를 그림
	private void drawVisibilitySettingCurrentPoint(Canvas pCanvas) {
		// TODO Auto-generated method stub

		if (mAllVisible == false) {
			float maxValue = (mVisibilitySettingRect.right - mWidth / 2)
					- (mVisibilitySettingRect.left - mWidth / 2);
			float unitValue = maxValue / 10;

			pCanvas.drawCircle(
					mVisibilitySettingRect.left
							+ (unitValue * mVisibleDistance),
					mVisibilitySettingRect.top
							+ ((mVisibilitySettingRect.bottom - mVisibilitySettingRect.top) / 2),
					20, mPointPaint1);

			pCanvas.drawCircle(
					mVisibilitySettingRect.left
							+ (unitValue * mVisibleDistance),
					mVisibilitySettingRect.top
							+ ((mVisibilitySettingRect.bottom - mVisibilitySettingRect.top) / 2),
					10, mPointPaint2);
		}
	}
	*/

    // 스크린이 터치되었을때 좌표를 해석하고 처리
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        // 화면이 회전되었기에 좌표도 변환함
        float convertedX, convertedY, temp;
        convertedX = event.getX();
        convertedY = event.getY();
        convertedX = convertedX - mWidth / 2;
        convertedY = convertedY - mHeight / 2;
        temp = convertedX;
        convertedX = -convertedY;
        convertedY = temp;

        mTouchedX = event.getX();
        mTouchedY = event.getY();

        mScreenTouched = true;

        // 테마 버튼별 터치시 처리, 테마에 해당하는 db 파일을 읽음
        if (convertedX > mPalaceRect.left - mWidth / 2
                && convertedX < mPalaceRect.right - mWidth / 2
                && convertedY > mPalaceRect.top - mHeight / 2
                && convertedY < mPalaceRect.bottom - mHeight / 2) {

            mDBHandler.setTheme(DBHandler.PALACE);
            mDBHandler.readDB();

        } else if (convertedX > mLandscapesRect.left - mWidth / 2
                && convertedX < mLandscapesRect.right - mWidth / 2
                && convertedY > mLandscapesRect.top - mHeight / 2
                && convertedY < mLandscapesRect.bottom - mHeight / 2) {

            mDBHandler.setTheme(DBHandler.LANDSCAPE);
            mDBHandler.readDB();

        } else if (convertedX > mShoppingRect.left - mWidth / 2
                && convertedX < mShoppingRect.right - mWidth / 2
                && convertedY > mShoppingRect.top - mHeight / 2
                && convertedY < mShoppingRect.bottom - mHeight / 2) {

            mDBHandler.setTheme(DBHandler.SHOPPING);
            mDBHandler.readDB();

        } else if (convertedX > mCultureRect.left - mWidth / 2
                && convertedX < mCultureRect.right - mWidth / 2
                && convertedY > mCultureRect.top - mHeight / 2
                && convertedY < mCultureRect.bottom - mHeight / 2) {

            mDBHandler.setTheme(DBHandler.CULTURE);
            mDBHandler.readDB();

        } else if (convertedX > mMuseumRect.left - mWidth / 2
                && convertedX < mMuseumRect.right - mWidth / 2
                && convertedY > mMuseumRect.top - mHeight / 2
                && convertedY < mMuseumRect.bottom - mHeight / 2) {

            mDBHandler.setTheme(DBHandler.MUSEUM);
            mDBHandler.readDB();

        } else if (convertedX > mAllRect.left - mWidth / 2
                && convertedX < mAllRect.right - mWidth / 2
                && convertedY > mAllRect.top - mHeight / 2
                && convertedY < mAllRect.bottom - mHeight / 2) {

            mDBHandler.setTheme(DBHandler.ALL);
            mDBHandler.readDB();
        }

		/*
		// 보여지는 범위 셋팅을 터치했을때 처리
		// 터치시 터치된 지점에 따라 보여지는 범위를 설정
		if (convertedX > mVisibilitySettingRect.left - mWidth / 2
				&& convertedX < mVisibilitySettingRect.right - mWidth / 2
				&& convertedY > mVisibilitySettingRect.top - mHeight / 2
				&& convertedY < mVisibilitySettingRect.bottom - mHeight / 2) {
			float maxValue = (mVisibilitySettingRect.right - mWidth / 2)
					- (mVisibilitySettingRect.left - mWidth / 2);
			float touchedValue = maxValue
					- ((mVisibilitySettingRect.right - mWidth / 2) - convertedX);
			float percentValue = (touchedValue / maxValue) * 100;

			if (percentValue < 2) {
				percentValue = 2;
			} else if (percentValue > 99) {
				percentValue = 100;
			}

			mVisibleDistance = (int) (percentValue / 2);
			mAllVisible = false;
		}
		*/

        mTouched = false;
        PointF tPoint;
        Iterator<PointF> pointIterator = mPointFList.iterator();
        for (int i = 0; i < mPointFList.size(); i++) {
            tPoint = pointIterator.next();

            if (convertedX > tPoint.x - (mPalaceIconBitmap.getWidth() / 2)
                    && convertedX < tPoint.x
                    + (mPalaceIconBitmap.getWidth() / 2)
                    && convertedY > tPoint.y
                    - (mPalaceIconBitmap.getHeight() / 2)
                    && convertedY < tPoint.y
                    + (mPalaceIconBitmap.getHeight() / 2)) {

                mTouched = true;
                mTouchedItem = i;

            }
        }


        String tName = mPointHashMap.get(mTouchedItem);
        String theme = null;
        Iterator<DBRecord> dbRecordIterator = mDBRecordList.iterator();
        for (int i = 0; i < mDBRecordList.size(); i++) {
            DBRecord tDBRecord = dbRecordIterator.next();
            if (tDBRecord.getName().equals(tName) == true) {
                theme = tDBRecord.getTheme();
            }
        }

        // 팝업을 터치시 처리
        // 터치시 정보 페이지로 이동
        if (mTouched == true) {

            //Toast.makeText(mContext, "정보 페이지로 이동중", Toast.LENGTH_SHORT)
            //		.show();

            Intent intent = new Intent(mContext, ItemActivity.class);
            intent.putExtra("theme", theme);
            mContext.startActivity(intent);
        }




        // 보여지는 범위 무제한 터치시 처리
        // 범위 50000km로 설정
        if (convertedX > mAllVisibleRect.left - mWidth / 2
                && convertedX < mAllVisibleRect.right - mWidth / 2
                && convertedY > mAllVisibleRect.top - mHeight / 2
                && convertedY < mAllVisibleRect.bottom - mHeight / 2) {
            //mAllVisible = true;
            //mVisibleDistance = 50000;
            Toast.makeText(mContext, "구글맵으로 이동합니다.", Toast.LENGTH_SHORT)
                    .show();
            Intent intent=new Intent(mContext, MapGoogle.class);
            mContext.startActivity(intent);
        }

        return super.onTouchEvent(event);

    }

    // 테마 아이콘 비트맵 초기화
    private void initBitamaps() {
        // TODO Auto-generated method stub
        mPalaceIconBitmap = BitmapFactory.decodeResource(getResources(),
                gs25img);
        mPalaceIconBitmap = Bitmap.createScaledBitmap(mPalaceIconBitmap, 100,
                100, true);

        mCultureIconBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.food);
        mCultureIconBitmap = Bitmap.createScaledBitmap(mCultureIconBitmap, 100,
                100, true);

        mLandscapesIconBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.school);
        mLandscapesIconBitmap = Bitmap.createScaledBitmap(
                mLandscapesIconBitmap, 100, 100, true);

        mMuseumIconBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.toy);
        mMuseumIconBitmap = Bitmap.createScaledBitmap(mMuseumIconBitmap, 100,
                100, true);

        mShoppingIconBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.cafe);
        mShoppingIconBitmap = Bitmap.createScaledBitmap(mShoppingIconBitmap,
                100, 100, true);

    }

    // 센서 초기화
    // TYPE_ORIENTATION 사용할수 있게 설정
    private void initSensor(Context context) {
        // TODO Auto-generated method stub
        mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        mOriSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, mOriSensor,
                SensorManager.SENSOR_DELAY_UI);
    }

    // DB 초기화
    // 처음에는 MUSEUM 테마 읽음
    private void initDBHandler() {
        // TODO Auto-generated method stub

        mDBHandler = new DBHandler(mContext);
        mDBHandler.setTheme(DBHandler.MUSEUM);
        // mDBHandler.copyDB();
        mDBHandler.readDB();

    }

    // 페인트 초기화
    // 그려질 여러 메뉴, 아이템의 페인트 설정
    private void initPaints() {
        // TODO Auto-generated method stub
        mShadowXMargin = 2;
        mShadowYMargin = 2;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.rgb(249, 249, 249));
        //mPaint.getText
        mPaint.setTextSize(25);

        mTextColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextColor.setColor(Color.rgb(20, 20, 20));
        mTextColor.setTextSize(25);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setColor(Color.BLACK);
        mShadowPaint.setTextSize(25);

        mThemePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThemePaint.setColor(Color.rgb(239, 239, 35));

        mSelectedThemePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectedThemePaint.setColor(Color.rgb(193, 205, 193));

        mPopPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPopPaint.setColor(Color.rgb(131, 139, 131));

        mTouchEffectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTouchEffectPaint.setColor(Color.rgb(239, 239, 35));
        mTouchEffectPaint.setStrokeWidth(5);
        mTouchEffectPaint.setStyle(Paint.Style.STROKE);

        mPointPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint1.setColor(Color.rgb(173, 216, 230));

        mPointPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint2.setColor(Color.rgb(240, 255, 240));
    }

    // 사각형 초기화
    // 보여질 여러 사각형 좌표, 페인트 설정
    private void initRectFs() {
        // TODO Auto-generated method stub
        int themeRectWidth = (mHeight - (mHeight / 20 * 2)) / 6;

        mPalaceRect = new RectF((float) ((mWidth - mHeight) / 2) + mHeight / 20
                + (themeRectWidth * 0), (float) (-(mWidth - mHeight) / 2)
                + mHeight / 20, (float) ((mWidth - mHeight) / 2) + mHeight / 20
                + (themeRectWidth * 1), (float) -(mHeight / 20));

        mLandscapesRect = new RectF((float) ((mWidth - mHeight) / 2) + mHeight
                / 20 + (themeRectWidth * 1), (float) (-(mWidth - mHeight) / 2)
                + mHeight / 20, (float) ((mWidth - mHeight) / 2) + mHeight / 20
                + (themeRectWidth * 2), (float) -(mHeight / 20));

        mShoppingRect = new RectF((float) ((mWidth - mHeight) / 2) + mHeight
                / 20 + (themeRectWidth * 2), (float) (-(mWidth - mHeight) / 2)
                + mHeight / 20, (float) ((mWidth - mHeight) / 2) + mHeight / 20
                + (themeRectWidth * 3), (float) -(mHeight / 20));

        mCultureRect = new RectF((float) ((mWidth - mHeight) / 2) + mHeight
                / 20 + (themeRectWidth * 3), (float) (-(mWidth - mHeight) / 2)
                + mHeight / 20, (float) ((mWidth - mHeight) / 2) + mHeight / 20
                + (themeRectWidth * 4), (float) -(mHeight / 20));

        mMuseumRect = new RectF((float) ((mWidth - mHeight) / 2) + mHeight / 20
                + (themeRectWidth * 4), (float) (-(mWidth - mHeight) / 2)
                + mHeight / 20, (float) ((mWidth - mHeight) / 2) + mHeight / 20
                + (themeRectWidth * 5), (float) -(mHeight / 20));

        mAllRect = new RectF((float) ((mWidth - mHeight) / 2) + mHeight / 20
                + (themeRectWidth * 5), (float) (-(mWidth - mHeight) / 2)
                + mHeight / 20, (float) ((mWidth - mHeight) / 2) + mHeight / 20
                + (themeRectWidth * 6), (float) -(mHeight / 20));

        mPopRect = new RectF((float) ((mWidth - mHeight) / 2) + mHeight / 20,
                (float) ((mHeight / 5) * 4), (float) mWidth
                - ((mWidth - mHeight) / 2) - mHeight / 20,
                (float) mHeight + ((mHeight / 5)) - 30);

        mVisibilitySettingRect = new RectF((float) ((mWidth - mHeight) / 2)
                + mHeight / 20 + (themeRectWidth * 0),
                (float) (-(mWidth - mHeight) / 2) + mHeight / 4,
                (float) ((mWidth - mHeight) / 2) + mHeight / 20
                        + (themeRectWidth * 6), (float) (mHeight / 20));

        mAllVisibleRect = new RectF((float) ((mWidth - mHeight) / 2) + mHeight
                / 20 + (themeRectWidth * 5),
                (float) (-(mWidth - mHeight) / 2 + 10) + mHeight / 20
                        + (themeRectWidth * 2),
                (float) ((mWidth - mHeight) / 2) + mHeight / 20
                        + (themeRectWidth * 6), (float) -(mHeight / 20)
                + (themeRectWidth * 2) + 10);

    }

    // 아이템 터치시 나타나는 팝업 그림
    private void drawPopup(Canvas pCanvas) {
        // TODO Auto-generated method stub
        pCanvas.drawRoundRect(mPopRect, 20, 20, mPopPaint);

        int xMargin = 20;
        int yMargin = 0;

        // 터치된 아이템을 이용하여 이름이 무엇인지 알아내고 보여줌
        String tName = mPointHashMap.get(mTouchedItem);
        pCanvas.drawText(tName, ((mWidth - mHeight) / 2) + mHeight / 20
                + xMargin + mShadowXMargin, ((mHeight / 5) * 4 + 40) + yMargin
                + mShadowYMargin, mShadowPaint);

        pCanvas.drawText(tName, ((mWidth - mHeight) / 2) + mHeight / 20
                + xMargin, ((mHeight / 5) * 4 + 40) + yMargin, mPaint);

        // 터치된 아이템 정보를 보여줌
        Iterator<DBRecord> dbRecordIterator = mDBRecordList.iterator();
        for (int i = 0; i < mDBRecordList.size(); i++) {
            DBRecord tDBRecord = dbRecordIterator.next();
            if (tDBRecord.getName().equals(tName) == true) {
                String tAbout = tDBRecord.getAbout();

                pCanvas.drawText(tAbout, ((mWidth - mHeight) / 2) + mHeight
                                / 20 + xMargin + mShadowXMargin,
                        ((mHeight / 5) * 4 + 90) + yMargin + mShadowYMargin,
                        mShadowPaint);

                pCanvas.drawText(tAbout, ((mWidth - mHeight) / 2) + mHeight
                                / 20 + xMargin, ((mHeight / 5) * 4 + 90) + yMargin,
                        mPaint);
            }
        }

        String tInfo = "자세히 보기 - 터치";

        pCanvas.drawText(tInfo, ((mWidth - mHeight) / 2) + mHeight / 20
                + xMargin + mShadowXMargin, ((mHeight / 5) * 4 + 140) + yMargin
                + mShadowYMargin, mShadowPaint);

        pCanvas.drawText(tInfo, ((mWidth - mHeight) / 2) + mHeight / 20
                + xMargin, ((mHeight / 5) * 4 + 140) + yMargin, mPaint);
    }



    // 현재 보여지는 범위, 위치 정보를 보여줌
    private void drawCurrentLocationInfo(Canvas pCanvas) {
        // TODO Auto-generated method stub

        // 현재 위치를 알수 있을 때 보여줌

        if (mCurrentGeoPointUpdated) {

            String tVisible = "";
            //[보여지는 범위]  " + mVisibleDistance + "Km

            //if (mAllVisible == true) {
            //	tVisible = "[보여지는 범위] 무제한 ";
            //}

            Paint tPaint = new Paint();

            pCanvas.drawText(
                    tVisible,
                    mVisibilitySettingRect.left
                            + (mPaint.measureText(tVisible) / 2 + mShadowXMargin)
                            - 50, mVisibilitySettingRect.bottom + 70
                            + mShadowYMargin, mShadowPaint);

            pCanvas.drawText(tVisible,
                    mVisibilitySettingRect.left
                            + (mPaint.measureText(tVisible) / 2) - 50,
                    mVisibilitySettingRect.bottom + 70, mPaint);


            String[] tSplit = mAdressString.split("대한민국");

            pCanvas.drawText("[현재 위치] " + tSplit[1], mWidth / 6
                            + mShadowXMargin, mHeight + 100 + mShadowYMargin,
                    mShadowPaint);

            pCanvas.drawText("[현재 위치] " + tSplit[1], mWidth / 6, mHeight + 100,
                    mPaint);

            pCanvas.drawText("[P] " + mProviders + " [C]  " + mCurrentProvider,
                    mWidth / 6, mHeight + 120, tPaint);
            // pCanvas.drawText("[현재 선택된 프로바이더]  " + mCurrentProvider, mWidth /
            // 6, mHeight + 70, mPaint);

        }

        else {
            pCanvas.drawText("---현재 위치 찾는 중---",
                    mWidth / 2 - (mPaint.measureText("---현재 위치 찾는 중---") / 2)
                            + mShadowXMargin,
                    mHeight / 2 - 20 + mShadowYMargin, mShadowPaint);

            pCanvas.drawText("---현재 위치 찾는 중---",
                    mWidth / 2 - (mPaint.measureText("---현재 위치 찾는 중---") / 2),
                    mHeight / 2 - 20, mPaint);
        }
    }




    // 테스트용 x, y 축 그리기
    // private void drawAxes(Canvas pCanvas) {
    // // TODO Auto-generated method stub
    // pCanvas.drawLine(0, 0, mWidth, 0, mPaint);
    // pCanvas.drawLine(0, mHeight, mWidth, mHeight, mPaint);
    // pCanvas.drawLine(0, mHeight / 2, mWidth, mHeight / 2, mPaint);
    // pCanvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight, mPaint);
    // }


	// 보여지는 범위 설정을 그림
	// 0 ~10Km 또는 무제한
	private void drawVisibilitySetting(Canvas pCanvas) {
		// TODO Auto-generated method stub

		// pCanvas.drawRoundRect(mVisibilitySettingRect, 10, 10, mPaint);
		pCanvas.drawRoundRect(mAllVisibleRect, 10, 10, mThemePaint);
/*
		pCanvas.drawText("0",
				mVisibilitySettingRect.left - (mPaint.measureText("0") / 2)
						+ mShadowXMargin, mVisibilitySettingRect.bottom + 20
						+ mShadowYMargin, mShadowPaint);
		pCanvas.drawText("0",
				mVisibilitySettingRect.left - (mPaint.measureText("0") / 2),
				mVisibilitySettingRect.bottom + 20, mPaint);

		pCanvas.drawText("10Km",
				mVisibilitySettingRect.right - (mPaint.measureText("10Km") / 2)
						+ mShadowXMargin, mVisibilitySettingRect.bottom + 20
						+ mShadowYMargin, mShadowPaint);
		pCanvas.drawText(
				"10Km",
				mVisibilitySettingRect.right - (mPaint.measureText("10Km") / 2),
				mVisibilitySettingRect.bottom + 20, mPaint);
*/
		pCanvas.drawText("2D지도", (mAllVisibleRect.left + mAllVisibleRect.right)
				/ 2 - mTextColor.measureText("2D지도") / 2,
				(mAllVisibleRect.top + mAllVisibleRect.bottom) / 2 + 8, mTextColor);


	}


	//public class MyView extends View {
    //    private Bitmap gs25img, foodimg, cafeimg, schoolimg, toyimg, allimg;
//
    //    public MyView(Context context) {
    //        super(context);
    //        //setBackground(Color.LTGRAY);
//
    //        Resources r = context.getResources();
    //        gs25img = BitmapFactory.decodeResource(r, R.drawable.gs25img);
    //        foodimg = BitmapFactory.decodeResource(r, R.drawable.food);
    //        cafeimg = BitmapFactory.decodeResource(r, R.drawable.cafe);
    //        schoolimg = BitmapFactory.decodeResource(r, R.drawable.school);
    //        toyimg = BitmapFactory.decodeResource(r, R.drawable.toy);
    //        allimg = BitmapFactory.decodeResource(r, R.drawable.all);
    //    }


        // 테마 버튼들을 그림
        // 선택된 테마가 있을때는 다른 색상으로 그려서 선택되었음을 나타냄
        private void drawThemeButton(Canvas pCanvas) {
            Paint tPaint = new Paint();
            int yTextMargin = 8;

            if (mDBHandler.getTheme().equals(DBHandler.PALACE)) {
                tPaint = mSelectedThemePaint;
            } else {
                tPaint = mThemePaint;
            }

            pCanvas.drawRoundRect(mPalaceRect, 10, 10, tPaint);
            pCanvas.drawText("편의점", (mPalaceRect.left + mPalaceRect.right) / 2
                            - mTextColor.measureText("편의점") / 2,
                    (mPalaceRect.top + mPalaceRect.bottom) / 2 + yTextMargin,
                    mTextColor);
            //pCanvas.drawBitmap(gs25img, (mPalaceRect.left + mPalaceRect.right) / 2
            //        - mTextColor.measureText("편의점") / 2,
            //        (mPalaceRect.top + mPalaceRect.bottom) / 2 + yTextMargin, mTextColor);

            if (mDBHandler.getTheme().equals(DBHandler.LANDSCAPE)) {
                tPaint = mSelectedThemePaint;
            } else {
                tPaint = mThemePaint;
            }
            pCanvas.drawRoundRect(mLandscapesRect, 10, 10, tPaint);
            pCanvas.drawText("음식점", (mLandscapesRect.left + mLandscapesRect.right)
                            / 2 - mTextColor.measureText("음식점") / 2,
                    (mLandscapesRect.top + mLandscapesRect.bottom) / 2
                            + yTextMargin, mTextColor);
            //pCanvas.drawBitmap(foodimg, (mLandscapesRect.left + mLandscapesRect.right)
            //        / 2 - mTextColor.measureText("음식점") / 2,
            //        (mLandscapesRect.top + mLandscapesRect.bottom) / 2
            //                + yTextMargin, mTextColor);

            if (mDBHandler.getTheme().equals(DBHandler.SHOPPING)) {
                tPaint = mSelectedThemePaint;
            } else {
                tPaint = mThemePaint;
            }
            pCanvas.drawRoundRect(mShoppingRect, 10, 10, tPaint);
            pCanvas.drawText("카페", (mShoppingRect.left + mShoppingRect.right) / 2
                            - mTextColor.measureText("카페") / 2,
                    (mShoppingRect.top + mShoppingRect.bottom) / 2 + yTextMargin,
                    mTextColor);
            //pCanvas.drawBitmap(cafeimg, (mShoppingRect.left + mShoppingRect.right) / 2
            //                - mTextColor.measureText("카페") / 2,
            //        (mShoppingRect.top + mShoppingRect.bottom) / 2 + yTextMargin,
            //        mTextColor);

            if (mDBHandler.getTheme().equals(DBHandler.CULTURE)) {
                tPaint = mSelectedThemePaint;
            } else {
                tPaint = mThemePaint;
            }
            pCanvas.drawRoundRect(mCultureRect, 10, 10, tPaint);
            pCanvas.drawText("학교건물", (mCultureRect.left + mCultureRect.right) / 2
                            - mTextColor.measureText("학교건물") / 2,
                    (mCultureRect.top + mCultureRect.bottom) / 2 + yTextMargin,
                    mTextColor);
            //pCanvas.drawBitmap(schoolimg, (mCultureRect.left + mCultureRect.right) / 2
            //                - mTextColor.measureText("학교건물") / 2,
            //        (mCultureRect.top + mCultureRect.bottom) / 2 + yTextMargin,
            //        mTextColor);

            if (mDBHandler.getTheme().equals(DBHandler.MUSEUM)) {
                tPaint = mSelectedThemePaint;
            } else {
                tPaint = mThemePaint;
            }
            pCanvas.drawRoundRect(mMuseumRect, 10, 10, tPaint);
            pCanvas.drawText("문구류", (mMuseumRect.left + mMuseumRect.right) / 2
                            - mTextColor.measureText("문구류") / 2,
                    (mMuseumRect.top + mMuseumRect.bottom) / 2 + yTextMargin,
                    mTextColor);
            //pCanvas.drawBitmap(toyimg, (mMuseumRect.left + mMuseumRect.right) / 2
            //                - mTextColor.measureText("문구류") / 2,
            //        (mMuseumRect.top + mMuseumRect.bottom) / 2 + yTextMargin,
            //        mTextColor);

            if (mDBHandler.getTheme().equals(DBHandler.ALL)) {
                tPaint = mSelectedThemePaint;
            } else {
                tPaint = mThemePaint;
            }
            pCanvas.drawRoundRect(mAllRect, 10, 10, tPaint);
            pCanvas.drawText("모두",
                    (mAllRect.left + mAllRect.right) / 2 - mTextColor.measureText("모두")
                            / 2,
                    (mAllRect.top + mAllRect.bottom) / 2 + yTextMargin, mTextColor);
            //pCanvas.drawBitmap(allimg,
            //        (mAllRect.left + mAllRect.right) / 2 - mTextColor.measureText("모두")
            //                / 2,
            //        (mAllRect.top + mAllRect.bottom) / 2 + yTextMargin, mTextColor);

        }


    // 선택된 테마의 랜드마크를 그림
    // 현재의 위치정보와 랜드마크의 위치정보를 이용하여 두 위치간의 각도를 계산하고,
    // 현재 기기의 방향이 동쪽 기준 각도가 몇인지를 참고로
    // 기기 화면에 계속 새로고침됨
    // 두 위치간의 거리 또한 표시
    // 정면이 90도라하였을때 75도에서 105도 사이 30도가 시야각
    private PointF drawGrid(double tAx, double tAy, double tBx, double tBy,
                            Canvas pCanvas, Paint pPaint, String name, String theme) {
        // TODO Auto-generated method stub

        // 현재 위치와 랜드마크의 위치를 계산하는 공식
        double mXDegree = (double) (Math.atan((double) (tBy - tAy)
                / (double) (tBx - tAx)) * 180.0 / Math.PI);
        float mYDegree = mYCompassDegree; // 기기의 기울임각도

        // 4/4분면을 고려하여 0~360도가 나오게 설정
        if (tBx > tAx && tBy > tAy) {
            ;
        } else if (tBx < tAx && tBy > tAy) {
            mXDegree += 180;
        } else if (tBx < tAx && tBy < tAy) {
            mXDegree += 180;
        } else if (tBx > tAx && tBy < tAy) {
            mXDegree += 360;
        }

        // 두 위치간의 각도에 현재 스마트폰이 동쪽기준 바라보고 있는 방향 만큼 더해줌
        // 360도(한바퀴)가 넘었으면 한바퀴 회전한것이기에 360를 빼줌
        if (mXDegree + mXCompassDegree < 360) {
            mXDegree += mXCompassDegree;
        } else if (mXDegree + mXCompassDegree >= 360) {
            mXDegree = mXDegree + mXCompassDegree - 360;
        }

        // 계산된 각도 만큼 기기 정중앙 화면 기준 어디에 나타날지 계산함
        // 정중앙은 90도, 시야각은 30도로 75 ~ 105 사이일때만 화면에 나타남
        float mX = 0;
        float mY = 0;

        if (mXDegree > 75 && mXDegree < 105) {
            if (mYDegree > -180 && mYDegree < 0) {

                mX = (float) mWidth
                        - (float) ((mXDegree - 75) * ((float) mWidth / 30));

                mYDegree = -(mYDegree);

                mY = (float) (mYDegree * ((float) mHeight / 180));

            }

        }

        // 두 위치간의 거리를 계산함
        Location locationA = new Location("Point A");
        Location locationB = new Location("Point B");

        locationA.setLongitude(tAx);
        locationA.setLatitude(tAy);

        locationB.setLongitude(tBx);
        locationB.setLatitude(tBy);

        int distance = (int) locationA.distanceTo(locationB);

        Bitmap tIconBitmap = null;
        if (theme.equals("편의점")) {
            tIconBitmap = mPalaceIconBitmap;
        } else if (theme.equals("음식점")) {
            tIconBitmap = mCultureIconBitmap;
        } else if (theme.equals("카페")) {
            tIconBitmap = mShoppingIconBitmap;
        } else if (theme.equals("학교건물")) {
            tIconBitmap = mLandscapesIconBitmap;
        } else if (theme.equals("문구류")) {
            tIconBitmap = mMuseumIconBitmap;
        }

        int iconWidth, iconHeight;
        iconWidth = tIconBitmap.getWidth();
        iconHeight = tIconBitmap.getHeight();

        // 랜드마크에 해당하는 테마 아이콘과 이름, 거리를 그림
        // 거리는 1000미터 이하와 초과로 나누어 m, Km로 출력
        if (distance <= mVisibleDistance * 1000) {
            if (distance < 1000) {
                pCanvas.drawBitmap(tIconBitmap, mX - (iconWidth / 2), mY
                        - (iconHeight / 2), pPaint);

                pCanvas.drawText(name, mX - pPaint.measureText(name) / 2
                        + mShadowXMargin, mY + iconHeight / 2 + 30
                        + mShadowYMargin, mShadowPaint);

                pCanvas.drawText(name, mX - pPaint.measureText(name) / 2, mY
                        + iconHeight / 2 + 30, pPaint);

                pCanvas.drawText(distance + "m",
                        mX - pPaint.measureText(distance + "m") / 2
                                + mShadowXMargin, mY + iconHeight / 2 + 60
                                + mShadowYMargin, mShadowPaint);

                pCanvas.drawText(distance + "m",
                        mX - pPaint.measureText(distance + "m") / 2, mY
                                + iconHeight / 2 + 60, pPaint);

            } else if (distance >= 1000) {
                float fDistance = (float) distance / 1000;
                fDistance = (float) Math.round(fDistance * 10) / 10;

                pCanvas.drawBitmap(tIconBitmap, mX - (iconWidth / 2), mY
                        - (iconHeight / 2), pPaint);

                pCanvas.drawText(name, mX - pPaint.measureText(name) / 2
                        + mShadowXMargin, mY + iconHeight / 2 + 30
                        + mShadowYMargin, mShadowPaint);

                pCanvas.drawText(name, mX - pPaint.measureText(name) / 2, mY
                        + iconHeight / 2 + 30, pPaint);

                pCanvas.drawText(fDistance + "Km",
                        mX - pPaint.measureText(fDistance + "Km") / 2
                                + mShadowXMargin, mY + iconHeight / 2 + 60
                                + mShadowYMargin, mShadowPaint);

                pCanvas.drawText(fDistance + "Km",
                        mX - pPaint.measureText(fDistance + "Km") / 2, mY
                                + iconHeight / 2 + 60, pPaint);

            }
        }

        // 현재의 회전되기전의 좌표를 회전된 좌표로 변환한후 반환함
        PointF tPoint = new PointF();

        tPoint.set(mX - mWidth / 2, mY - mHeight / 2);
        return tPoint;
    }

    // DB를 해석하여 레코드를 하나씩 읽어
    // 그리는 함수를 호출
    private void interpretDB(Canvas pCanvas) {

        // TODO Auto-generated method stub
        double tAx, tAy, tBx, tBy;

        // 현재 위치를 알수 없을때는 임의의 위치를 현재 위치로 설정
        if (mCurrentGeoPoint != null) {
            tAx = (double) (mCurrentGeoPoint.getLongitudeE6()) / 1E6;
            tAy = (double) (mCurrentGeoPoint.getLatitudeE6()) / 1E6;
        } else {
            tAx = 126.95524;
            tAy = 37.602944;
        }

        mPointFList = new ArrayList<PointF>();
        mPointHashMap = new HashMap<Integer, String>();

        String tName;
        PointF tPoint;
        String tTheme;
        DBRecord tDBRecord;

        // DB를 하나씩 읽어 랜드마크를 화면에 그리는 함수 호출
        mDBRecordList = mDBHandler.getmDBRecordList();
        Iterator<DBRecord> dbRecordIterator = mDBRecordList.iterator();
        for (int i = 0; i < mDBRecordList.size(); i++) {
            tDBRecord = dbRecordIterator.next();
            if (tDBRecord != null) {
                tName = tDBRecord.getName();
                tBx = tDBRecord.getLongitude();
                tBy = tDBRecord.getLatitude();
                tTheme = tDBRecord.getTheme();

                // 화면에 그림
                tPoint = drawGrid(tAx, tAy, tBx, tBy, pCanvas, mPaint, tName,
                        tTheme);

                // 랜드마크 아이탬의 화면 위치를 리스트로 저장
                // 해시맵으로 아이템 번호와 이름을 저장
                // 랜드마크 아이템이 터치되었을때 어떤 아이템이 터치 되었는지 확인하기 위함
                mPointFList.add(tPoint);
                mPointHashMap.put(i, tName);
            }
        }
    }

    // 센서가 바뀔때마다 실행됨
    // 기기의 방향중 X, Y값을 저장하고 오버레이 화면을 다시 그리게 함
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {

            mXCompassDegree = event.values[0];
            mYCompassDegree = event.values[1];

            this.invalidate();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    // 카메라 액티비티가 소멸될때 센서 리스너를 해제
    public void viewDestory() {
        mSensorManager.unregisterListener(this);

    }

    // 카메라 액티비티에서 오버레이 화면 크기를 설정함
    public void setOverlaySize(int width, int height) {
        // TODO Auto-generated method stub
        mWidth = width;
        mHeight = height;

    }

    // 카메라 액티비티에서 현재 위치 정보를 알려줌
    public void setCurrentGeoPoint(GeoPoint currentGeoPoint,
                                   String addressString) {
        // TODO Auto-generated method stub
        mCurrentGeoPoint = currentGeoPoint;
        mCurrentGeoPointUpdated = true;
        mAdressString = addressString;

    }

    // 카메라 액티비티에서 현재 이용가능한 프로바이더들을 알려줌
    public void setProviderString(String providers) {
        // TODO Auto-generated method stub
        mProviders = providers;
    }

    // 카메라 액티비티에서 현재 사용하는 프로바이더를 알려줌
    public void setCurrentProvider(String provider) {
        // TODO Auto-generated method stub
        mCurrentProvider = provider;
    }

}