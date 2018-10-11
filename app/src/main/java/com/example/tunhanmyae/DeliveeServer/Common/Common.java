package com.example.tunhanmyae.DeliveeServer.Common;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.tunhanmyae.DeliveeServer.Remote.APIService;
import com.example.tunhanmyae.DeliveeServer.Remote.FCMRetrofitClinet;
import com.example.tunhanmyae.DeliveeServer.Remote.IGoCoordinates;
import com.example.tunhanmyae.DeliveeServer.Remote.RetrofitClient;
import com.example.tunhanmyae.DeliveeServer.model.Request;
import com.example.tunhanmyae.DeliveeServer.model.User;

import retrofit2.Retrofit;

public class Common {
    public static User currentUser;
    public  static final String UPDATE = "Update";
    public  static final String DELETE = "Delete";
    public static final int PICK_IMAGE_REQUEST = 71;
    public static Request currentRequest;
    public  static String baseUrl = "https://maps.googleapis.com/";
    private static final String fcm_URL = "https://fcm.googleapis.com/";


    public  static APIService getFCMService()
    {
        return FCMRetrofitClinet.getClient(fcm_URL).create(APIService.class);

    }


    public static IGoCoordinates getGeoCodeService()
    {
        return RetrofitClient.getClient(baseUrl).create(IGoCoordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap,int newWidth,int newHeight)
    {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);
        float scaleX = newWidth/(float)bitmap.getWidth();
        float scaleY = newHeight/(float)bitmap.getHeight();
        float pivotX=0,pivotY=0;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }

    public static String convertCodeToStatus(String code)
    {
        if (code.equals("0"))
        {
            return "Placed";
        }
        else if(code.equals("1"))
        {
            return "On my way!";
        }
        else
            return  "Shipped";
    }

}
