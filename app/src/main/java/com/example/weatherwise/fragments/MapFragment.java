package com.example.weatherwise.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.weatherwise.R;
import com.example.weatherwise.databinding.FragmentMapsBinding;
import com.example.weatherwise.manager.LocationManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.model.Place.Type;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.model.PlaceLikelihood;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements LocationManager.LocationUpdateListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final String DEBUG_TAG = "HealthFragment";

    private FragmentMapsBinding binding;
    private View root;
    private SupportMapFragment googleMapFragment;
    private GoogleMap googleMap;
    private Marker currentLocationMarker;
    private Marker selectedLocationMarker;
    private LatLng selectedLatLng;
    private PlacesClient placesClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationManager.getInstance().setLocationUpdateListener(this);

        // Initialize the Places API
        Places.initialize(requireContext(), "AIzaSyBYXNXd_nwYhDNT3ngF7HQ5sKnqYZ6jcRs");
        placesClient = Places.createClient(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
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

                // Check for location permissions
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    googleMap.setMyLocationEnabled(true);
                    Location currentLocation = LocationManager.getInstance().getCurrentLocation();
                    if (currentLocation != null) {
                        updateMapWithLocation(currentLocation);
                        findNearestMedicalCenter(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handleGoogleMap();
            } else {
                // Permission denied
                // Handle the case where the user denied the permission
            }
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        if (googleMap != null) {
            updateMapWithLocation(location);
            findNearestMedicalCenter(new LatLng(location.getLatitude(), location.getLongitude()));
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
            String featuredName = getFeaturedNameFrom(selectedLatLng);
            if (address != null) {
                MarkerOptions markerOptions = new MarkerOptions().position(selectedLatLng).title(address);
                selectedLocationMarker = googleMap.addMarker(markerOptions);
                selectedLocationMarker.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_marker_selected));
                binding.tvAddress.setText(address);
                binding.tvFeaturedName.setText(featuredName);
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
                return address.getFeatureName();
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
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Selected Location";
    }

    private String getFeaturedNameFrom(LatLng latLng) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getFeatureName();
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

    @SuppressLint("SetTextI18n")
    private void findNearestMedicalCenter(LatLng currentLatLng) {
        // Define the place fields you want to use
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        // Create a FindCurrentPlaceRequest
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Call findCurrentPlace and handle the response
        placesClient.findCurrentPlace(request).addOnSuccessListener(response -> {
            Place nearestMedicalCenter = null;
            float minDistance = Float.MAX_VALUE;

            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                Place place = placeLikelihood.getPlace();
                List<Type> placeTypes = place.getTypes();
                if (placeTypes != null && (placeTypes.contains(Type.HOSPITAL) || placeTypes.contains(Type.DOCTOR))) {
                    LatLng placeLatLng = place.getLatLng();
                    if (placeLatLng != null) {
                        float[] results = new float[1];
                        Location.distanceBetween(currentLatLng.latitude, currentLatLng.longitude,
                                placeLatLng.latitude, placeLatLng.longitude, results);
                        float distance = results[0];
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestMedicalCenter = place;
                        }
                    }
                }
            }


            if (nearestMedicalCenter != null) {
                binding.tvAddress.setText(nearestMedicalCenter.getAddress());
                binding.tvFeaturedName.setText(nearestMedicalCenter.getName());
                MarkerOptions markerOptions = new MarkerOptions().position(nearestMedicalCenter.getLatLng()).title(nearestMedicalCenter.getName());
                selectedLocationMarker = googleMap.addMarker(markerOptions);
                selectedLocationMarker.setIcon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_marker_selected));
            } else {
                binding.tvAddress.setText("No nearby medical center found");
                binding.tvFeaturedName.setText("");
            }
        }).addOnFailureListener(Throwable::printStackTrace);
    }
}
