package com.example.android_lab_assignment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android_lab_assignment.Nearby.GetNearbyPlaceData;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.dynamic.SupportFragmentWrapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {

SupportMapFragment fragment;
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





    public MapFragment() {
        // Required empty public constructor

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getUserLocation();
        if(!checkPermission())
        {
            requestPermission();
        }
        else
        {
            fusedLocationProviderClient.requestLocationUpdates( locationRequest, locationCallback, Looper.myLooper() );
        }
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById( R.id.map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }




        fragment.getMapAsync(this);



        //search places



        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if(autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS,Place.Field.ADDRESS_COMPONENTS));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    LatLng latLng = place.getLatLng();
                    if(latLng!=null) {
                        //mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng)
                        .title(place.getName())
                        .snippet(place.getAddress())
                        .icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_AZURE ) ));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12.0f));
                    }

                    //Toast.makeText(getContext(), "" + temp, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull Status status) {

                    Toast.makeText(getContext(), "" + status, Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_map, container, false);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingBtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity());
                View sheetView = getActivity().getLayoutInflater().inflate(R.layout.map_type_sheet, null);
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

        Button restaurant = view.findViewById(R.id.restaurant);
        Button museum = view.findViewById(R.id.mueseum);
        Button hospital = view.findViewById(R.id.hospital);
        Button school = view.findViewById(R.id.school);
        Button cafe = view.findViewById(R.id.cafe);
        restaurant.setOnClickListener(this);
        museum.setOnClickListener(this);
        hospital.setOnClickListener(this);
        school.setOnClickListener(this);
        cafe.setOnClickListener(this);

        directionBtn = view.findViewById(R.id.direction_btn);
        directionBtn.setOnClickListener(this);


       favbtn = view.findViewById(R.id.fav_btn);
        favbtn.setOnClickListener(this);


        //bottomNavigationView = view.findViewById(R.id.bottomNavigationView);

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(mapType );
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);
        setStoredMarkers();

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

                LocationDB locationDB = LocationDB.getInstance(getContext());
                List<FavLocation> locations = locationDB.daoObjct().getDefault();

                FavLocation location = new FavLocation(locations.size()+1,latLng.latitude,latLng.longitude);

                String address = getAddress(latLng.latitude,latLng.longitude);
                String name = getName(latLng.latitude,latLng.longitude);
                location.setName(name);
                location.setAddress(address);
                locationDB.daoObjct().insert(location);

                Toast.makeText(getContext(),"Size" +locations.size(),Toast.LENGTH_SHORT).show();

                Toast.makeText(getContext(),address,Toast.LENGTH_SHORT).show();
                setStoredMarkers();






            }
        } );


    }

    private String getName(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses!=null && addresses.size() > 0 ) {
                Address address = addresses.get(0);
                result.append(address.getFeatureName());

            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }


        return result.toString();
    }

    private String getDirectionUrl()
    {
        StringBuilder placeUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?"  );
        placeUrl.append("origin="+latitude+","+longitude);

        placeUrl.append("&destination="+destLat+","+destLong);
        placeUrl.append("&key="+getString(R.string.api_key_class));
        return placeUrl.toString();
    }

    private void setStoredMarkers()
    {

        LocationDB placesDB = LocationDB.getInstance( getContext() );
        List<FavLocation> locations = placesDB.daoObjct().getDefault();


        for(int i=0;i<locations.size();i++)
        {
            LatLng latLng = new LatLng( locations.get( i ).getLatitude(), locations.get( i ).getLongitude() );
            CameraPosition cameraPosition = CameraPosition.builder()
                    .target( latLng )
                    .zoom( 15 )
                    .bearing( 0 )
                    .tilt( 45 )
                    .build();

            Marker marker =  mMap.addMarker(new MarkerOptions().position( latLng )
                    .title( locations.get(i).getAddress())
                    .draggable( true )
                    .snippet( "you are going there" )
                    .icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_AZURE ) )
            );
           // marker.showInfoWindow();




        }
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses!=null && addresses.size() > 0 ) {
                Address address = addresses.get(0);
                result.append(address.getAddressLine(0)).append(" ");
                result.append(address.getThoroughfare()).append(" ");
                //result.append(address.getLocality()).append(" ");
                result.append(address.getPostalCode()).append(" ");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }


        return result.toString();
    }


    private Boolean checkPermission()
    {
        int permissionState = ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION );
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions( getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE );

    }

    private void getUserLocation()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( getActivity() );
        locationRequest = new LocationRequest();
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        locationRequest.setInterval( 5000 );
        locationRequest.setFastestInterval( 3000 );
        locationRequest.setSmallestDisplacement( 10 );
        setHomeMarker();
        //setFavouriteMarkers();


    }
    private void setHomeMarker(){
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations())
                {
                    //new
                    LatLng userLocation = new LatLng( location.getLatitude(), location.getLongitude());
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target( userLocation )
                            .zoom( 15 )
                            .bearing( 0 )
                            .tilt( 45 )
                            .build();
                    mMap.animateCamera( CameraUpdateFactory.newCameraPosition( cameraPosition ) );
                    //mMap.addMarker( new MarkerOptions().position( userLocation )
                            //.title( "Your Location" ));




                    // .icon( BitmapDescriptorFactory.fromResource( R.drawable.icon ) ));


                }
            }
        };
        //setStoredMarkers();
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.restaurant:
                getPlaces("restaurant");
                Toast.makeText( getContext(), "Restautants", Toast.LENGTH_SHORT ).show();
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
                Object[] dataTransfer = new Object[4];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(destLat,destLong);
                dataTransfer[3] = new LatLng(latitude,longitude);
                GetDirection getDirection = new GetDirection();
                getDirection.execute(dataTransfer);
                directionRequested = true;
                directionBtn.setVisibility(View.GONE);

                break;


            case R.id.fav_btn:

                if(presentInDB)
                {


                    Toast.makeText(getContext(),"Removed",Toast.LENGTH_SHORT).show();
                    favbtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_nfav));
                }
                else
                {

                    LocationDB locationDB = LocationDB.getInstance(getContext());
                    List<FavLocation> favLocations = locationDB.daoObjct().getDefault();
                    FavLocation favLocation = new FavLocation(favLocations.size()+1,destLat,destLong);
                    String address = getAddress(destLat,destLong);
                    favLocation.setAddress(address);
                    locationDB.daoObjct().insert(favLocation);
                    Toast.makeText(getContext(),"Added",Toast.LENGTH_SHORT).show();
                    favbtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_fav));
                }


        }

    }

    void getPlaces(String place)
    {
        url = getUrl( latitude, longitude, place );
        Log.i("MainActivity", url);
        // setmarkers( url );
        Object[] dataTransfer = new Object[3];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        //dataTransfer[3] = new LatLng(null,null);
        //dataTransfer[2] = place;
        //dataTransfer[3] = getContext();
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

    @Override
    public boolean onMarkerClick(final Marker marker) {

       // bottomNavigationView.setVisibility(View.GONE);
        destLat = marker.getPosition().latitude;
        destLong = marker.getPosition().longitude;
        directionBtn.setVisibility(View.VISIBLE);
        favbtn.setVisibility(View.VISIBLE);
        Boolean present = false;
        LocationDB locationDB = LocationDB.getInstance(getContext());
        List<FavLocation> favLocations = locationDB.daoObjct().getDefault();
        for (int i =0;i<favLocations.size();i++)
        {
            if(destLat == favLocations.get(i).getLatitude() && destLong == favLocations.get(i).getLongitude())
            {
                present = true;
            }
        }
        if(present)
        {
            favbtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_fav));
            presentInDB = true;
            //favbtn.setVisibility(View.GONE);
        }
        else
        {
            favbtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_nfav));
            presentInDB = false;
            //favbtn.setVisibility(View.VISIBLE);
        }

        return false;
    }



}
