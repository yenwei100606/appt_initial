package ncku.appt3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class uploadconfirm extends AppCompatActivity {

    private Button ucBack;
    TextView ucTV,ucTV2;
    String message,imei;
    private final String tag = getClass().getName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadconfirm);

        Bundle Data = getIntent().getExtras();
        if(Data == null){
            Toast.makeText(this, "Take picture again", Toast.LENGTH_SHORT).show();
            uploadconfirm.this.finish();
        }

        message = Data.getString("picpath");
        Toast.makeText(this, "path = "+message , Toast.LENGTH_SHORT).show();
        loadImageFromStorage(message);
        imei();

        ucBack = (Button)findViewById(R.id.ucBack);
        ucBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                uploadconfirm.this.finish();
            }
        });
    }

    private void loadImageFromStorage(String path){
        try{
            File f = new File(path,"profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img = (ImageView)findViewById(R.id.ucImageView);
            img.setImageBitmap(b);
        }catch (FileNotFoundException e){
            Log.d(tag,"File not found");
            e.printStackTrace();
        }
    }

    private void imei(){
        TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);

        if(ContextCompat.checkSelfPermission(uploadconfirm.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(uploadconfirm.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(uploadconfirm.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }else{
            imei = telephonyManager.getImei();
            ucTV = (TextView)findViewById(R.id.ucTV);
            ucTV.setText(imei);
            ucTV2 = (TextView)findViewById(R.id.ucTV2);
            ucTV2.setText(message);
        }
    }

    public void ucConfirm(View v){
        Intent i = new Intent(this,replace.class);
        i.putExtra("picpath2",message);
        Toast.makeText(uploadconfirm.this, "Path2 sent", Toast.LENGTH_SHORT).show();
        System.gc();
        startActivity(i);
    }
}
