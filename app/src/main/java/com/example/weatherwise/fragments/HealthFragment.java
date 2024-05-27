package com.example.weatherwise.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentHealthBinding;
import com.example.weatherwise.manager.LocationManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HealthFragment extends Fragment implements LocationManager.LocationUpdateListener {

    private final String DEBUG_TAG = "HealthFragment";

    private FragmentHealthBinding binding;
    private View root;
    private SupportMapFragment googleMapFragment;
    private GoogleMap googleMap;
    private Marker currentLocationMarker;
    private Marker selectedLocationMarker;
    private LatLng selectedLatLng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationManager.getInstance().setLocationUpdateListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHealthBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        setup();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setup() {
        googleMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_googleMap);
        handleGoogleMap();
    }

    private void handleGoogleMap() {
        if (googleMapFragment != null) {
            googleMapFragment.getMapAsync(map -> {
                googleMap = map;
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
                googleMap.setOnMapClickListener(latLng -> {
                    if (selectedLocationMarker != null) {
                        selectedLocationMarker.remove();
                    }
                    selectedLatLng = latLng;
                    updateSelectedLocationMarker();
                });

                Location currentLocation = LocationManager.getInstance().getCurrentLocation();
                if (currentLocation != null) {
                    updateMapWithLocation(currentLocation);
                }
            });
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        if (location != null && googleMap != null) {
            updateMapWithLocation(location);
        }
    }

    private void updateMapWithLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(getAddressFromLocation(location));
        currentLocationMarker = googleMap.addMarker(markerOptions);
        currentLocationMarker.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_marker_current));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private void updateSelectedLocationMarker() {
        if (selectedLatLng != null) {
            if (selectedLocationMarker != null) {
                selectedLocationMarker.remove();
            }
            String address = getAddressFromLatLng(selectedLatLng);
            if (address != null) {
                MarkerOptions markerOptions = new MarkerOptions().position(selectedLatLng).title(address);
                selectedLocationMarker = googleMap.addMarker(markerOptions);
                selectedLocationMarker.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_marker_selected));
            }
        }
    }

    private String getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String placeName = address.getFeatureName();
                if (placeName != null) {
                    return placeName;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Current Location";
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0); // Get the full address
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Selected Location";
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

