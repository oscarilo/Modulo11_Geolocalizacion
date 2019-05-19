package ittepic.com.mx.modulo11_geolocalizacion_domingo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    // Variables globales
    private GoogleMap mMap;

    private Marker marcador;
    private double lat = 0.0;
    private double lon = 0.0;
    String mensaje = "";
    String direccion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }// onCreate

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setTrafficEnabled(true);

        miubicacion();

    }// onMapReady

    private void iniciarLocacion() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsActivado = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsActivado) {
            Intent settings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settings);
        }

    }// iniciarLocacion


    private void asignarLocacion(Location loc) {
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

                if(!list.isEmpty()){
                    Address dirCalles = list.get(0);
                    direccion = (dirCalles.getAddressLine(0));
                }

            } catch (IOException e) {
                System.out.println("ERROR METODO asignarLocacion: " + e.getMessage());
            }
        }
    }// asignarLocacion


    private void agregarMarcador(double lat, double lon) {
        LatLng coordenadas = new LatLng(lat, lon);
        CameraUpdate miubicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);

        if (marcador != null) marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title("Dirección:")
                .snippet(direccion)
        );

        mMap.animateCamera(miubicacion);

    }// agregarMarcador


    private void actualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            agregarMarcador(lat, lon);

        }// if

    }// actualizarUbicacion


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            mensaje = "GPS ACTIVADO";
            mensajes();
        }

        @Override
        public void onProviderDisabled(String provider) {
            mensaje = "GPS DESACTIVADO";
            iniciarLocacion();
            mensajes();
        }
    };


    private static int PETICION_PERMISO_LOCATION = 101;


    private void miubicacion() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PETICION_PERMISO_LOCATION);
            return;
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1200, 0, locationListener);

        }

    }// miubicacion


    private void mensajes() {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }


}// class
