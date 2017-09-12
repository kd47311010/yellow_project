// MainMenuActivity와 내용은 같다.
package graduation.sangmyung.project;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);
        overridePendingTransition(android.R.anim.slide_in_left, 0);

        if (getIntent().getExtras().getBoolean("timeout")) {
            Log.i(getLocalClassName(), "timeout");
            new DBHandler(this).copyDB();
        }
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        ImageView startView = (ImageView)findViewById(R.id.startView);
        Animation anim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.alpha_anim);
        startView.startAnimation(anim);

        startView.setOnClickListener(new View.OnClickListener() {

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
