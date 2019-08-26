package ncku.appt3;

public class Draw_Property {
    //property basics
    private String i_date;
    private float f_value;

    //constructor
    public Draw_Property(String del,float value)
    {
        this.i_date = del;
        this.f_value = value;
    }

    //getters
    public String get_idate() { return i_date; }
    public float get_fvalue() {return f_value; }
}