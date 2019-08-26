package ncku.appt3;

public class Property {
    //property basics
    private String s_date;
    private String s_value;

    //constructor
    public Property(String date, String value)
    {
        this.s_date = date;
        this.s_value = value;
    }

    //getters
    public String get_sdate() { return s_date; }
    public String get_svalue() {return s_value; }
}
