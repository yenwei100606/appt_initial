package ncku.appt3;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class URS extends AppCompatActivity implements TextureView.SurfaceTextureListener{

    String imeipost;

    private final String tag = getClass().getName();
    private static final String TAG = "URS";
    private HandlerThread mThreadHandler;
    private TextureView mPreviewView;
    private Handler mHandler = new Handler();
    private Button ursTake,ursBack;
    private ImageView ursIV;
    private Size mPreviewSize;
    private String mCameraId;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CaptureRequest mCaptureRequest;
    private CameraCaptureSession mPreviewSession;
    private ImageReader mImageReader;
    ContextWrapper cw;
    File directory;
    File mypath;
    String abpath;

    int mComValue = 5;

    private static final SparseIntArray ORIENTATION = new SparseIntArray();

    static {
        ORIENTATION.append(Surface.ROTATION_0, 90);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 270);
        ORIENTATION.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new URS.MakeNetworkCall().execute("http://140.116.226.182/mems_main/file.php?post=1", "Post");
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.urs);

        getSupportActionBar().hide(); //隱藏標題

        mThreadHandler = new HandlerThread("CAMERA2");
        mThreadHandler.start();
        //ursIV = (ImageView)findViewById(R.id.ursIV);
        mPreviewView = (TextureView) findViewById(R.id.ursTexture);
        mPreviewView.setSurfaceTextureListener(this);
        ursTake = (Button)findViewById(R.id.ursTake);
        ursBack = (Button)findViewById(R.id.ursBack);

        ursTake.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    try{
                        //螢幕方向
                        int rotation = getWindowManager().getDefaultDisplay().getRotation();
                        //設定拍照方向
                        mPreviewBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATION.get(rotation));
                        //聚焦
                        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        //閃光燈
                        //mPreviewBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);

                        //白平衡
                        mPreviewBuilder.set(CaptureRequest.CONTROL_AWB_MODE,CaptureRequest.CONTROL_AWB_MODE_AUTO);

                        //場景
                        //mPreviewBuilder.set(CaptureRequest.CONTROL_SCENE_MODE,CaptureRequest.CONTROL_SCENE_MODE_STEADYPHOTO);

                        //ISO
                        mPreviewBuilder.set(CaptureRequest.SENSOR_SENSITIVITY,50);

                        mPreviewBuilder.addTarget(mImageReader.getSurface());

                        //停止預覽
                        mPreviewSession.stopRepeating();

                        mPreviewSession.capture(mPreviewBuilder.build(), mSessionCaptureCallback, null);
                        }catch (CameraAccessException e){
                            e.printStackTrace();
                    }
                }
             }
        );
    }

    //////head
    private class MakeNetworkCall extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            Log.d(tag,"MakeNetworkCall  preExecute error");
        }

        @Override
        protected String doInBackground(String... arg){

            InputStream is = null;
            String URL = arg[0];
            String res = "";
            is = ByPostMethod(URL);

            if(is != null){
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();

                String line = null;
                try{
                    while ((line = br.readLine()) != null){
                        sb.append(line);
                    }
                }catch (IOException e){
                    Log.d(tag,"Fail converting stream to String");
                }catch (Exception e){
                    Log.d(tag,"Fail converting stream to String");
                }finally {
                    try{
                        is.close();
                    }catch (IOException e){
                        Log.d(tag,"Fail converting stream to String");
                    }catch (Exception e){
                        Log.d(tag,"Fail converting stream to String");
                    }
                }

                res = sb.toString();

            }else{
                res = "Something went wrong";
            }

            return res;
        }

        @Override
        protected void onPostExecute(String result){

        }
    }

    private void imei(){
        TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);

        if(ContextCompat.checkSelfPermission(URS.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(URS.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(URS.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }else{
            imeipost = telephonyManager.getImei();
        }
    }

    InputStream ByPostMethod(String ServerURL){
        InputStream DataInputStream = null;
        try{
            imei();

            String PostParam = "imei=" + imeipost;

            URL url = new URL(ServerURL);

            HttpURLConnection cc = (HttpURLConnection) url.openConnection();
            // set Timeout for reading InputStream
            cc.setReadTimeout(5000);
            // set Timeout for connection
            cc.setConnectTimeout(5000);
            //set HTTP method to POST
            cc.setRequestMethod("POST");
            //set it to true as we are connecting for input
            cc.setDoInput(true);
            //opens the communication link
            cc.connect();

            //Writing data (bytes) to the data output stream
            DataOutputStream dos = new DataOutputStream(cc.getOutputStream());
            dos.writeBytes(PostParam);

            dos.flush();
            dos.close();

            //Getting HTTP response code
            int response = cc.getResponseCode();

            //if response code is 200 / OK then read Inputstream
            //HttpURLConnection.HTTP_OK is equal to 200
            if(response == HttpURLConnection.HTTP_OK){
                DataInputStream = cc.getInputStream();
            }

        }catch(Exception e){

            Log.d(tag,"ByPostMethod error");
        }

        return DataInputStream;
    }
    //////end

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        setupCamera();
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void setupCamera(){
        //相機的管理者
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            //遍歷所有鏡頭
            for(String id :manager.getCameraIdList()){
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                //打開後鏡頭
                if (characteristics.get(CameraCharacteristics.LENS_FACING)==CameraCharacteristics.LENS_FACING_FRONT)
                    continue;
                //StreamConfigurationMap為管理鏡頭支援的所有輸出格式跟尺寸
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                Size[] size = map.getOutputSizes(ImageFormat.JPEG);

                Size aspectSize = chooseSize(size);

                for(Size sizes : map.getOutputSizes(ImageFormat.JPEG)){
                    Log.i(tag,"imageDimension :" + sizes);
                }

                Log.e(tag,"aspectSize = "+ aspectSize);

                //我們所能使用的最大尺寸
                mPreviewSize = aspectSize;

                Log.e(tag,"previewSize = "+mPreviewSize.getWidth() + "X" + mPreviewSize.getHeight());


                mPreviewView.setLayoutParams(new FrameLayout.LayoutParams(mPreviewView.getWidth(),mPreviewSize.getHeight(), Gravity.CENTER));
                //ursIV.setLayoutParams(new FrameLayout.LayoutParams(mPreviewView.getWidth(),mPreviewSize.getHeight(), Gravity.CENTER));

                mCameraId = id;
                break;
            }
        }catch(CameraAccessException e){
            e.printStackTrace();
        }
    }

    ///for preview size

    private static Size chooseSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 16 / 9 && size.getWidth() <= 1920) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    private static Size chooseOptimalSize(Size[] choices, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<Size>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        double ratio = (double) h / w;
        for (Size option : choices) {
            double optionRatio = (double) option.getHeight() / option.getWidth();
            if (ratio == optionRatio) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new URS.CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[1];
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    ///for preview size

    private void openCamera(){
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        //檢查權限
        try{
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                return;
            }

            manager.openCamera(mCameraId, stateCallback, null);
        }catch(CameraAccessException e){
            e.printStackTrace();
        }
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            //開啟預覽
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    };

    private void startPreview(){
        SurfaceTexture mSurfaceTexture = mPreviewView.getSurfaceTexture();

        mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());

        //mSurfaceTexture.setDefaultBufferSize(1080, 1920);
        //SAMSUNG 1080*1920
        //SONY

        Surface mSurface = new Surface(mSurfaceTexture);

        setupImageReader();

        Surface imageReaderSurface = mImageReader.getSurface();

        try{
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            //設置SURFACE作為預覽數據的顯示介面
            mPreviewBuilder.addTarget(mSurface);

            mCameraDevice.createCaptureSession(Arrays.asList(mSurface,imageReaderSurface),mSessionStateCallback,null);

        }catch(CameraAccessException e){
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try{
                //對焦
                mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                //白平衡
                mPreviewBuilder.set(CaptureRequest.CONTROL_AWB_MODE,CaptureRequest.CONTROL_AWB_MODE_AUTO);

                //場景
                //mPreviewBuilder.set(CaptureRequest.CONTROL_SCENE_MODE,CaptureRequest.CONTROL_SCENE_MODE_STEADYPHOTO);

                //ISO
                mPreviewBuilder.set(CaptureRequest.SENSOR_SENSITIVITY,50);

                //曝光補償
                mPreviewBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION,mComValue);

                //閃光燈
                //mPreviewBuilder.set(CaptureRequest.FLASH_MODE,CaptureRequest.FLASH_MODE_TORCH);

                mCaptureRequest = mPreviewBuilder.build();

                mPreviewSession = session;

                mPreviewSession.setRepeatingRequest(mCaptureRequest, mSessionCaptureCallback, mHandler);
            }catch (CameraAccessException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }
    };

    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            //重啟預覽
            restartPreview();
        }
    };

    private void restartPreview(){
        try{
            mPreviewSession.setRepeatingRequest(mCaptureRequest,null,mHandler);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    private void setupImageReader(){
        mImageReader = ImageReader.newInstance(mPreviewSize.getHeight(), mPreviewSize.getWidth(),
                ImageFormat.JPEG, 2);

        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                mHandler.post(new URS.ImageSaver(reader.acquireNextImage()));
            }
        },mHandler);
    }

    public class ImageSaver implements Runnable {
        private Image mImage;
        private File mFile;

        public ImageSaver(Image image){
            this.mImage = image;
        }

        @Override
        public void run(){
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            FileOutputStream output = null;
            buffer.get(bytes);

            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyyMMdd_HHmmss",
                    Locale.US);

            String fname = "IMG_" +
                    sdf.format(new Date())
                    + ".jpg";
            //mFile = new File(getApplication().getExternalFilesDir(null), fname); //存在Android/XXX/files裡
            //mFile = new File("/storage/emulated/0/DCIM/Camera", fname); 存在相簿
            cw = new ContextWrapper(getApplicationContext());
            directory = cw.getDir("imageDir.jpg", Context.MODE_PRIVATE);
            mFile = new File(directory, "profile.jpg");

            try{
                //Bitmap bitmap = mPreviewView.getBitmap();
                //byte[] bytes1 = BitmapUtil.bitmapToByte(bitmap);
                output = new FileOutputStream(mFile);
                output.write(bytes);

            }catch(IOException e){
                e.printStackTrace();
                Log.d(tag,"File not found");
            }
            finally {
                mImage.close();
                if(null != output){
                    try{
                        output.close();

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
            abpath = directory.getAbsolutePath();
            showImg(abpath);
        }
    }

    private void showImg(String path){
        try{
            File f = new File(path,"profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

        }catch (FileNotFoundException e){
            e.printStackTrace();
            Log.d(tag,"Flie in showImg not found");
        }
        Intent i =new Intent(URS.this,uploadconfirmURS.class);
        i.putExtra("picpath",abpath);
        Toast.makeText(URS.this, "Path sent", Toast.LENGTH_SHORT).show();
        System.gc();
        startActivity(i);
    }

    public void backtoHome(View v){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }
}
