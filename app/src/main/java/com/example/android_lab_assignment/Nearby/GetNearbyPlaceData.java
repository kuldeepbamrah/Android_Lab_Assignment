package com.example.android_lab_assignment.Nearby;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.example.android_lab_assignment.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaceData extends AsyncTask<Object, String, String> {
    GoogleMap googleMap;
    String url,place;
    Context context;
    String placeData;
    LatLng user;

    @Override
    protected String doInBackground(Object... objects) {
        googleMap = (GoogleMap) objects[0];
        url = (String) objects[1];
       user = (LatLng) objects[2];
       // context = (Context) objects[3];
        //placeData = (String) objects[2];
        //FetchURL fetchURL = new FetchURL();
        try {
            placeData = FetchURL.readURL( url );
        } catch (IOException e) {
            e.printStackTrace();
        }
return placeData;
    }


    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearByPlaceList = null;
        DataParser parser = new DataParser();
        nearByPlaceList = parser.parse( s );
        showNearByPlaces( nearByPlaceList );
    }


    private void  showNearByPlaces(List<HashMap<String, String>> nearbyList){
        googleMap.clear();
        for(int i = 0; i <nearbyList.size();i++)
        {

           //Log.i("MainActivity", String.valueOf( nearbyList.size()));
            HashMap<String, String> place = nearbyList.get( i );

            String placeName = place.get( "placeName" );
            String vicinity = place.get( "vicinity" );
            double lat = Double.parseDouble( place.get( "lat" ) );
            double lng = Double.parseDouble( place.get( "lng" ) );
            String reference = place.get( "reference" );

            LatLng location = new LatLng( lat, lng );
            MarkerOptions marker = new MarkerOptions().position( location )
                    .title( placeName )

                    .icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_RED ) ) ;
            googleMap.addMarker( marker );

            if(user!=null)
            {
                googleMap.addMarker(new MarkerOptions().position(user)
                .title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }
//            CameraPosition cameraPosition = CameraPosition.builder()
//                    .target( location )
//                    .zoom( 15 )
//                    .bearing( 0 )
//                    .tilt( 45 )
//                    .build();
//            googleMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );


        }
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
