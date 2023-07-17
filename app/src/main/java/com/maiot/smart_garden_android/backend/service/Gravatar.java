package com.maiot.smart_garden_android.backend.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Gravatar {
    private final String type;
    private final int size;

    public Gravatar(String type, int size) {
        this.type = type;
        this.size = size;
    }

    private String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public Bitmap getAvatar(String name){
        // get avatar from gravatar
        String url = "https://www.gravatar.com/";
        String hash = MD5(name);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .build();
        Call<ResponseBody> call = retrofit.create(SmartGardenService.class).getAvatar(hash, size, type);
        ServerCaller<ResponseBody> caller = new ServerCaller<>(call);
        try {
            caller.call();
        } catch (Exception e) {
            return null;
        }
        Response<ResponseBody> response = caller.getResponse();
        Integer responseCode = caller.getResponseCode();

        if (responseCode != 200) {
            return null;
        }

        InputStream inputStream = response.body().byteStream();
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }
}
