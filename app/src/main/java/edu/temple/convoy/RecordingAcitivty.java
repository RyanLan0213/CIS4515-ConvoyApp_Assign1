package edu.temple.convoy;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RecordingAcitivty extends MainActivity implements GoogleMapActivity.datareturn{
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    Button record,stop,play,send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_acitivty);

        if(isMicrophonePresent()){
            getMicrophonePermission();
        }
        record = findViewById(R.id.Recordbutton);
        stop = findViewById(R.id.StopButton);
        play = findViewById(R.id.PlayButton);
        send = findViewById(R.id.SendButton);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordPressed();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopPressed();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayPressed();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                File audioFile = new File(getrecordingFilePath());
                Log.d("My audioPath is ", getrecordingFilePath());
                byte[] audioByteArray = getBytes(audioFile);
                Log.d("Audio Byte Array is:",audioByteArray.toString());
                uploadBitmap(audioFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });


    }
    private String getrecordingFilePath(){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file =new File(musicDirectory,"MyAudioRecording"+ ".3pg");
        return file.getPath();

    }
    public void recordPressed(){
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getrecordingFilePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();

            Toast.makeText(this,"Recording is Started",Toast.LENGTH_LONG).show();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
    public static byte[] getBytes(File f) throws FileNotFoundException, IOException {
        byte[] buffer = new byte[2048];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(f);
        int read;
        while((read = fis.read(buffer))!=-1){
            os.write(buffer,0,read);
        }
        fis.close();
        os.close();
        return os.toByteArray();



    }
    public void StopPressed(){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        Toast.makeText(this,"Recording is Stopped",Toast.LENGTH_LONG).show();


    }

    public void PlayPressed(){
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getrecordingFilePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.d("Playing recording","Playing");
        }
        catch(Exception e){
            e.printStackTrace();
        }


    }
    private boolean isMicrophonePresent() {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE))
            return true;

        else
            return false;

    }

    private void getMicrophonePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},100);
        }
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    private void uploadBitmap(Bitmap bitmap, File file) {

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, "https://kamorris.com/lab/convoy/convoy.php",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Log.d("The URL is: ",obj.getString("message_url"));
                            Toast.makeText(getApplicationContext(), obj.getString("message_url"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError",""+error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "MESSAGE");
                params.put("username",getusername());
                params.put("session_key",getsessionkey());
                params.put("convoy_id",getconvoyid());


                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("message_file",);

                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

}
