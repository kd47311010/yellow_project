package graduation.sangmyung.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
/**
 * Created by hyunik on 2017-08-31.
 */

public class MarkerIconInfo_ListView_Adapter extends BaseAdapter{

    private ArrayList<RecordInfo> listViewItemList = new ArrayList<RecordInfo>() ;

    public MarkerIconInfo_ListView_Adapter(){

    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        LayoutInflater inflater;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.single_record, parent ,false);

        ImageView item_type_image = (ImageView)convertView.findViewById(R.id.imageView_type);
        TextView  item_type = (TextView) convertView.findViewById(R.id.item_type);
        TextView  content = (TextView) convertView.findViewById(R.id.discount_description);

        RecordInfo item = listViewItemList.get(position);

        LinearLayout item_real_images_layout = (LinearLayout) convertView.findViewById(R.id.item_show_list);
        for(int i = 0; i < item.getItem_imagesSize(); i++){
            inflater.inflate(item.getItem_images().get(i), item_real_images_layout,true);
        }

        item_type_image.setImageResource(item.getItem_type_image());
        item_type.setText(item.getItem_type());
        content.setText(item.getDiscount_content());

        return convertView;
    }

    public void addItem(int item_type_image, String item_type, String discount_content, ArrayList<Integer> item_images){
        RecordInfo item = new RecordInfo();
        item.setItem_type_image(item_type_image);
        item.setItem_type(item_type);
        item.setDiscount_content(discount_content);
        item.setItem_images(item_images);

        listViewItemList.add(item);
    }
}
