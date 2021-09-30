package edu.temple.convoy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class fcm extends FirebaseMessagingService {
    public fcm() {

    }
    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        Log.d("FCMInvolke","involked");
        Intent intent = new Intent("MyData");
        intent.putExtra("username", remoteMessage.getData().get("username"));
        intent.putExtra("latitude", remoteMessage.getData().get("latitude"));
        intent.putExtra("longitude", remoteMessage.getData().get("longitude"));
        broadcaster.sendBroadcast(intent);
    }
    }

