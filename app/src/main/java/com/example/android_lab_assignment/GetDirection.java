package com.example.android_lab_assignment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.TextView;

import com.example.android_lab_assignment.MainActivity;
import com.example.android_lab_assignment.Nearby.DataParser;
import com.example.android_lab_assignment.Nearby.FetchURL;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

public class GetDirection extends AsyncTask <Object, String, String>
{
    String directionData;
    GoogleMap mMap;
    String url;

    String distance, duration;

    TextView dist,dur;

    LatLng latLng;
    LatLng latLng_user;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng  =(LatLng) objects[2];
        latLng_user = (LatLng)objects[3];
        if(objects[3] != null && objects[4]!=null) {
            dist = (TextView) objects[4];
            dur = (TextView) objects[5];
        }

        FetchURL fetchURL = new FetchURL();
        try{
            directionData = FetchURL.readURL(url);
        }catch(IOException e)
        {
            e.printStackTrace();
        }
        return directionData;
    }


    

    @Override
    protected void onPostExecute(String s) {
        HashMap<String,String> distanceData = null;
        DataParser distanceParser = new DataParser();
        distanceData = distanceParser.parseDistance(s);

        distance = distanceData.get("distance");
        duration = distanceData.get("duration");

        mMap.clear();
        MapFragment mainActivity = new MapFragment();
        CustomDirection customDirection = new CustomDirection();

        //create a new marker with distance and duration as title
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Duration : "+ duration)
                .snippet("Distance : "+ distance);
        mMap.addMarker(options);

        mMap.addMarker(new MarkerOptions().position(latLng_user)
                .title("Your Location")
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

//        if(MapFragment.directionRequested || CustomDirection.directionRequested)
//        {
            String[] directionList;
            DataParser directionParser = new DataParser();
            directionList = directionParser.parseDirection(s);
            displayDirection(directionList);
        if(dist!=null && dur!=null) {
            dist.setText(distance);
            dur.setText(duration);
        }
        //}

    }

    private void displayDirection(String[] directionList)
    {
        int count = directionList.length;
        for(int i = 0;i<count;i++)
        {
            PolylineOptions options = new PolylineOptions()
                    .color(Color.RED)
                    .width(10)
                    .addAll(PolyUtil.decode(directionList[i]));
            mMap.addPolyline(options);
        }
    }
}
