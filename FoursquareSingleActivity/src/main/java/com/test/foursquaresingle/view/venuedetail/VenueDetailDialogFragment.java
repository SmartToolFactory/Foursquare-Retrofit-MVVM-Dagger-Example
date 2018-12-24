package com.test.foursquaresingle.view.venuedetail;

import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.test.foursquaresingle.R;
import com.test.foursquaresingle.databinding.FragmentVenueDetailBinding;
import com.test.foursquaresingle.model.Location;
import com.test.foursquaresingle.model.TipGroup;
import com.test.foursquaresingle.model.Tips;
import com.test.foursquaresingle.model.UserComment;
import com.test.foursquaresingle.model.Venue;

import java.util.List;


public class VenueDetailDialogFragment extends DialogFragment implements OnMapReadyCallback {

    public static final String VENUE_ARG = "details";

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

        // savedInstanceState returns null, use getArguments()
        //  mVenue = getArguments().getParcelable(VENUE_ARG);
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


        // Set up RecyclerView for User Comments

        RecyclerView recyclerView = binding.recyclerViewUserComments;

        Tips tips = mVenue.getTips();
        List<TipGroup> groups = tips.getGroups();


        List<UserComment> userComments = groups.get(0).getItems();

        UserCommentAdapter adapter = new UserCommentAdapter(userComments);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        // Set up Map
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);

        builder.setView(binding.getRoot());

        return builder.create();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        Location location = mVenue.getLocation();

        googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLat(), location.getLng())).title(""));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLat(), location.getLng()), 14));
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // TODO If SupportMapFragment is not removed app crashes because of adding it with duplicate id or null
        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
    }
}