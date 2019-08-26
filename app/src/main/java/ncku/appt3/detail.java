package ncku.appt3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.InputMismatchException;

public class detail extends AppCompatActivity{

    private Button back;
    private Button delete;
    private final String tag = getClass().getName();

    MyDBHandler_re dbHandler;
    TextView t_date;
    TextView t_value;
    TextView t_id;
    String s_date;
    String s_value;
    String id;

    double r_1=180;
    double r_2=210;
    double r_3=240;
    double d_a;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        //不知咋的，value和date反了呢
        s_value=getIntent().getExtras().getString("date");
        s_date=getIntent().getExtras().getString("value");
        id=getIntent().getStringExtra("int");

        t_date=(TextView)findViewById(R.id.date);
        t_id=(TextView)findViewById(R.id.id);
        t_value=(TextView)findViewById(R.id.value);
        t_date.setText(s_date);

        try{
            d_a=Double.parseDouble( s_value);
            t_value.setText( s_value);
            if(d_a<r_1){
                t_id.setText("Detail : \n" +
                        "Concentration  ->  High ");
            }
            if(d_a>=r_1&&d_a<r_2){
                t_id.setText("Detail : \n" +
                        "Concentration  ->  Medium to high");
            }
            if(d_a>=r_2&&d_a<r_3){
                t_id.setText("Detail : \n" +
                        "Concentration  ->  Medium to low");
            }
            if(d_a>=r_3){
                t_id.setText("Detail : \n" +
                        "Concentration  ->  Low");
            }
            if(d_a < 50){
                t_id.setText("Detail : \n" +
                        "Something we don't expect happened");
            }
        }catch(InputMismatchException ex){
            t_value.setText( " 1 Error");
            t_id.setText("Detail : \n" + " 1 Error");
        }catch (NumberFormatException ex){
            t_value.setText( " 2 Error");
            t_id.setText("Detail : \n" + " 2 Error");
        }catch (NullPointerException ex){
            t_value.setText( " 3 Error");
            t_id.setText("Detail : \n" + " 3 Error");
        }

        dbHandler = new MyDBHandler_re(this, null, null, 1);

        delete = (Button)findViewById(R.id.detailDel);

        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                detail.this.finish();
            }
        });
    }



    public void yes(){
        dbHandler.deleteProduct(id);
        Intent i=new Intent(this,history_re.class);
        startActivity(i);
    }

    public void detailDel(View v){

        new AlertDialog.Builder(detail.this)
                .setTitle("Remove Data")//設定視窗標題
                .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                .setMessage("Sure you want to delete this data,buddy?")//設定顯示的文字
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            yes();
                        }catch(NullPointerException e){
                            Log.d(tag,"Fail deleting");
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }
}
