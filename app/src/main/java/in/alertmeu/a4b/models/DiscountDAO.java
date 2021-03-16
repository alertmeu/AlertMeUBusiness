package in.alertmeu.a4b.models;

public class DiscountDAO {
    String unit = "";
    String discount = "";

    public DiscountDAO() {

    }

    public DiscountDAO(String unit, String discount) {
        this.unit = unit;
        this.discount = discount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return discount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DiscountDAO) {
            DiscountDAO c = (DiscountDAO) obj;
            if (c.getDiscount().equals(discount) && c.getUnit() == unit) return true;
        }

        return false;
    }
}
