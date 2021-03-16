package in.alertmeu.a4b.models;

public class TransactionHistoryDAO {
    String id = "";
    String business_user_id = "";
    String description = "";
    String amount = "";
    String previous_balance = "";
    String current_balance = "";
    String date_time = "";

    public TransactionHistoryDAO() {

    }

    public TransactionHistoryDAO(String id, String business_user_id, String description, String amount, String previous_balance, String current_balance, String date_time) {
        this.id = id;
        this.business_user_id = business_user_id;
        this.description = description;
        this.amount = amount;
        this.previous_balance = previous_balance;
        this.current_balance = current_balance;
        this.date_time = date_time;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrevious_balance() {
        return previous_balance;
    }

    public void setPrevious_balance(String previous_balance) {
        this.previous_balance = previous_balance;
    }

    public String getCurrent_balance() {
        return current_balance;
    }

    public void setCurrent_balance(String current_balance) {
        this.current_balance = current_balance;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

}
