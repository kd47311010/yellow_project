//	 구글맵이나 카메라 오버레이뷰에서 터치하여 선택된 아이템의 
// 
//	 정보를 보여주는 액티비티, db에서 선택된 아이템을 찾아서 사진과, 설명, 링크 홈페이지를 보여줌

package seoultour.project.team;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoActivity extends Activity {

	private List<DBRecord> mDBRecordList = null;
	private DBRecord mDBRecord;
	private String mName;
	private String mEtc;
	private String mImageURL;
	private Iterator<DBRecord> mDBRecordIterator;
	private Bitmap mBitmap;
	private String mRecordName;
	private DBHandler mDBHandler;
	private String mLink;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		overridePendingTransition(android.R.anim.slide_in_left, 0);

		// 이전 액티비티에서 전달된 레코드 이름을 꺼냄
		mRecordName = getIntent().getExtras().getString("name");

		// DB를 초기화 시키고 읽음
		initDBHandler();
		interpretDB();

		TextView title = (TextView) findViewById(R.id.title);
		TextView etc = (TextView) findViewById(R.id.etc);
		ImageView imageurl = (ImageView) findViewById(R.id.imageurl);
		TextView link = (TextView) findViewById(R.id.link);

		// 레코드의 이미지 주소를 이용하여 해당하는 이미지를 읽음
		try {
			URL imageURL = new URL(mImageURL);
			HttpURLConnection conn = (HttpURLConnection) imageURL
					.openConnection();
			BufferedInputStream bis = new BufferedInputStream(
					conn.getInputStream(), 10240);
			mBitmap = BitmapFactory.decodeStream(bis);
			bis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 이미지 뷰, 텍스트뷰, 텍스트뷰 형식으로 화면에 표현함
		// 마지막 텍스트뷰는 웹페이지 링크

		title.setText(mName);
		etc.setText(mEtc);
		imageurl.setImageBitmap(mBitmap);
		link.setText(Html.fromHtml("<a href = " + mLink + ">" + "홈페이지 정보로 이동"
				+ "</a>"));
		link.setMovementMethod(LinkMovementMethod.getInstance());
	}

	// DB를 초기화하고 레코드들을 읽음
	private void initDBHandler() {
		mDBHandler = new DBHandler(this);
		mDBHandler.setTheme(DBHandler.ALL);
		mDBHandler.readDB();
		mDBRecordList = mDBHandler.getmDBRecordList();
	}

	// 읽은 레코드중 이름이 같은 레코드를 읽어
	// 해당 레코드의 정보들을 저장함
	private void interpretDB() {
		mDBRecordIterator = mDBRecordList.iterator();

		for (int i = 0; i < mDBRecordList.size(); i++) {
			mDBRecord = mDBRecordIterator.next();

			if (mDBRecord.getName().equals(mRecordName)) {
				mName = mDBRecord.getName();
				mImageURL = mDBRecord.getImageurl();
				mEtc = mDBRecord.getEtc();
				mLink = mDBRecord.getInfourl();
				break;
			}
		}
	}

	public void onBackPressed() {
		this.finish();
		overridePendingTransition(0, android.R.anim.slide_out_right);
	}

}