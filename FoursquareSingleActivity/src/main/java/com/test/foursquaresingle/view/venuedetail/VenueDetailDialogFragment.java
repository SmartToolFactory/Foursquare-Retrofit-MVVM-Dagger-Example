package com.test.foursquaresingle.view.venuedetail;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.test.foursquaresingle.R;
import com.test.foursquaresingle.databinding.FragmentVenueDetailBinding;
import com.test.foursquaresingle.model.Location;
import com.test.foursquaresingle.model.Venue;


public class VenueDetailDialogFragment extends DialogFragment implements OnMapReadyCallback {

    public static final String VENUE_ARG = "details";

    private MapView mMap;


    private Venue mVenue;

    public VenueDetailDialogFragment() {

    }


    public static VenueDetailDialogFragment newInstance(Venue venue) {

        VenueDetailDialogFragment venueDetailFragment = new VenueDetailDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(VENUE_ARG, venue);

        venueDetailFragment.setArguments(bundle);

        return venueDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("Dialog onCreate()");

        if (savedInstanceState != null) {
            mVenue = (Venue) getArguments().get(VENUE_ARG);
        }
    }

    public void setVenue(Venue venue) {
        this.mVenue = venue;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());


        FragmentVenueDetailBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.fragment_venue_detail, null, false);

        binding.setVenue(mVenue);

        builder.setView(binding.getRoot());


        mMap = binding.map;

        MapsInitializer.initialize(getContext());
        mMap.onCreate(savedInstanceState);
        mMap.getMapAsync(this);


        return builder.create();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        Location location = mVenue.getLocation();


        System.out.println("VenueDetailDialogFragment onMapReady() map: " + googleMap.getMapType());

        googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLat(), location.getLng())).title(""));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLat(), location.getLng()), 14));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap != null)
            mMap.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMap != null)
            mMap.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMap != null)
            mMap.onDestroy();
    }
}