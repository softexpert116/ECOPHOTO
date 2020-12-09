package com.clevery.android.ecophoto;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.clevery.android.ecophoto.Models.StudentModel;
import com.clevery.android.ecophoto.Service.PingService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    public static App app;
    public static SharedPreferences prefs;
    private static Context mContext;
    private static String WAITING = "WAITING";
    public static String MY_ID = "MY_ID";
    public static String DATE_FORMAT = "MM/dd/yyyy";
    public static boolean secret_checked = false;
    private static final int MAX_SMS_MESSAGE_LENGTH = 160;
    public static String serverUrl = "http://10.10.10.134/ecophoto_admin/index.php/mobile";
//    public static String serverUrl = "https://ecophoto.photos/index.php/mobile";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        app = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }
    public static void startMyService(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return;
            }
        }
        Intent i = new Intent(mContext, PingService.class);
        mContext.startService(i);
//        mContext.startForegroundService(i);
    }
    public static void stopMyService(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Intent i = new Intent(mContext, PingService.class);
                mContext.stopService(i);
            }
        }
    }
    public static void sendSMS(String phoneNo, String msg) {
        SmsManager manager = SmsManager.getDefault();
        PendingIntent piSend = PendingIntent.getBroadcast(mContext, 0, new Intent("SMS_SENT"), 0);
        PendingIntent piDelivered = PendingIntent.getBroadcast(mContext, 0, new Intent("SMS_DELIVERED"), 0);
        if(msg.length() > MAX_SMS_MESSAGE_LENGTH)
        {
            ArrayList<String> messagelist = manager.divideMessage(msg);

            manager.sendMultipartTextMessage(phoneNo, null, messagelist, null, null);
        }
        else
        {
            manager.sendTextMessage(phoneNo, null, msg, piSend, piDelivered);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    public static void setPreferences(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static void setPreference_waiting_array(ArrayList list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        setPreference(WAITING, json);
    }
    public static ArrayList<StudentModel> readPreference_waiting_array() {
        String json = readPreference(WAITING, "");
        Type type = new TypeToken<List<StudentModel>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<StudentModel> list= gson.fromJson(json, type);
        return list;
    }
    public static void setPreference(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putString(key, value)
                .commit();
    }
    public static String readPreference(String key, String defaultValue) {
        String value = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(key, defaultValue);
        return value;
    }
    public static void removePreference(String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void social_share(Context context, String url) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
    }
    public void generateHashkey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.ujs.acer.Oyoo",  PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                String hashKey = Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                Log.e("hashkey -------------", hashKey); /// CkLR2IIEs9xCrDGJbKOQ/Jr3exE=
// release key hash: w6gx2BgXV0ybPMNC4PfbKnfpu50=
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {
        }
    }
    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int count = listAdapter.getCount();
        int totalHeight = 0;
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        params.height *= 1.5;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    public static void DialNumber(String number, Context context)
    {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
//            intent.setData(Uri.parse("tel:" + number));
            Uri uri = ussdToCallableUri(number);
            intent.setData(uri);
//            context.startActivity(intent);
        } catch (SecurityException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT);
        }
    }

    private static Uri ussdToCallableUri(String ussd) {

        String uriString = "";

        if(!ussd.startsWith("tel:"))
            uriString += "tel:";

        for(char c : ussd.toCharArray()) {

            if(c == '#')
                uriString += Uri.encode("#");
            else
                uriString += c;
        }

        return Uri.parse(uriString);
    }
    public static int getThemeAccentColor (final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme ().resolveAttribute (R.attr.colorAccent, value, true);
        return value.data;
    }
    public static void openUrl (String url, Context context) {
        if (!URLUtil.isValidUrl(url)) {
            Toast.makeText(context, "Invalid url", Toast.LENGTH_SHORT);
            return;
        }
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static String getTimestampString()
    {
        long tsLong = System.currentTimeMillis();
        String ts =  Long.toString(tsLong);
        return ts;
    }
    public static int getPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = mContext.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr = mContext.obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.textColorPrimary});
        int primaryColor = arr.getColor(0, -1);
        return primaryColor;
    }
}