package in.alertmeu.a4b.models;


import java.util.ArrayList;

public class MainCatModeDAO {
    private String id;
    private String category_name;
    private String category_name_hindi;
    private String currency_sign;
    private String ads_pricing;
    private String discount;
    private String qrcode = "";
    String isselected = "";
    String image_path="";
    String checked_status = "";
    private String notification_charge;
    private String price_formula;
    private String rounding_scale;
    private String special_message;
    private String special_message_hindi;
    private String defalut_image;
    private boolean isSelected = false;
    private ArrayList<SubCatModeDAO> list = new ArrayList<SubCatModeDAO>();

    public MainCatModeDAO() {

    }

    public MainCatModeDAO(String id, String category_name) {
        this.id = id;
        this.category_name = category_name;

    }



    public MainCatModeDAO(String id, String category_name, String currency_sign, String ads_pricing, String discount, String notification_charge, String price_formula, String rounding_scale, String special_message,String defalut_image) {
        this.id = id;
        this.category_name = category_name;
        this.currency_sign = currency_sign;
        this.ads_pricing = ads_pricing;
        this.discount = discount;
        this.notification_charge = notification_charge;
        this.price_formula = price_formula;
        this.rounding_scale = rounding_scale;
        this.special_message = special_message;
        this.defalut_image = defalut_image;
    }

    public MainCatModeDAO(String id, String category_name, String isselected, String checked_status, boolean isSelected, ArrayList<SubCatModeDAO> list) {
        this.id = id;
        this.category_name = category_name;
        this.isselected = isselected;
        this.checked_status = checked_status;
        this.isSelected = isSelected;
        this.list = list;
    }


    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getIsselected() {
        return isselected;
    }

    public void setIsselected(String isselected) {
        this.isselected = isselected;
    }

    public String getChecked_status() {
        return checked_status;
    }

    public void setChecked_status(String checked_status) {
        this.checked_status = checked_status;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ArrayList<SubCatModeDAO> getList() {
        return list;
    }

    public void setList(ArrayList<SubCatModeDAO> list) {
        this.list = list;
    }

    public String getCurrency_sign() {
        return currency_sign;
    }

    public void setCurrency_sign(String currency_sign) {
        this.currency_sign = currency_sign;
    }

    public String getAds_pricing() {
        return ads_pricing;
    }

    public void setAds_pricing(String ads_pricing) {
        this.ads_pricing = ads_pricing;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCategory_name_hindi() {
        return category_name_hindi;
    }

    public void setCategory_name_hindi(String category_name_hindi) {
        this.category_name_hindi = category_name_hindi;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getNotification_charge() {
        return notification_charge;
    }

    public void setNotification_charge(String notification_charge) {
        this.notification_charge = notification_charge;
    }

    public String getPrice_formula() {
        return price_formula;
    }

    public void setPrice_formula(String price_formula) {
        this.price_formula = price_formula;
    }

    public String getRounding_scale() {
        return rounding_scale;
    }

    public void setRounding_scale(String rounding_scale) {
        this.rounding_scale = rounding_scale;
    }

    public String getSpecial_message() {
        return special_message;
    }

    public void setSpecial_message(String special_message) {
        this.special_message = special_message;
    }

    public String getSpecial_message_hindi() {
        return special_message_hindi;
    }

    public void setSpecial_message_hindi(String special_message_hindi) {
        this.special_message_hindi = special_message_hindi;
    }

    public String getDefalut_image() {
        return defalut_image;
    }

    public void setDefalut_image(String defalut_image) {
        this.defalut_image = defalut_image;
    }

    @Override
    public String toString() {
        return category_name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MainCatModeDAO) {
            MainCatModeDAO c = (MainCatModeDAO) obj;
            if (c.getCategory_name().equals(category_name) && c.getId() == id) return true;
        }

        return false;
    }

}