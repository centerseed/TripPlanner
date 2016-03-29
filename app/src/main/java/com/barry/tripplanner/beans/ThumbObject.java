package com.barry.tripplanner.beans;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ThumbObject {
    Bitmap mBmp;
    String mPhotoURL;

    public void decodeToBitmap(String encodeString) {
        encodeString = encodeString.replace("data:image/jpeg;base64,","");

        byte[] imageAsBytes = Base64.decode(encodeString.getBytes(), 0);
        mBmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}
