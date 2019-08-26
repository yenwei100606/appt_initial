package ncku.appt3;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class BitmapUtil {
    public static byte[] bitmapToByte(Bitmap bitmap){
        if (bitmap == null) {
            throw new IllegalArgumentException("Bitmap is empty , please check again.");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        return baos.toByteArray();
    }
}
