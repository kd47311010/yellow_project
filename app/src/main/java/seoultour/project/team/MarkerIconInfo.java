package seoultour.project.team;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;
import java.util.ArrayList;


/**
 * Created by choidaek on 2017-07-01.
 */

public  class MarkerIconInfo extends Activity {

    public int type_image;
    public String type;
    public String content;
    public ArrayList<Integer> images;
    public MarkerIconInfo_ListView_Adapter adapter;
    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_info);

        /*adapter = new MarkerIconInfo_ListView_Adapter();

        type_image = getIntent().getExtras().getInt("type_image");
        type = getIntent().getExtras().getString("type");
        content = getIntent().getExtras().getString("content");
        images = getIntent().getExtras().getIntegerArrayList("item_images");

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        adapter.addItem(type_image,type, content, images);*/
    }


}
