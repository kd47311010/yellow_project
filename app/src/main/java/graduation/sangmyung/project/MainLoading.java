// SeoulTourActivity와 내용은 같다.
package graduation.sangmyung.project;

/**
 * Created by hyunik on 2017-08-31.
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class MainLoading extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // 왼쪽에서 미끄러지면서 나타나는 효과
        overridePendingTransition(android.R.anim.slide_in_left, 0);

        // 로딩 스레드
        Loading loading = new Loading(this);
        new Thread(loading).start();
        //Toast.makeText(this, "DB 다운로드 중...", Toast.LENGTH_SHORT).show();
    }

    public void onBackPressed() {
        this.finish();
        // 오른쪽으로 미끄러지면서 사라지는 효과
        overridePendingTransition(0, android.R.anim.slide_out_right);
    }
}