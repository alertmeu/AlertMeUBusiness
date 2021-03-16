package in.alertmeu.a4b.models;


public class MainPriListModeDAO {
    private String id;
    private String category_name;
    private String business_user_id;
    private String status;

    public MainPriListModeDAO() {

    }

    public MainPriListModeDAO(String id, String category_name, String business_user_id) {
        this.id = id;
        this.category_name = category_name;
        this.business_user_id = business_user_id;
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

    public String getBusiness_user_id() {
        return business_user_id;
    }

    public void setBusiness_user_id(String business_user_id) {
        this.business_user_id = business_user_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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