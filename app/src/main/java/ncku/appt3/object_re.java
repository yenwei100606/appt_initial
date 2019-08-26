package ncku.appt3;

public class object_re {
    private int _id;
    private String _date;
    private String _value;

    //Added this empty constructor in lesson 50 in case we ever want to create the object and assign it later.

    public object_re(String value,String date)
    {
        this._value = value;
        this._date = date;
    }

    public String get_value()
    {
        return _value;
    }

    public void set_value(String value)
    {
        this._value = value;
    }

    public int get_id()
    {
        return _id;
    }

    public void set_id(int _id)
    {
        this._id = _id;
    }

    public String get_date()
    {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }
}
