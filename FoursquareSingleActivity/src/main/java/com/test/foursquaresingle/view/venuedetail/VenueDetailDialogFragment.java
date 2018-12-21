package com.test.foursquaresingle.view.venuedetail;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import com.test.foursquaresingle.R;
import com.test.foursquaresingle.databinding.FragmentVenueDetailBinding;
import com.test.foursquaresingle.model.Venue;


public class VenueDetailDialogFragment extends DialogFragment {

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

        return builder.create();
    }
}