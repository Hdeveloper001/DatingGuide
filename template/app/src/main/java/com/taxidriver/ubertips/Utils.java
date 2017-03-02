package com.taxidriver.ubertips;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private Context context;

    public Utils(Context c){
        this.context = c;
    }

    public Bitmap getAssetsImage( String imgName) {

        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(imgName);
        } catch (IOException e) {
            Log.w("EL", e);
        }
        Bitmap image = BitmapFactory.decodeStream(is);

        return image;
    }


    public List<Integer> calculateShades(int baseColor, int numberShades)
    {
        //decompose color into RGB
        int color = context.getResources().getColor(baseColor);

//        int color = (int)Long.parseLong(baseColor, 16);
        int redMax = (color >> 0) & 0xFF;
        int greenMax = (color >> 0) & 0xFF;
        int blueMax = (color >> 0) & 0xFF;  //default settings rgb = 16 11 8



        // int redMax  = baseColor;
//        int greenMax  = baseColor.GREEN;
//        int blueMax  = baseColor.BLUE;


        //Max color component in RGB
        final int MAX_COMPONENT = 200;

        //bin sizes for each color component
        int redDelta = (MAX_COMPONENT - redMax) / numberShades;
        int greenDelta = (MAX_COMPONENT - greenMax) / numberShades;
        int blueDelta = (MAX_COMPONENT - blueMax) / numberShades;

        List<Integer> colors = new ArrayList<Integer>();

        int redCurrent = redMax;
        int greenCurrent = greenMax;
        int blueCurrent = blueMax;

        //now step through each shade, and decrease darkness by adding color to it
        for(int i = 0; i < numberShades; i++)
        {

            //step up by the bin size, but stop at the max color component (255)
            redCurrent = (redCurrent+redDelta) < MAX_COMPONENT ? (redCurrent + redDelta ) : MAX_COMPONENT;
            greenCurrent = (greenCurrent+greenDelta) < MAX_COMPONENT ? (greenCurrent + greenDelta ) : MAX_COMPONENT;
            blueCurrent = (blueCurrent+blueDelta) < MAX_COMPONENT ? (blueCurrent + blueDelta ) : MAX_COMPONENT;


            int nextShade =  Color.rgb(redCurrent, greenCurrent, blueCurrent);


            colors.add(nextShade);
        }

        return colors;
    }


    public ArrayList<String> listAssetFiles(String path) {

        ArrayList<String> arrFile = new ArrayList<String>();
        String[] f = null;

        try {
            f = context.getAssets().list(path);
        } catch (IOException e) {
             e.printStackTrace();
        }
        for(String f1:f){
            arrFile.add(f1.replace(".txt",""));

        }
        return arrFile;
    }

    public ArrayList<String> txtToArray(String filename) {

        ArrayList<String> arrList = new ArrayList<String>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(filename), "UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine = reader.readLine();
            while (mLine != null) {
                //process line

                mLine = mLine.replaceAll("[^\\w\\s\\-_\\.\\@\\#]", "").trim();

                arrList.add(mLine );
                mLine = reader.readLine();
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return arrList;
    }

    public String loadAssetTxtAsString( String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ( (str = in.readLine()) != null ) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("loadAssetTextAsString", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("loadAssetTextAsString", "Error closing asset " + name);
                }
            }
        }

        return null;
    }


    public boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


//    private Bitmap getBitmapFromAsset(String strName) throws IOException
//    {
//        AssetManager assetManager = getAssets();
//        InputStream istr = assetManager.open(strName);
//        Bitmap bitmap = BitmapFactory.decodeStream(istr);
//        return bitmap;
//    }


}
