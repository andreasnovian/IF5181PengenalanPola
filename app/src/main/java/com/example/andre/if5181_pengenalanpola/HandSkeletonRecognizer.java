package com.example.andre.if5181_pengenalanpola;

public class HandSkeletonRecognizer {

    private static final String[] arialUppercase = {
            "2,false,false,true,false,false,false,true,false,false", //A
            "0,true,true,true,true,false,false,true,false,true", //B
            "2,false,false,false,false,false,false,false,false,false", //C
            "0,false,false,true,true,false,false,false,true,false", //D
            "3,true,true,true,true,false,false,false,false,false", //E
            "3,true,true,false,true,false,false,false,false,false", //F
            "2,false,false,false,false,false,false,false,false,false", //G
            "4,false,true,false,true,false,true,false,false,false", //H
            "2,false,false,false,false,false,true,false,false,false", //I
            "2,false,false,false,false,false,true,false,false,false", //J
            "4,false,false,false,true,false,false,false,false,false", //K
            "2,false,false,true,true,false,false,false,false,false", //L
            "2,false,false,false,true,false,true,false,false,false", //M
            "4,false,false,false,true,false,true,false,false,false", //N
            "0,false,false,false,false,false,false,false,true,false", //O
            "1,true,true,false,true,false,false,true,false,false", //P
            "2,false,false,false,false,false,false,false,true,false", //Q
            "2,true,true,false,true,false,false,true,false,false", //R
            "2,false,false,false,false,false,false,false,false,false", //S
            "3,true,false,false,false,true,false,false,false,false", //T
            "2,false,false,false,true,false,true,false,false,false", //U
            "2,false,false,false,false,false,false,false,false,false", //V
            "2,false,false,false,false,false,false,false,false,false", //W
            "2,false,false,false,false,false,false,false,false,true", //X
            "3,false,false,false,false,false,false,false,false,false", //Y
            "2,true,false,true,false,false,false,false,false,false" //Z
    };

    private static final String[] arialDowncase = {
            "2,false,false,false,false,false,true,false,false,true", //a
            "2,false,false,false,true,false,false,false,false,true", //b
            "2,false,false,false,false,false,false,false,false,false", //c
            "2,false,false,false,false,false,true,false,false,true", //d
            "1,false,true,false,false,false,false,true,false,false", //e
            "4,true,false,false,false,true,false,false,false,false", //f
            "2,false,false,false,false,false,true,true,false,false", //g
            "3,false,false,false,true,false,true,false,false,false", //h
            "2,false,false,false,false,false,true,false,false,false", //i
            "2,false,false,false,false,false,true,false,false,false", //j
            "4,false,false,false,true,false,false,false,false,false", //k
            "2,false,false,false,false,false,true,false,false,false", //l
            "4,false,false,false,true,true,true,false,false,false", //m
            "3,false,false,false,true,false,true,false,false,false", //n
            "0,false,false,false,false,false,false,false,true,false", //o
            "2,false,false,false,true,false,false,true,false,false", //p
            "2,false,false,false,false,false,true,true,false,false", //q
            "3,false,false,false,true,false,false,false,false,false", //r
            "2,false,false,false,false,false,false,false,false,false", //s
            "4,true,false,false,false,true,false,false,false,false", //t
            "3,false,false,true,true,false,true,false,false,false", //u
            "2,false,false,false,false,false,false,false,false,false", //v
            "3,false,false,false,false,false,false,false,false,false", //w
            "4,false,false,false,false,false,false,false,false,false", //x
            "3,false,false,false,false,false,false,false,false,false", //y
            "3,true,false,true,false,false,false,false,false,false" //z
    };

    private static final String[] arialNumber = {
            "0,false,false,false,false,false,false,false,true,false", //0
            "3,false,false,false,false,false,true,false,false,false", //1
            "2,false,false,true,false,false,false,false,false,false", //2
            "3,false,false,false,false,false,false,false,false,false", //3
            "3,false,false,true,false,false,true,false,true,false", //4
            "2,true,false,false,false,false,false,false,false,false", //5
            "1,false,false,false,false,false,false,false,false,true", //6
            "2,true,false,false,false,false,false,false,false,false", //7
            "0,false,false,false,false,false,false,true,false,true", //8
            "1,false,false,false,false,false,false,true,false,false" //9
    };

    public static String recognize(String feature){
        String result = "";
        char temp;

        for (int i = 0; i<arialUppercase.length;i++){
            if (feature.equals(arialUppercase[i])){
                temp = (char)(i+65);
                result += ""+temp+" ";
            }
        }

        for (int i = 0; i<arialDowncase.length;i++){
            if (feature.equals(arialDowncase[i])){
                temp = (char)(i+97);
                result += ""+temp+" ";
            }
        }

        for (int i = 0; i<arialNumber.length;i++){
            if (feature.equals(arialNumber[i])){
                temp = (char)(i+48);
                result += ""+temp+" ";
            }
        }

        return result.trim();
    }
}
