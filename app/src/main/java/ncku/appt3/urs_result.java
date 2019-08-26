package ncku.appt3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class urs_result extends AppCompatActivity{

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private final String tag = getClass().getName();

    MyDBHandler_re dbHandler;

    TextView urs_resultTV,urs_resultTime;

    public String sqlt,result;
    private Calendar mCalendar;
    DateFormat sdf;
    private String str;
    long i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.urs_result);

        urs_resultTV = (TextView)findViewById(R.id.urs_resultTV);
        urs_resultTime = (TextView)findViewById(R.id.urs_resultTime);

        dbHandler = new MyDBHandler_re(this, null, null, 1);

        new urs_result.AsyncRetrieve().execute();
    }

    private class AsyncRetrieve extends AsyncTask<String,String,String> {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(tag,"OnPreExecute");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL("http://140.116.226.182/mems_client/355758080228806/urs.php");
                //SAMSUNG 355758080228806
                //SONY 358096071301846

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d(tag,"doInBackground 1 error");
                return e.toString();
            }

            try {

                // Setup HttpURLConnection class to send and receive data from php
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoOutput to true as we recieve data from json file
                conn.setDoOutput(true);

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                Log.d(tag,"doInBackground 2 error");
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line ="";

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    sqlt=result.toString();

                    return sqlt;

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(tag,"doInBackground 3 error");
                return e.toString();
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result){
            urs_resultTV.setText(sqlt);
            mCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            str = sdf.format(mCalendar.getTime());
            urs_resultTime.setText(str);
            object_re object = new object_re(sqlt,str);
            i=0;
            i=dbHandler.addProduct(object);
        }

    }

    public void backtoHome(View v){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }
}
