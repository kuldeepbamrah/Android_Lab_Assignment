package com.example.android_lab_assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android_lab_assignment.Nearby.GetNearbyPlaceData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CustomDirection extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener, View.OnClickListener {


    GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;

    double latitude, longitude;
    double destLat, destLong;
    final int RADIUS = 1500;
    static boolean directionRequest;
    private final int REQUEST_CODE = 1;
    int mapType = GoogleMap.MAP_TYPE_NORMAL;
    String url;
    FloatingActionButton directionBtn,favbtn;
    static Boolean directionRequested,presentInDB;
    BottomNavigationView bottomNavigationView;
    FavLocation favLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_direction);
        favLocation = getIntent().getParcelableExtra("data");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingBtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(CustomDirection.this);
                View sheetView = CustomDirection.this.getLayoutInflater().inflate(R.layout.map_type_sheet, null);
                mBottomSheetDialog.setContentView(sheetView);
                mBottomSheetDialog.show();
                ImageButton normalMap = sheetView.findViewById(R.id.normal_map);
                ImageButton hybridMAp = sheetView.findViewById(R.id.hybrid_map);
                ImageButton satelliteMap = sheetView.findViewById(R.id.satellite_map);
                ImageButton terrainMap = sheetView.findViewById(R.id.terrain_map);

                normalMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mapType = GoogleMap.MAP_TYPE_NORMAL;
                        onMapReady(mMap);
                        mBottomSheetDialog.dismiss();
                    }
                });
                hybridMAp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mapType = GoogleMap.MAP_TYPE_HYBRID;
                        onMapReady(mMap);
                        mBottomSheetDialog.dismiss();
                    }
                });
                satelliteMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mapType = GoogleMap.MAP_TYPE_SATELLITE;
                        onMapReady(mMap);
                        mBottomSheetDialog.dismiss();
                    }
                });terrainMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mapType = GoogleMap.MAP_TYPE_TERRAIN;
                        onMapReady(mMap);
                        mBottomSheetDialog.dismiss();
                    }
                });


            }
        });

        Button restaurant = findViewById(R.id.restaurant);
        Button museum = findViewById(R.id.mueseum);
        Button hospital = findViewById(R.id.hospital);
        Button school = findViewById(R.id.school);
        Button cafe = findViewById(R.id.cafe);
        restaurant.setOnClickListener(this);
        museum.setOnClickListener(this);
        hospital.setOnClickListener(this);
        school.setOnClickListener(this);
        cafe.setOnClickListener(this);

        directionBtn = findViewById(R.id.direction_btn);
        directionBtn.setOnClickListener(this);


        favbtn = findViewById(R.id.fav_btn);
        favbtn.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setMapType(mapType );
        //mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);

        LatLng userLocation = new LatLng( favLocation.getLatitude(), favLocation.getLongitude());
        CameraPosition cameraPosition = CameraPosition.builder()
                .target( userLocation )
                .zoom( 15 )
                .bearing( 0 )
                .tilt( 45 )
                .build();
        mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );
        mMap.addMarker(new MarkerOptions().position(userLocation));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                directionBtn.setVisibility(View.GONE
                );
                favbtn.setVisibility(View.GONE);
            }
        });

        mMap.setOnMapLongClickListener( new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                //calendar










            }
        } );


    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses!=null && addresses.size() > 0 ) {
                Address address = addresses.get(0);
                result.append(address.getAddressLine(0)).append(" ");
                result.append(address.getThoroughfare()).append(" ");
                result.append(address.getLocality()).append(" ");
                result.append(address.getPostalCode()).append(" ");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }


        return result.toString();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.restaurant:
                getPlaces("restaurant");
                Toast.makeText( this, "Restautants", Toast.LENGTH_SHORT ).show();
                break;

            case R.id.school:
                getPlaces("school");
                break;

            case R.id.mueseum:
                getPlaces("museum");
                break;
            case R.id.cafe:
                getPlaces("cafe");
                break;

            case R.id.hospital:
                getPlaces("hospital");
                break;


            case R.id.direction_btn:
                url = getDirectionUrl();
                Log.i("Main Activity",url);
                Object[] dataTransfer = new Object[3];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(destLat,destLong);
                GetDirection getDirection = new GetDirection();
                getDirection.execute(dataTransfer);
                directionRequested = true;
                directionBtn.setVisibility(View.GONE);

                break;


            case R.id.fav_btn:

                if(presentInDB)
                {


                    Toast.makeText(this,"Removed",Toast.LENGTH_SHORT).show();
                    favbtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_nfav));
                }
                else
                {

                    LocationDB locationDB = LocationDB.getInstance(this);
                    List<FavLocation> favLocations = locationDB.daoObjct().getDefault();
                    FavLocation favLocation = new FavLocation(favLocations.size()+1,destLat,destLong);
                    String address = getAddress(destLat,destLong);
                    favLocation.setAddress(address);
                    locationDB.daoObjct().insert(favLocation);
                    Toast.makeText(this,"Added",Toast.LENGTH_SHORT).show();
                    favbtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_fav));
                }


        }
    }
    private String getDirectionUrl()
    {
        StringBuilder placeUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?"  );
        placeUrl.append("origin="+favLocation.getLatitude()+","+favLocation.getLongitude());

        placeUrl.append("&destination="+destLat+","+destLong);
        placeUrl.append("&key="+getString(R.string.api_key_class));
        return placeUrl.toString();
    }
    void getPlaces(String place)
    {
        url = getUrl( favLocation.latitude, favLocation.longitude, place );
        Log.i("MainActivity", url);
        // setmarkers( url );
        Object[] dataTransfer = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        GetNearbyPlaceData getNearbyPlaceData = new GetNearbyPlaceData();
        getNearbyPlaceData.execute(dataTransfer);
    }
    private String getUrl(double latitude, double longitude, String nearbyPlace)
    {
        StringBuilder placeUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?"  );
        placeUrl.append( "location=" + latitude + "," + longitude );
        placeUrl.append( "&radius=" + RADIUS );
        placeUrl.append( "&type=" + nearbyPlace );
        //placeUrl.append( "&keyword=cruise" );
        placeUrl.append( "&key=" + getString(R.string.api_key_class ));
        return placeUrl.toString();
    }
}
