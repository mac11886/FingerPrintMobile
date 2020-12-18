//package com.example.fingerprinttest;
//
//import android.app.ProgressDialog;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.google.gson.Gson;
//import com.kisracorp.coach.models.CoachUser;
//import com.kisracorp.coach.models.OpenViduToken;
//import com.kisracorp.coach.models.RegisterDevice;
//import com.kisracorp.coach.models.UserToken;
//import com.kisracorp.coach.services.MapToJsonConverter;
//import com.example.fingerprinttest.services.SimpleHTTPRequest;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class API {
//
//    private static final String TAG = "API";
//
//    public static UserToken loginWithDevice(String username, String password, String callingToken){
//
//        Log.i(TAG, "loginWithDevice: " + username);
//
//        String url = "https://api-auth.namjai.co/api/streaming/login";
//        Map<String, String> data = new HashMap<String, String>();
//        data.put("username", username);
//        data.put("password", password);
//        data.put("notificationToken", callingToken);
//        data.put("device", "android");
//
//        String json = MapToJsonConverter.convert(data);
//
//        UserToken userToken = null;
//        try {
//            String jsonResponse = SimpleHTTPRequest.createPostJsonRequest(url, json);
//            userToken = UserToken.convertJSON(jsonResponse);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return userToken;
//    }
//
//
//    public static RegisterDevice registerDevice(RegisterDevice registerDevice){
//
//        String url = "https://streaming-server.namjai.co/api/device";
//
//        RegisterDevice device = null;
//        try {
//            String json = SimpleHTTPRequest.createPostJsonRequest(url,registerDevice.toJson());
//            device = RegisterDevice.convertJSON(json);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return device;
//    }
//
//
//    public static CoachUser[] getAllUser(){
//
//        String url = "https://streaming-server.namjai.co/api/user/list/active";
//
//        CoachUser[] coachArray =  new   CoachUser[0];
//
//        try {
//            String json = SimpleHTTPRequest.createGetRequest(url);
//            Gson gson = new Gson();
//            coachArray = gson.fromJson(json, CoachUser[].class);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return coachArray;
//    }
//
//    public static CoachUser getUser(String userID){
//
//
//        String url = "https://streaming-server.namjai.co/api/user/profile/"+userID;
//
//        CoachUser user =  new   CoachUser();
//
//        try {
//            String json = SimpleHTTPRequest.createGetRequest(url);
//            Gson gson = new Gson();
//            user = gson.fromJson(json, CoachUser.class);
//            Log.i(TAG, "getUser: done" + json  );
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.i(TAG, "getUser: error" );
//        }
//
//        return user;
//    }
//
//
//    public static OpenViduToken getToken(String sessionID, String username){
//
//        String url = "https://streaming-server.namjai.co/api/test/getToken?sessionID="+sessionID+"&username="+username;
//        OpenViduToken token =  new   OpenViduToken();
//
//        try {
//            String json = SimpleHTTPRequest.createGetRequest(url);
//            Gson gson = new Gson();
//            token = gson.fromJson(json, OpenViduToken.class);
//            Log.i(TAG, "getToken: " + json);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.i(TAG, "getToken: error" );
//        }
//
//        return token;
//    }
//
//
//
//    public static boolean requestCalling(String fromUserID, String toUserID){
//
//        String url = "https://streaming-server.namjai.co/api/test/calling?from="+fromUserID+"&to="+toUserID;
//        Log.i(TAG, "requestCalling: " + url);
//        try {
//            String jsonResponse = SimpleHTTPRequest.createGetRequest(url);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return  false;
//        }
//        return true;
//    }
//
//    public static boolean acceptCall(String toUserID){
//
//        String url = "https://streaming-server.namjai.co/api/test/accept-call?userId="+toUserID;
//        Log.i(TAG, "acceptCall: " + url);
////        try {
////            String jsonResponse = SimpleHTTPRequest.createGetRequest(url);
////        } catch (IOException e) {
////            e.printStackTrace();
////            return  false;
////        }
//        return true;
//    }
//
//    public static boolean rejectCall(String toUserID){
//
//        String url = "https://streaming-server.namjai.co/api/test/reject-call?userId="+toUserID;
//        Log.i(TAG, "rejectCall: " + url);
//        try {
//            String jsonResponse = SimpleHTTPRequest.createGetRequest(url);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return  false;
//        }
//        return true;
//    }
//
//    public static boolean endCall(String fromUserID){
//
//        String url = "https://streaming-server.namjai.co/api/test/end-call?userId="+fromUserID;
//        Log.i(TAG, "endCall: " + url);
//        try {
//            String jsonResponse = SimpleHTTPRequest.createGetRequest(url);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return  false;
//        }
//        return true;
//    }
//
//
//}
