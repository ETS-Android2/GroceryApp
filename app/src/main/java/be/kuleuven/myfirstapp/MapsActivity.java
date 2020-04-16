package be.kuleuven.myfirstapp;
/** **/

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 15000;
    double latitude,longitude;
    private ArrayList<Groceries> myGroceryList;
    private GroceriesAdapter myAdapter;
    private  String selectedItem;
    private final String nearbyGroceryStore = "supermarket";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initList();


        Spinner spinnerGroceries = findViewById(R.id.spinner_groceries);
        myAdapter=new GroceriesAdapter(this,myGroceryList);

        spinnerGroceries.setAdapter(myAdapter);
        spinnerGroceries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Groceries clickedItem=(Groceries) parent.getItemAtPosition(position);
                String clickedGroceryName= clickedItem.getgName();
                selectedItem=clickedGroceryName;
                Object dataTransfer[] = new Object[2];
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
              // GetSelectedItem(selectedItem,dataTransfer,getNearbyPlacesData);

              switch(selectedItem)
                {

                    case "colruyt":
                        //GetSelectedItem(selectedItem,dataTransfer,getNearbyPlacesData);
                        mMap.clear();
                        String hospital = "supermarket";
                        String url = getUrl(latitude, longitude, hospital);
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;
                        getNearbyPlacesData.Pname="Colruyt";
                        getNearbyPlacesData.execute(dataTransfer);
                        Toast.makeText(MapsActivity.this, "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
                        break;

                    case "aldi":
                        mMap.clear();
                        url = getUrl(latitude, longitude, nearbyGroceryStore);
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;
                        getNearbyPlacesData.Pname="ALDI";
                        getNearbyPlacesData.execute(dataTransfer);
                        Toast.makeText(MapsActivity.this, "Showing Nearby aldi", Toast.LENGTH_SHORT).show();

                        break;
                    case "delhaize":
                        mMap.clear();
                        url = getUrl(latitude, longitude, nearbyGroceryStore);
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;
                        getNearbyPlacesData.Pname="Delhaize";
                        getNearbyPlacesData.execute(dataTransfer);
                        Toast.makeText(MapsActivity.this, "Showing Nearby Delhaize", Toast.LENGTH_SHORT).show();
                        break;
                    case "lidl":
                        mMap.clear();
                        url = getUrl(latitude, longitude, nearbyGroceryStore);
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;
                        getNearbyPlacesData.Pname="Lidl";
                        getNearbyPlacesData.execute(dataTransfer);
                        Toast.makeText(MapsActivity.this, "Showing Nearby lidl", Toast.LENGTH_SHORT).show();

                        break;
                    case "spar":
                        mMap.clear();
                        url = getUrl(latitude, longitude, nearbyGroceryStore);
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;
                        getNearbyPlacesData.Pname="Spar";
                        getNearbyPlacesData.execute(dataTransfer);
                        Toast.makeText(MapsActivity.this, "Showing Nearby Spar", Toast.LENGTH_SHORT).show();
                        break;
                    case "carrefour":
                        mMap.clear();
                        url = getUrl(latitude, longitude, nearbyGroceryStore);
                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;
                        getNearbyPlacesData.Pname="Carrefour";
                        getNearbyPlacesData.execute(dataTransfer);
                        Toast.makeText(MapsActivity.this, "Showing Nearby Carrefour", Toast.LENGTH_SHORT).show();
                        break;

                }


                //Toast.makeText(MapsActivity.this,clickedGroceryName+" selected",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
    }
    private void GetSelectedItem(String groceryName,Object dataTransfer[],GetNearbyPlacesData getNearbyPlacesData ){
        mMap.clear();
        String url = getUrl(latitude, longitude,nearbyGroceryStore);
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        getNearbyPlacesData.Pname=groceryName;
        getNearbyPlacesData.execute(dataTransfer);
        Toast.makeText(MapsActivity.this, "Showing Nearby "+ groceryName, Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }


    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }
        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);


        }
    }
    private void initList(){
        myGroceryList=new ArrayList<>();
        myGroceryList.add(new Groceries("blank",R.drawable.blank));
        myGroceryList.add(new Groceries("colruyt",R.drawable.colruyt));
        myGroceryList.add(new Groceries("delhaize",R.drawable.delhaize));
        myGroceryList.add(new Groceries("aldi",R.drawable.aldi));
        myGroceryList.add(new Groceries("spar",R.drawable.spar));
        myGroceryList.add(new Groceries("lidl",R.drawable.lidl));
        myGroceryList.add(new Groceries("carrefour",R.drawable.carrefour));
    }

    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyD5ZQU67uH2y6SP4Q6A72OEHH3TyUioMSU");

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }


    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}

