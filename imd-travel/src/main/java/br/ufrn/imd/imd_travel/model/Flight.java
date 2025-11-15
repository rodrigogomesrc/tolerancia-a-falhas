package br.ufrn.imd.imd_travel.model;

public class Flight {

    int flight;
    String day;

    // price in dollar
    float value;

    public Flight(int flight, String day, float value) {
        this.flight = flight;
        this.day = day;
        this.value = value;
    }

    // getters and setters
    public int getFlight() {
        return flight;
    }

    public void setFlight(int flight) {
        this.flight = flight;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String toString() {
        return "Flight{" +
                "flight=" + flight +
                ", day='" + day + '\'' +
                ", value=" + value +
                '}';
    }

}
