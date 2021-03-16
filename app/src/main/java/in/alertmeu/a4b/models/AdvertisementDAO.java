package in.alertmeu.a4b.models;

public class AdvertisementDAO {
    String id = "";
    String business_user_id = "";
    String title = "";
    String description = "";
    String describe_limitations = "";
    String rq_code = "";
    String active_status = "";
    String click_count = "";
    String total_views="";
    String total_redeemed="";
    String create_at = "";
    String s_date = "";
    String s_time = "";
    String e_date = "";
    String e_time = "";
    String paid_amount = "";
    String total_time="";
    String original_image_path = "";
    String modify_image_path = "";
    String likecnt = "";
    String dislikecnt = "";
    String numbers = "";
    String business_main_category = "";
    String business_subcategory = "";
    String tunit="";
    String tsign="";
    String active_user_count="";
    String business_main_category_hindi="";
    String business_subcategory_hindi="";
    private boolean isSelected;

    public AdvertisementDAO() {
    }

    public AdvertisementDAO(String id, String business_user_id, String title, String description, String describe_limitations, String rq_code, String active_status, String click_count, String total_views, String total_redeemed, String create_at, String s_date, String s_time, String e_date, String e_time, String paid_amount, String total_time, String original_image_path, String modify_image_path, String likecnt, String dislikecnt, String numbers, String business_main_category, String business_subcategory, String tunit, String tsign, String active_user_count, boolean isSelected) {
        this.id = id;
        this.business_user_id = business_user_id;
        this.title = title;
        this.description = description;
        this.describe_limitations = describe_limitations;
        this.rq_code = rq_code;
        this.active_status = active_status;
        this.click_count = click_count;
        this.total_views = total_views;
        this.total_redeemed = total_redeemed;
        this.create_at = create_at;
        this.s_date = s_date;
        this.s_time = s_time;
        this.e_date = e_date;
        this.e_time = e_time;
        this.paid_amount = paid_amount;
        this.total_time = total_time;
        this.original_image_path = original_image_path;
        this.modify_image_path = modify_image_path;
        this.likecnt = likecnt;
        this.dislikecnt = dislikecnt;
        this.numbers = numbers;
        this.business_main_category = business_main_category;
        this.business_subcategory = business_subcategory;
        this.tunit = tunit;
        this.tsign = tsign;
        this.active_user_count = active_user_count;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusiness_user_id() {
        return business_user_id;
    }

    public void setBusiness_user_id(String business_user_id) {
        this.business_user_id = business_user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescribe_limitations() {
        return describe_limitations;
    }

    public void setDescribe_limitations(String describe_limitations) {
        this.describe_limitations = describe_limitations;
    }

    public String getRq_code() {
        return rq_code;
    }

    public void setRq_code(String rq_code) {
        this.rq_code = rq_code;
    }

    public String getActive_status() {
        return active_status;
    }

    public void setActive_status(String active_status) {
        this.active_status = active_status;
    }

    public String getClick_count() {
        return click_count;
    }

    public void setClick_count(String click_count) {
        this.click_count = click_count;
    }

    public String getTotal_views() {
        return total_views;
    }

    public void setTotal_views(String total_views) {
        this.total_views = total_views;
    }

    public String getTotal_redeemed() {
        return total_redeemed;
    }

    public void setTotal_redeemed(String total_redeemed) {
        this.total_redeemed = total_redeemed;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getS_date() {
        return s_date;
    }

    public void setS_date(String s_date) {
        this.s_date = s_date;
    }

    public String getS_time() {
        return s_time;
    }

    public void setS_time(String s_time) {
        this.s_time = s_time;
    }

    public String getE_date() {
        return e_date;
    }

    public void setE_date(String e_date) {
        this.e_date = e_date;
    }

    public String getE_time() {
        return e_time;
    }

    public void setE_time(String e_time) {
        this.e_time = e_time;
    }

    public String getPaid_amount() {
        return paid_amount;
    }

    public void setPaid_amount(String paid_amount) {
        this.paid_amount = paid_amount;
    }

    public String getTotal_time() {
        return total_time;
    }

    public void setTotal_time(String total_time) {
        this.total_time = total_time;
    }

    public String getOriginal_image_path() {
        return original_image_path;
    }

    public void setOriginal_image_path(String original_image_path) {
        this.original_image_path = original_image_path;
    }

    public String getModify_image_path() {
        return modify_image_path;
    }

    public void setModify_image_path(String modify_image_path) {
        this.modify_image_path = modify_image_path;
    }

    public String getLikecnt() {
        return likecnt;
    }

    public void setLikecnt(String likecnt) {
        this.likecnt = likecnt;
    }

    public String getDislikecnt() {
        return dislikecnt;
    }

    public void setDislikecnt(String dislikecnt) {
        this.dislikecnt = dislikecnt;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public String getBusiness_main_category() {
        return business_main_category;
    }

    public void setBusiness_main_category(String business_main_category) {
        this.business_main_category = business_main_category;
    }

    public String getBusiness_subcategory() {
        return business_subcategory;
    }

    public void setBusiness_subcategory(String business_subcategory) {
        this.business_subcategory = business_subcategory;
    }

    public String getTunit() {
        return tunit;
    }

    public void setTunit(String tunit) {
        this.tunit = tunit;
    }

    public String getTsign() {
        return tsign;
    }

    public void setTsign(String tsign) {
        this.tsign = tsign;
    }

    public String getActive_user_count() {
        return active_user_count;
    }

    public void setActive_user_count(String active_user_count) {
        this.active_user_count = active_user_count;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getBusiness_main_category_hindi() {
        return business_main_category_hindi;
    }

    public void setBusiness_main_category_hindi(String business_main_category_hindi) {
        this.business_main_category_hindi = business_main_category_hindi;
    }

    public String getBusiness_subcategory_hindi() {
        return business_subcategory_hindi;
    }

    public void setBusiness_subcategory_hindi(String business_subcategory_hindi) {
        this.business_subcategory_hindi = business_subcategory_hindi;
    }
}
