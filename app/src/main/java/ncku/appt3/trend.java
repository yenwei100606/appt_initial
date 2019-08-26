package ncku.appt3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;

public class trend extends AppCompatActivity{

    private final String tag = getClass().getName();

    MyDBHandler_re dbHandler;

    LineChart lineChart;
    private ArrayList<Property> rentalProperties = new ArrayList<>();
    private ArrayList<Property> intimeProperties = new ArrayList<>();
    private ArrayList<Draw_Property> drawProperties = new ArrayList<>();
    private ArrayList<Property> digProperties = new ArrayList<>();
    private List<Entry> catch_y = new ArrayList<>();
    private List<String> catch_x = new ArrayList<>();

    private Calendar Calendar1;
    private Calendar Calendar2;

    Date date;
    DateFormat sdf;
    private String str;
    double diff;
    int b;
    int count=0;
    List<LineDataSet> dataSets = new ArrayList<>();

    TextView trendTV,trendWarning;
    Switch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trend);

        dbHandler = new MyDBHandler_re(this, null, null, 1);
        rentalProperties=  dbHandler.databaseToString_value();

        trendTV = (TextView)findViewById(R.id.trendTV);
        trendWarning = findViewById(R.id.trendWarning);

        sdf = new SimpleDateFormat("yyyy/MM/dd mm:ss");

        lineChart = findViewById(R.id.chart);
        mSwitch = findViewById(R.id.trendSwitch);

        now();

        intimeProperties=DataInTime(7);
        trendTV.setText(Integer.toString(intimeProperties.size())+"筆資料");

        //draw
        draw_data();
        catch_y=getChartDatay(drawProperties.size());
        catch_x=getChartDatax(drawProperties.size());

        dataSets.add(getLineData(catch_y,"data1"));

        LineData mChartData = new LineData(catch_x, dataSets);
        lineChart.setData(mChartData);
        lineChart.setDescription("");

        trendWarning.setText(diagnosis(digProperties.size()));

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // If the switch button is on
                    //trendTV.setText("30 days");
                    clear_all();
                    intimeProperties=DataInTime(30);
                    draw_data();
                    catch_y=getChartDatay(drawProperties.size());
                    catch_x=getChartDatax(drawProperties.size());
                    dataSets.add(getLineData(catch_y,"data1"));
                    LineData mChartData = new LineData(catch_x, dataSets);
                    lineChart.notifyDataSetChanged(); // let the chart know it's data changed
                    lineChart.invalidate();
                    lineChart.setData(mChartData);
                }
                else{
                    // If the switch button is off
                    //trendTV.setText("7 days");
                    clear_all();
                    intimeProperties=DataInTime(7);
                    draw_data();
                    catch_y=getChartDatay(drawProperties.size());
                    catch_x=getChartDatax(drawProperties.size());
                    dataSets.add(getLineData(catch_y,"data1"));
                    LineData mChartData = new LineData(catch_x, dataSets);
                    lineChart.notifyDataSetChanged(); // let the chart know it's data changed
                    lineChart.invalidate();
                    lineChart.setData(mChartData);
                }
            }
        });
    }

    private void now(){
        Calendar1 = Calendar.getInstance();
        str = sdf.format(Calendar1.getTime());
    }

    private ArrayList<Property> DataInTime(int k){
        int month,day,a=0;

        ArrayList<Property> mList = new ArrayList<>();

        for(a=0 ;a < rentalProperties.size(); a++){
            Property property = rentalProperties.get(a);

            long milisec1 = Calendar1.getTimeInMillis();
            try{
                date =sdf.parse(property.get_sdate());
            }catch (ParseException e){
                Log.d(tag,"DataInTime error 1 ");
                e.printStackTrace();
            }

            Calendar2 = Calendar.getInstance();
            Calendar2.setTime(date);

            long milisec2=  Calendar2.getTimeInMillis();
            diff= (milisec1 - milisec2)/(60 * 1000.0);

            b=(int)diff;

            month = Calendar2.get(Calendar.MONTH) +1;
            day = Calendar2.get(Calendar.DAY_OF_MONTH);

            if(b<=(24*7*60)){
                digProperties.add(new Property(property.get_sdate(),property.get_svalue()));
            }

            if(b<=(24*k*60)){
                mList.add(new Property(Integer.toString(month) + "/" + Integer.toString(day),property.get_svalue()));
            }

            else{
                break;
            }
        }

        return mList;
    }

    private void draw_data(){
        //去除非10-255的值
        for(int a=0; a<intimeProperties.size(); a++){
            Property property = intimeProperties.get(a);
            try{
                if((Math.round(Float.parseFloat(property.get_svalue()))>10)&&(Math.round(Float.parseFloat(property.get_svalue()))<=255)){
                    drawProperties.add(new Draw_Property(property.get_sdate(),Float.parseFloat(property.get_svalue())));
                }
            }catch(InputMismatchException e1){
                Log.d(tag,"draw_data error1");
                e1.printStackTrace();
            }catch (NumberFormatException e2){
                Log.d(tag,"draw_data error2");
                e2.printStackTrace();
            }
        }
    }

    private List<Entry> getChartDatay(int k){
        List<Entry> chartData = new ArrayList<>();
        Draw_Property property;
        for(int i=0 ;i<k ;i++){
            property = drawProperties.get(k-1-i);
            chartData.add(new Entry(property.get_fvalue(), i));
        }

        return chartData;
    }

    private List<String> getChartDatax(int k){
        Draw_Property property;
        List<String> chartLabels = new ArrayList<>();
        for(int i=0 ;i<k ;i++){
            property = drawProperties.get(k-1-i);
            chartLabels.add(property.get_idate());
        }

        return chartLabels;
    }

    private LineDataSet getLineData(List<Entry> datay, String name){
        LineDataSet dataSetA = new LineDataSet(datay, name);
        return dataSetA;
    }

    private String diagnosis(int num){
        ArrayList<Property>buffer = new ArrayList<>();
        List<String> tri = new ArrayList<>();

        double a1,a2;
        String S1="",S2;
        boolean B1=false,B2=false,B3=false;
        Property property1,property2;
        int target=-1;
        String output="數據量不足";
        double k=-15.0;//

        int month,day,year;

        if(num >= 2) {
            //排回正序
            for (int a = 0; a <= num - 1; a++) {
                property1 = digProperties.get(num - 1 - a);
                buffer.add(property1);
            }
            output = "數據量";
            for (int a = 0; a < num - 1; a++) {
                property1 = buffer.get(a);
                a1 = Double.parseDouble(property1.get_svalue());
                property2 = buffer.get(a + 1);
                a2 = Double.parseDouble(property2.get_svalue());
                output = property1.get_svalue();
                if (a2 - a1 <= k) {
                    tri.add("1");//濃度高過閾值
                    target = a;
                } else {
                    if (a2 - a1 > 0.0) {
                        tri.add("-1");//濃度更低
                    } else {
                        tri.add("0");
                    }
                }
            }
            //峰値位置
            for (int a = 0; a < num - 1; a++){
                output="數據量555";
                S1=tri.get(a);
                output=S1;
                if(S1=="1")
                {
                    output="數據量t";
                    B1=true;
                }
                else
                {
                    if(B1==true)
                    {
                        count++;
                    }
                }
            }
            if(B1==true&&count==0){
                Property property = buffer.get(target+1);
                try{
                    Calendar cal= Calendar.getInstance();
                    date =sdf.parse(property.get_sdate());
                    cal.setTime(date);
                    cal.add(Calendar.DATE, +2);
                    str = sdf.format(cal.getTime());
                    month = cal.get(Calendar.MONTH) +1;
                    day = cal.get(Calendar.DAY_OF_MONTH);
                    year =cal.get(Calendar.YEAR);

                    output=Integer.toString(year)+"/"+Integer.toString(month)+"/"+Integer.toString(day);
                }catch(ParseException e){
                    Log.d(tag,"111 error");
                    e.printStackTrace();
                }
            }
            else{
                if(B1==true&&count==1){
                    Property property = buffer.get(target+1);
                    try{
                        Calendar cal= Calendar.getInstance();
                        date =sdf.parse(property.get_sdate());
                        cal.setTime(date);
                        cal.add(Calendar.DATE, +1);
                        str = sdf.format(cal.getTime());
                        month = cal.get(Calendar.MONTH) +1;
                        day = cal.get(Calendar.DAY_OF_MONTH);
                        year =cal.get(Calendar.YEAR);

                        output=Integer.toString(year)+"/"+Integer.toString(month)+"/"+Integer.toString(day);
                    }catch (ParseException e){
                        Log.d(tag,"222 error");
                        e.printStackTrace();
                    }
                }
                else{
                    if(B1==true){
                        output="排卵期已過";
                    }
                }
            }
        }
        return output;
    }

    public void backtoHome(View v){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

    public void clear_all(){
        //clear all thing where is necessary for refresh the chart
        intimeProperties.clear();
        drawProperties.clear();
        digProperties.clear();
        catch_y.clear();
        catch_x.clear();
        dataSets.clear();
    }
}