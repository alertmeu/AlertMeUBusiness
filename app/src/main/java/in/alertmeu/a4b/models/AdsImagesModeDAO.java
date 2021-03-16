package in.alertmeu.a4b.models;


import java.util.ArrayList;

public class AdsImagesModeDAO {

    String image_path = "";

    public AdsImagesModeDAO() {

    }

    public AdsImagesModeDAO(String image_path) {
        this.image_path = image_path;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
}