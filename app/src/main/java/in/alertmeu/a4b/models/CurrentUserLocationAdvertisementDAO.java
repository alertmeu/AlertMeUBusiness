package in.alertmeu.a4b.models;

public class CurrentUserLocationAdvertisementDAO {
    String business_user_id = "";
    String subbc_id = "";
    String subcategory_name = "";
    String subcategory_name_hindi = "";
    String maincat_id = "";
    String category_name = "";
    String category_name_hindi = "";
    String latitude = "";
    String longitude = "";
    String entry_date = "";
    String time = "";
    String fcm_id = "";
    String location_name = "";
    String user_distance = "";
    String user_count = "";
    String numbers = "";
    String main_image_path = "";
    String sub_image_path = "";
    private boolean isSelected;

    public CurrentUserLocationAdvertisementDAO() {
    }

    public CurrentUserLocationAdvertisementDAO(String business_user_id, String subbc_id, String subcategory_name, String maincat_id, String category_name, String latitude, String longitude, String entry_date, String time, String fcm_id, String location_name, String user_distance, String user_count, String numbers, boolean isSelected) {
        this.business_user_id = business_user_id;
        this.subbc_id = subbc_id;
        this.subcategory_name = subcategory_name;
        this.maincat_id = maincat_id;
        this.category_name = category_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.entry_date = entry_date;
        this.time = time;
        this.fcm_id = fcm_id;
        this.location_name = location_name;
        this.user_distance = user_distance;
        this.user_count = user_count;
        this.numbers = numbers;
        this.isSelected = isSelected;
    }

    public String getBusiness_user_id() {
        return business_user_id;
    }

    public void setBusiness_user_id(String business_user_id) {
        this.business_user_id = business_user_id;
    }

    public String getSubbc_id() {
        return subbc_id;
    }

    public void setSubbc_id(String subbc_id) {
        this.subbc_id = subbc_id;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public void setSubcategory_name(String subcategory_name) {
        this.subcategory_name = subcategory_name;
    }

    public String getMaincat_id() {
        return maincat_id;
    }

    public void setMaincat_id(String maincat_id) {
        this.maincat_id = maincat_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFcm_id() {
        return fcm_id;
    }

    public void setFcm_id(String fcm_id) {
        this.fcm_id = fcm_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getUser_distance() {
        return user_distance;
    }

    public void setUser_distance(String user_distance) {
        this.user_distance = user_distance;
    }

    public String getUser_count() {
        return user_count;
    }

    public void setUser_count(String user_count) {
        this.user_count = user_count;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSubcategory_name_hindi() {
        return subcategory_name_hindi;
    }

    public void setSubcategory_name_hindi(String subcategory_name_hindi) {
        this.subcategory_name_hindi = subcategory_name_hindi;
    }

    public String getCategory_name_hindi() {
        return category_name_hindi;
    }

    public void setCategory_name_hindi(String category_name_hindi) {
        this.category_name_hindi = category_name_hindi;
    }

    public String getMain_image_path() {
        return main_image_path;
    }

    public void setMain_image_path(String main_image_path) {
        this.main_image_path = main_image_path;
    }

    public String getSub_image_path() {
        return sub_image_path;
    }

    public void setSub_image_path(String sub_image_path) {
        this.sub_image_path = sub_image_path;
    }
}
