package in.alertmeu.a4b.models;

public class DateTimeDAO {
    String unit = "";
    String timings = "";

    public DateTimeDAO() {

    }

    public DateTimeDAO(String unit, String timings) {
        this.unit = unit;
        this.timings = timings;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTimings() {
        return timings;
    }

    public void setTimings(String timings) {
        this.timings = timings;
    }

    @Override
    public String toString() {
        return timings;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DateTimeDAO) {
            DateTimeDAO c = (DateTimeDAO) obj;
            if (c.getTimings().equals(timings) && c.getUnit() == unit) return true;
        }

        return false;
    }
}
