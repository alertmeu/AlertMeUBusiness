package in.alertmeu.a4b.models;


public class AllSubCatModeDAO {
    private String subbc_id;
    private String maincat_id;
    private String subcategory_name;
    String subcategory_name_hindi;
    String image_path="";
    private String sequence = "";
    private boolean isSelected = false;


    public AllSubCatModeDAO() {

    }

    public AllSubCatModeDAO(String subbc_id, String maincat_id, String subcategory_name, String subcategory_name_hindi, String image_path, String sequence, boolean isSelected) {
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


}