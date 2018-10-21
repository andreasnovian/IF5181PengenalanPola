package com.example.andre.if5181_pengenalanpola;

import android.widget.TextView;

public class SkeletonRecognizer {
    public static String recognize(String feature){
        String result = "";

        if (feature.equals("0,false,false,false,false,false,false,false,true,false")){
            result = "0";
        }
        else if (feature.equals("3,false,false,false,false,false,true,false,false,false")){
            result = "1";
        }
        else if (feature.equals("3,false,false,true,false,false,false,false,false,false")){
            result = "2";
        }
        else if (feature.equals("3,false,false,false,false,false,false,false,false,false")){
            result = "3";
        }
        else if (feature.equals("3,false,false,true,false,false,true,false,true,false")){
            result = "4";
        }
        else if (feature.equals("3,true,true,false,false,false,false,false,false,false")){
            result = "5";
        }
        else if (feature.equals("1,false,false,false,false,false,false,false,false,true")){
            result = "6";
        }
        else if (feature.equals("3,true,false,false,false,false,false,false,false,false")){
            result = "7";
        }
        else if (feature.equals("0,false,false,false,false,false,false,true,false,true")){
            result = "8";
        }
        else if (feature.equals("1,false,false,false,false,false,false,true,false,false")){
            result = "9";
        }

        return result;
    }
}
