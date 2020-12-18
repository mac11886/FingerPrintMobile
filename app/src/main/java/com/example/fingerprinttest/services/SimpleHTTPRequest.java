//package com.example.fingerprinttest.services;
//
//import android.util.Log;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.Map;
//
//public class SimpleHTTPRequest {
//
//    private static final String TAG = "SimpleHTTPRequest";
//
//    public static String createGetRequest(String requestURL) throws IOException {
//
//        URL url;
//        HttpURLConnection urlConnection = null;
//        try {
//            url = new URL(requestURL);
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            //HTTP header
////            urlConnection.setRequestProperty("Authorization", "Bearer "+ token);
//
//            int responseCode = urlConnection.getResponseCode();
//            String responseMessage = urlConnection.getResponseMessage();
//
//            if(responseCode == HttpURLConnection.HTTP_OK){
//                String responseString = readStream(urlConnection.getInputStream());
////                Log.v(TAG  + "-Response", responseString);
//                return responseString;
//            }else{
//                Log.v(TAG, "Response code:"+ responseCode);
//                Log.v(TAG, "Response message:"+ responseMessage);
//            }
//
//        } catch (Exception e) {
//            Log.e(TAG, e.toString());
//        } finally {
//            if(urlConnection != null)
//                urlConnection.disconnect();
//        }
//        return null;
//    }
//
//    private static String readStream(InputStream in) {
//        BufferedReader reader = null;
//        StringBuffer response = new StringBuffer();
//        try {
//            reader = new BufferedReader(new InputStreamReader(in));
//            String line = "";
//            while ((line = reader.readLine()) != null) {
//                response.append(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return response.toString();
//    }
//
//    public static String createPostJsonRequest(String requestURL, String json) throws IOException {
//
////        Log.i(TAG,"json: " + json );
//
//        URL url = new URL(requestURL);
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("POST");
//        con.setRequestProperty("Content-Type", "application/json");
//
//        con.setDoOutput(true);
//        DataOutputStream out = new DataOutputStream(con.getOutputStream());
//        out.writeBytes( json );
//        out.flush();
//        out.close();
//
//        int status = con.getResponseCode();
//        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuilder content = new StringBuilder();
//        while ((inputLine = in.readLine()) != null) {
//            content.append(inputLine);
//        }
//        in.close();
//
////        Log.i(TAG,"Status" + status );
//
//        String result = content.toString();
//
//        return result;
//
//    }
//
//}
