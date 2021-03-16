package in.alertmeu.a4b.models;


public class AllPriceListSubCatModeDAO {
    private String id;
    private String title;
    private String description;
    private String rate;
    private String link;
    private String image_path;
    private String subbc_id;
    private String maincat_id;
    private String subcategory_name;
    private String business_user_id;
    private String mainid_pricelist;
    String subcategory_name_hindi;
    private String sequence = "";
    private boolean isSelected = false;


    public AllPriceListSubCatModeDAO() {
    }
    public AllPriceListSubCatModeDAO(String subbc_id, String maincat_id, String subcategory_name, String subcategory_name_hindi, String image_path, String sequence, boolean isSelected) {
        this.subbc_id = subbc_id;
        this.maincat_id = maincat_id;
        this.subcategory_name = subcategory_name;
        this.subcategory_name_hindi = subcategory_name_hindi;
        this.image_path = image_path;
        this.sequence = sequence;
        this.isSelected = isSelected;
    }

    public String getSubbc_id() {
        return subbc_id;
    }

    public void setSubbc_id(String subbc_id) {
        this.subbc_id = subbc_id;
    }

    public String getMaincat_id() {
        return maincat_id;
    }

    public void setMaincat_id(String maincat_id) {
        this.maincat_id = maincat_id;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public void setSubcategory_name(String subcategory_name) {
        this.subcategory_name = subcategory_name;
    }

    public String getSubcategory_name_hindi() {
        return subcategory_name_hindi;
    }

    public void setSubcategory_name_hindi(String subcategory_name_hindi) {
        this.subcategory_name_hindi = subcategory_name_hindi;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBusiness_user_id() {
        return business_user_id;
    }

    public void setBusiness_user_id(String business_user_id) {
        this.business_user_id = business_user_id;
    }

    public String getMainid_pricelist() {
        return mainid_pricelist;
    }

    public void setMainid_pricelist(String mainid_pricelist) {
        this.mainid_pricelist = mainid_pricelist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}