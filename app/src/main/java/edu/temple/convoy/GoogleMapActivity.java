package edu.temple.convoy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GoogleMapActivity extends MainActivity {
    SupportMapFragment smf;
    FusedLocationProviderClient client;
    Button logoutbutton, createbutton, joinbutton, leavebutton;
    public String CONVOYURL = "https://kamorris.com/lab/convoy/convoy.php";
    String username;
    String sessionkey;
    String convoyid;
    String status;
    TextView convoytextview;
    public String logoutURL = "https://kamorris.com/lab/convoy/account.php";
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        convoytextview = findViewById(R.id.convoytextview);

        Bundle b = new Bundle();
        b = getIntent().getExtras();
        username = b.getString("username");
        sessionkey = b.getString("sessionkey");
        Log.d("thee key is ", sessionkey);
        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);

        logoutbutton = findViewById(R.id.logoutbutton);
        logoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                finish();
            }
        });
        createbutton = findViewById(R.id.createbutton);
        createbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(GoogleMapActivity.this,createbutton.getText(),Toast.LENGTH_SHORT);
                Log.d("123123", createbutton.getText().toString());
                if (createbutton.getText().toString().equals("Create Convoy")) {
                    startconvoy();

                } else {
                    endconvoy();

                }
            }
        });


        getCurrentlocation();


    }

    private void logout() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, logoutURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(GoogleMapActivity.this, response, Toast.LENGTH_SHORT).show();


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
                convoydata.put("action", "LOGOUT");
                convoydata.put("username", username);
                convoydata.put("session_key", sessionkey);
                // logindata.put("firstname",)

                return convoydata;
            }
        };
        queue.add(stringRequest);

    }

    private void endconvoy() {
        //Toast.makeText(GoogleMapActivity.this,"clicked",Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CONVOYURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   Toast.makeText(GoogleMapActivity.this,"clicked",Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObject = new JSONObject(response);
                    // Log.d("Creation Status is ", response.toString());
                    if (jObject.getString("status").equals("SUCCESS")) {
                        opendialog("End");


                        createbutton.setText("Create Convoy");
                        convoytextview.setText("The Convoy has been Ended");


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
                convoydata.put("action", "END");
                convoydata.put("username", username);
                convoydata.put("session_key", sessionkey);
                convoydata.put("convoy_id", convoyid);
                // logindata.put("firstname",)

                return convoydata;
            }
        };
        queue.add(stringRequest);

    }

    private void startconvoy() {

        //Toast.makeText(GoogleMapActivity.this,"clicked",Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CONVOYURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   Toast.makeText(GoogleMapActivity.this,"clicked",Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jObject = new JSONObject(response);
                    Log.d("Creation Status is ", response.toString());
                    if (jObject.getString("status").equals("SUCCESS")) {
                        convoyid = jObject.getString("convoy_id");
                        createbutton.setText("End Convoy");
                        String message = "The convoy id is: " + convoyid;
                        String title = "Convoy Created";
                        opendialog("Start");
                        convoytextview.setText("The Convoy ID is : " + convoyid);


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
                convoydata.put("action", "CREATE");
                convoydata.put("username", username);
                convoydata.put("session_key", sessionkey);
                // logindata.put("firstname",)

                return convoydata;
            }
        };
        queue.add(stringRequest);
    }

    public void opendialog(String status) {
        DialogFra dialog = new DialogFra(convoyid,status);
        dialog.show(getSupportFragmentManager(), "Convoy");

    }

    private void getCurrentlocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Log.d("in","in");
            //Task<Location> task = client.getLastLocation();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(1);
            mLocationRequest.setFastestInterval(1);
            mLocationRequest.setSmallestDisplacement(10);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.d("latlng", latlng.toString());
                            MarkerOptions options = new MarkerOptions().position(latlng).title("I am here");
                            smf.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull GoogleMap googleMap) {
                                    googleMap.clear();
                                    googleMap.addMarker(options);
                                }
                            });
                                                  }
                    }
                }
            };
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);

            LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //Log.d("location",location.toString());
                    if (location != null) {
                        smf.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                                Log.d("latlng", latlng.toString());
                                MarkerOptions options = new MarkerOptions().position(latlng).title("I am here");
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
                                Log.d("I", "I am invoked");
                                googleMap.addMarker(options);
                            }

                        });
                    }
                }
            });

        } else {
            ActivityCompat.requestPermissions(GoogleMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);

        }




           /* Task<Location> task = client.getLastLocation();
            Log.d("Task",task.toString());
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                   // Log.d("invoooo","ooooo");
                    Log.d("location",location.toString());
                    if(location!=null){
                        smf.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
                                Log.d("latlng",latlng.toString());
                                MarkerOptions options = new MarkerOptions().position(latlng).title("I am here");
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,10));
                                Log.d("I","I am invoked");
                                googleMap.addMarker(options);
                            }
                        });
                    }
                }
            });

        }
        else{
            ActivityCompat.requestPermissions(GoogleMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 44);

        }

            */

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getCurrentlocation();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    Looper.getMainLooper());
        }

    }

   /* @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    */


}