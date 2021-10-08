package edu.temple.convoy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class fcm extends FirebaseMessagingService implements GoogleMapActivity.datareturn {
    public fcm() {

    }

    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("I have been created","THE FCM");
        broadcaster = LocalBroadcastManager.getInstance(this);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Log.d("FCMInvolke", "involked");
     /*   Intent intent = new Intent("MyData");
        intent.putExtra("username", remoteMessage.getData().get("username"));
        intent.putExtra("latitude", remoteMessage.getData().get("latitude"));
        intent.putExtra("longitude", remoteMessage.getData().get("longitude"));
        broadcaster.sendBroadcast(intent);

      */
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://kamorris.com/lab/convoy/convoy.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   Toast.makeText(GoogleMapActivity.this,"clicked",Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObject = new JSONObject(response);
                    if (jObject.getString("status").equals("SUCCESS")) {
                        Log.d("NEWTOKEN","SUCESS");

                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error:", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> convoydata = new HashMap<>();
                convoydata.put("action", "UPDATE");
                convoydata.put("username", getusername());
                convoydata.put("session_key", getsessionkey());
                convoydata.put("fcm_token", s);


                return convoydata;
            }
        };
        returnqueue().add(stringRequest);


    }
}

