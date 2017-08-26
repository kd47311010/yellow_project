//	메인메뉴 액티비티
//
//	테마여행, 내위치, 편의정보를 선택할 수 있게 해주는 액티비티
//
//	터치시 해당 액티비티로 전환

package seoultour.project.team;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainMenuActivity extends Activity {


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO Auto-generated method stub
		setContentView(R.layout.mainmenu);
		overridePendingTransition(android.R.anim.slide_in_left, 0);

		// 서버에 접속하지 못하여 .db 파일을 다운로드 받지 못하였을때
		// 기존 어플리케이션에 포함된 .db 파일을 사용
		if (getIntent().getExtras().getBoolean("timeout") == true) {
			Log.i(getLocalClassName(), "timeout");
			new DBHandler(this).copyDB();
		}

		//Toast.makeText(this, "DB 다운로드 완료!", Toast.LENGTH_SHORT).show();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// 테마여행, 내위치, 편의정보 메뉴
		TextView startView = (TextView)findViewById(R.id.startView);
		startView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getBaseContext(), CameraActivity.class));

			}

		});
	}

	// 뒤로가기 버튼을 터치했을때
	// AlertDiallog를 띄우고 프로그램 종료 결정
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e("onKeyDown", "" + keyCode);
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("프로그램을 종료하시겠습니까?")
					.setCancelable(false)
					.setPositiveButton("예",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									finish();
									overridePendingTransition(0,
											android.R.anim.slide_out_right);
								}
							})
					.setNegativeButton("아니오",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							});
			builder.create().show();
		}

		return false;
	}
}
