package ncku.appt3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

public class replaceURS extends AppCompatActivity {

    private ProgressBar replaceProgress;
    private TextView replaceText,replaceResult;
    private String imagepath, uploadServerUri = null, imeipost;
    private String uploadresult;
    private final String tag = getClass().getName();
    String responseString;

    HttpURLConnection conn;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    long totalSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.replace);

        replaceProgress = (ProgressBar)findViewById(R.id.replaceProgress);
        replaceText = (TextView)findViewById(R.id.replaceText);
        replaceResult = (TextView)findViewById(R.id.replaceResult);

        Bundle Data = getIntent().getExtras();
        if(Data == null){
            Toast.makeText(replaceURS.this, "Sorry , take picture again", Toast.LENGTH_SHORT).show();
        }
        imagepath = Data.getString("picpath2");
        replaceResult.setText(imagepath);
        imei();
        uploadServerUri = "http://140.116.226.182/mems_main/replace_urs.php?imei="+imeipost;

        new replaceURS.UploadFileToServer().execute();

    }

    private void imei(){
        TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);

        if(ContextCompat.checkSelfPermission(replaceURS.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(replaceURS.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(replaceURS.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }else{
            imeipost = telephonyManager.getImei();
        }
    }

    class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            replaceProgress.setProgress(0);
            super.onPreExecute();
        }

        @Override//sec
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            replaceProgress.setVisibility(View.VISIBLE);

            // updating progress bar value
            replaceProgress.setProgress(progress[0]);

            // updating percentage value
            replaceText.setText(String.valueOf(progress[0]) + "%");
        }

        protected String doInBackground(Void... params)
        {
            return uploadFile();
        }

        @Override
        protected void onPostExecute(String result){
            Log.d(tag, "Response from server: " + result);
            nextpage();
            super.onPostExecute(result);
        }

    }

    private String uploadFile() {
        responseString = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(uploadServerUri);

        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {
                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }

                        private void publishProgress(int i) {
                            replaceProgress.setProgress(i);
                        }
                    });


            File sourceFile = new File(imagepath,"profile.jpg");

            // Adding file data to http body
            entity.addPart("image", new FileBody(sourceFile));

            totalSize = entity.getContentLength();
            //replaceText.setText(String.valueOf(totalSize));
            replaceResult.setText(imagepath);

            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;
            }

        }


        catch (ClientProtocolException e) {
            responseString = e.toString();
        } catch (IOException e) {
            responseString = e.toString();
        }
        catch (Exception ex) {
            System.out.println("something wrong");
        }
        return responseString;
    }

    private void nextpage(){
        Intent i = new Intent(this,urs_result.class);
        System.gc();

        startActivity(i);
    }
}
