package ncku.appt3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView mainImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainImage = findViewById(R.id.mainImage);
    }

    public void clickFOB(View v){
        Intent i = new Intent(this,FOB.class);
        startActivity(i);
    }

    public void clickURS(View v){
        Intent i = new Intent(this,URS.class);
        startActivity(i);
    }

    public void clickHIS(View v){
        Intent i = new Intent(this,history_re.class);
        startActivity(i);
    }

    public void clickTRE(View v){
        Intent i = new Intent(this,trend.class);
        startActivity(i);
    }
}
