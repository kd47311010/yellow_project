package graduation.sangmyung.project;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemActivity extends AppCompatActivity {

    TextView tellText;
    TextView nameText;
    TextView itemName;
    TextView itemCost;
    TextView eventContent;
    ImageView mainImage;
    ImageView iconImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Intent intent = getIntent();
        String theme = intent.getStringExtra("theme");

        tellText = (TextView) findViewById(R.id.tellText);
        nameText = (TextView) findViewById(R.id.nameText);
        itemName = (TextView) findViewById(R.id.itemName);
        itemCost = (TextView) findViewById(R.id.itemCost);
        eventContent = (TextView) findViewById(R.id.eventContent);
        mainImage = (ImageView) findViewById(R.id.mainImage);
        iconImage = (ImageView) findViewById(R.id.iconImage);

        if(theme.equals("편의점")){
            tellText.setText("02-379-8011");
            nameText.setText("GS25");
            itemName.setText("");
            itemCost.setText("");
            eventContent.setText("\t\t팝카드 결제시 10% 할인");
            mainImage.setImageResource(R.drawable.gs25);
            iconImage.setImageResource(R.mipmap.ic_gs25);
        } else if (theme.equals("카페")) {
            tellText.setText("02-3217-7000");
            nameText.setText("카페코스타");
            itemName.setText("\t\t아메리카노\n\t\t에스프레소\n\t\t콜드브루\n\t\t카푸치노\n\t\t카페라떼\n\t\t카페모카\n\t\t토피넛라떼\n\t\t바닐라라떼\n\t\t아포가토");
            itemCost.setText("\t\t2500\n\t\t2500\n\t\t4000\n\t\t3500\n\t\t3500\n\t\t4000\n\t\t4000\n\t\t4000\n\t\t4900");
            eventContent.setText("");
            mainImage.setImageResource(R.drawable.costa);
            iconImage.setImageResource(R.mipmap.ic_costa);
        } else if (theme.equals("학교건물")) {
            tellText.setText("02-2287-5501");
            nameText.setText("도서관");
            itemName.setText("");
            itemCost.setText("");
            eventContent.setText("\t\t최대 7일 대여가능");
            mainImage.setImageResource(R.drawable.library);
            iconImage.setImageResource(R.mipmap.ic_library);
        } else if (theme.equals("문구류")){
            tellText.setText("02-395-7353");
            nameText.setText("알파문구");
            itemName.setText("");
            itemCost.setText("");
            eventContent.setText("");
            mainImage.setImageResource(R.drawable.alpha);
            iconImage.setImageResource(R.mipmap.ic_alpha);
        }

        ImageButton callButton = (ImageButton) findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = tellText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" +number));
                startActivity(intent);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

