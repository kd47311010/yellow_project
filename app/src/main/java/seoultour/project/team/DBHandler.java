//	다운로드하여 저장된 db 파일을 관리함
// 
//	선택된 테마별로 db파일에서 레코드들을 읽어 리스트를 만들고 리턴함
//  
//	이때 레코드 마다 DBRecord 객체를 생성하여 리스트로 만듬

package seoultour.project.team;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHandler {

	private List<DBRecord> mDBRecordList;
	private String mTheme;
	private String mNewestDBInfo;
	private Object mContext;

	public static final String PALACE = "PALACE";
	public static final String LANDSCAPE = "LANDSCAPE";
	public static final String SHOPPING = "SHOPPING";
	public static final String CULTURE = "CULTURE";
	public static final String MUSEUM = "MUSEUM";
	public static final String ALL = "ALL";

	public DBHandler(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	// assets 폴더에 저장된 notice.txt 파일과 .db 파일을 해당하는 디렉토리로 복사함
	// assets 폴더 자체에서 db를 읽고 쓸수 없기에 복사한후 사용해야함
	public void copyDB() {
		// TODO Auto-generated method stub

		AssetManager assetManager = ((Context) mContext).getResources()
				.getAssets();

		OutputStream fos = null;
		InputStream fis = null;

		try {

			// notice.txt 파일 복사
			fis = assetManager.open("notice.txt");

			File file = new File("/data/data/seoultour.project.team/files/");
			file.mkdir();
			fos = new FileOutputStream(
					"/data/data/seoultour.project.team/files/notice.txt");

			byte[] buffer = new byte[1024];
			int readCount = 0;
			while (true) {
				readCount = fis.read(buffer, 0, 1024);

				if (readCount == -1) {
					break;
				}

				if (readCount < 1024) {
					fos.write(buffer, 0, readCount);
					break;
				}

				fos.write(buffer, 0, readCount);
			}
			fos.flush();

			fos.close();
			fis.close();

			FileReader fr;

			// 복사된 notice.txt 파일의 내용 한줄을 읽음
			// 그 내용은 현재 최신의 .db 파일 이름에 대한 정보
			fr = new FileReader(
					"/data/data/seoultour.project.team/files/notice.txt");
			BufferedReader br = new BufferedReader(fr);
			mNewestDBInfo = br.readLine();
			Log.i("readline", mNewestDBInfo);

			// 읽은 .db파일 이름에 대한 정보를 이용하여
			// .db 파일을 복사함
			fis = assetManager.open(mNewestDBInfo);

			file = new File("/data/data/seoultour.project.team/databases/");
			file.mkdir();
			fos = new FileOutputStream(
					"/data/data/seoultour.project.team/databases/"
							+ mNewestDBInfo);

			buffer = new byte[1024];
			readCount = 0;
			while (true) {
				readCount = fis.read(buffer, 0, 1024);

				if (readCount == -1) {
					break;
				}

				if (readCount < 1024) {
					fos.write(buffer, 0, readCount);
					break;
				}

				fos.write(buffer, 0, readCount);
			}
			fos.flush();

			fos.close();
			fis.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// db 파일을 읽음
	public void readDB() {

		mDBRecordList = new ArrayList<DBRecord>();

		// TODO Auto-generated method stub

		FileReader fr;

		// 최신의 .db파일 이름이 저장된 notice.txt 파일을 열어 내용을 읽음
		try {
			fr = new FileReader(
					"/data/data/seoultour.project.team/files/notice.txt");
			BufferedReader br = new BufferedReader(fr);
			mNewestDBInfo = br.readLine();
			Log.i("readline", mNewestDBInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 읽은 내용을 이용하여 .db 파일을 염
		SQLiteDatabase sqlDb = SQLiteDatabase.openDatabase(
				"/data/data/seoultour.project.team/databases/" + mNewestDBInfo,
				null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);

		String sqlStr = null;

		// 선택된 테마에 따라서 쿼리문이 달라짐
		if (mTheme.equals(PALACE)) {
			sqlStr = "select * from test where theme='고궁'";
		} else if (mTheme.equals(LANDSCAPE)) {
			sqlStr = "select * from test where theme='전경'";
		} else if (mTheme.equals(SHOPPING)) {
			sqlStr = "select * from test where theme='쇼핑'";
		} else if (mTheme.equals(CULTURE)) {
			sqlStr = "select * from test where theme='문화'";
		} else if (mTheme.equals(MUSEUM)) {
			sqlStr = "select * from test where theme='전시'";
		} else if (mTheme.equals(ALL)) {
			sqlStr = "select * from test";
		}

		// 커서를 레코드 하나씩 옮겨가면서 읽고
		// 레코드 리스트에 하나씩 추가함
		Cursor cursor = sqlDb.rawQuery(sqlStr, null);

		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {

			DBRecord dbRecord = new DBRecord();

			dbRecord.setTheme(cursor.getString(cursor.getColumnIndex("theme")));
			dbRecord.setName(cursor.getString(cursor.getColumnIndex("name")));
			dbRecord.setLatitude(cursor.getDouble(cursor
					.getColumnIndex("latitude")));
			dbRecord.setLongitude(cursor.getDouble(cursor
					.getColumnIndex("longitude")));
			dbRecord.setLocation(cursor.getString(cursor
					.getColumnIndex("location")));
			dbRecord.setImageurl(cursor.getString(cursor
					.getColumnIndex("imageurl")));
			dbRecord.setInfourl(cursor.getString(cursor
					.getColumnIndex("infourl")));
			dbRecord.setAbout(cursor.getString(cursor.getColumnIndex("about")));
			dbRecord.setEtc(cursor.getString(cursor.getColumnIndex("etc")));

			mDBRecordList.add(dbRecord);
			cursor.moveToNext();
		}

		cursor.close();
		sqlDb.close();
	}

	// 레코드 리스트를 반환함
	public List<DBRecord> getmDBRecordList() {
		return mDBRecordList;
	}

	// 테마를 선택함
	public void setTheme(String theme) {
		// TODO Auto-generated method stub
		mTheme = theme;
	}

	// 선택된 테마가 무엇인지 알려줌
	public String getTheme() {
		// TODO Auto-generated method stub
		return mTheme;
	}

	public String getDBInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
