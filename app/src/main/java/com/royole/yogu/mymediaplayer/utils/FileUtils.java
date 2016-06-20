package com.royole.yogu.mymediaplayer.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * read file utils
 * Author  yogu
 * Since  2016/6/20
 */


public class FileUtils {
    /**
     * read json file from dir asserts
     * @param context
     * @return
     */
    public static String readAssertJson(Context context,String jsonFileName){
        String line="";
        StringBuilder result= new StringBuilder();
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(jsonFileName) );
            BufferedReader bufReader = new BufferedReader(inputReader);
            while((line = bufReader.readLine()) != null){
                result.append(line);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d("json",result.toString());
        return result.toString();
    }
}
