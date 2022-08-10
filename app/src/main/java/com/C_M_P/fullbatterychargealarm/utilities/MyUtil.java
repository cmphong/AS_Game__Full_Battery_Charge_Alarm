package com.C_M_P.fullbatterychargealarm.utilities;

import android.util.Log;

public class MyUtil {


    // calculate ratio ================================================
    public static void simplifyComplexFractions(int pxWidth, int pxHeight) {
        int primeNumber = biggestPrimeNumber(pxWidth, pxHeight);
        pxWidth = pxWidth/primeNumber;
        pxHeight = pxHeight/primeNumber;

        if(pxHeight == 1){
            LogdlnStatic("result: " + pxWidth, 14);
        }else{
            if(pxHeight < 0){
                pxHeight *= -1;
                pxWidth *= -1;
            }
            LogdlnStatic("result: " +pxWidth +":" +pxHeight, 20);
        }

    }

    // Tìm USCLN
    public static int biggestPrimeNumber(int a, int b){
        if(a%b == 0) return b;
        else return biggestPrimeNumber(b, a%b);
    }

    // Kiểm tra số Nguyên tố
    // Thuật toán: cho i chạy từ 2 đến n-1
    // Nếu n chia hết cho i thì n không phải là số Nguyên tố
    public static boolean isPrimmeNumber(int n){

        for(int i = 2 ; i <= n - 1 ; i++){
            if(n % i == 0) return false;
        }
        return true;
    }


    // ====================================================================
    public void Logd(String str){
        Log.d("Log.d", "=== MyUtil.java ==============================\n" + str);
    }
    public void Logdln(String str, int n){
        Log.d("Log.d", "=== MyUtil.java - line: " + n + " ==============================\n" + str);
    }
    public static void LogdStatic(String str){
        Log.d("Log.d", "=== MyUtil.java ==============================\n" + str);
    }
    public static void LogdlnStatic(String str, int n){
        Log.d("Log.d", "=== MyUtil.java - line: " + n + " ==============================\n" + str);
    }
}
