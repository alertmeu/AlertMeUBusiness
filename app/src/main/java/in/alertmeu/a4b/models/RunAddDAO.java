package in.alertmeu.a4b.models;

public class RunAddDAO {
    String unit = "";
    String timings = "";

    public RunAddDAO() {

    }

    public RunAddDAO(String unit, String timings) {
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
        if (obj instanceof RunAddDAO) {
            RunAddDAO c = (RunAddDAO) obj;
            if (c.getTimings().equals(timings) && c.getUnit() == unit) return true;
        }

        return false;
    }
}
