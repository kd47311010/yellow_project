package graduation.sangmyung.project;

/**
 * Created by hyunik on 2017-08-31.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


public class LocationSettingRequest extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationsettingrequest);

        // 현재 위치 확인을 위한 설정 변경을 물어본후
        // '예'를 터치시 시스템 설정 중 위치서비스 페이지로 이동

        Toast.makeText(getBaseContext(), "현재 위치 확인을 위한 설정 변경이 필요합니다.",
                Toast.LENGTH_LONG).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("위치 서비스 변경")
                .setCancelable(false)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(
                                "android.settings.LOCATION_SOURCE_SETTINGS"));
                        finish();
                        overridePendingTransition(0,
                                android.R.anim.slide_out_right);
                    }
                })
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                overridePendingTransition(0,
                                        android.R.anim.slide_out_right);
                            }
                        });
        builder.create().show();
        // TODO Auto-generated method stub
    }

    public void onBackPressed() {
        this.finish();
        overridePendingTransition(0, android.R.anim.slide_out_right);
    }
}
