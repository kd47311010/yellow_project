package graduation.sangmyung.project;

import java.util.ArrayList;
/**
 * Created by hyunik on 2017-08-31.
 */

public class RecordInfo {
    private int item_type_image;
    private String item_type;
    private String discount_content;
    private ArrayList<Integer> item_images;

    public RecordInfo(){}

/*    public RecordInfo(int item_type_image, String item_type, String discount_content, ArrayList<Integer> item_images){
        this.item_type_image = item_type_image;
        this.item_type = item_type;
        this.discount_content = discount_content;
        this.item_images = item_images;
    }*/

    public int getItem_type_image() {
        return item_type_image;
    }

    public String getItem_type() {
        return item_type;
    }

    public String getDiscount_content() {
        return discount_content;
    }

    public int getItem_imagesSize(){
        return item_images.size();
    }


    public ArrayList<Integer> getItem_images() {
        return item_images;
    }

    public void setItem_type_image(int item_type_image) {
        this.item_type_image = item_type_image;
    }

    public void setItem_type(String item_type) {
        this.item_type = item_type;
    }

    public void setDiscount_content(String discount_content) {
        this.discount_content = discount_content;
    }

    public void setItem_images(ArrayList<Integer> item_images) {
        this.item_images = item_images;
    }
}
