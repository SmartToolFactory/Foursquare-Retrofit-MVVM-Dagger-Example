package com.test.foursquaresingle.view.venuelist;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.foursquaresingle.MainActivity;
import com.test.foursquaresingle.R;
import com.test.foursquaresingle.databinding.FragmentVenueListBinding;
import com.test.foursquaresingle.model.Venue;
import com.test.foursquaresingle.view.callback.OnVenueClickListener;
import com.test.foursquaresingle.viewmodel.VenueDetailViewModel;
import com.test.foursquaresingle.viewmodel.VenueSearchViewModel;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class VenueListFragment extends DaggerFragment implements OnVenueClickListener {

    @Inject
    ViewModelProvider.Factory viewModelFactory;


    private FragmentVenueListBinding fragmentBinding;

    private VenueSearchViewModel mVenueListViewModel;

    private VenueDetailViewModel mVenueDetailViewModel;

    private VenueDetailDialogFragment mDialogFragment;

    public static VenueListFragment newInstance() {
        return new VenueListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_venue_list, container, false);

        return fragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mVenueDetailViewModel = ViewModelProviders.of(this, viewModelFactory).get(VenueDetailViewModel.class);

        mVenueListViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(VenueSearchViewModel.class);

        fragmentBinding.setViewModel(mVenueListViewModel);

        // Set Toolbar title and hide back button
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(mVenueListViewModel.queryLiveData.getValue().type);

        RecyclerView recyclerView = fragmentBinding.recyclerView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL);

        recyclerView.addItemDecoration(itemDecoration);

        // Create adapter and bind data of ViewModel
        VenueAdapter adapter = new VenueAdapter(mVenueListViewModel.getVenueListResource().getValue().data, this);

        // Bind data to RecyclerView from adapter
        recyclerView.setAdapter(adapter);

        // List Resource is a wrapper class that contains web request status and response data of venues if successful
        mVenueListViewModel.getVenueListResource().observe(this, listResource -> {

            if (listResource == null) return;

            switch (listResource.status) {

                case SUCCESS:
                    System.out.println("List Fragment SUCCESS");

                    if (listResource.data != null && !mVenueListViewModel.isEventConsumed) {

                        adapter.setVenueList(listResource.data);
                        // Consume ViewModel data to not trigger SUCCESS events on device rotation
                        mVenueListViewModel.isEventConsumed = true;

                    }

                    break;
            }

        });


        mVenueDetailViewModel.getVenueDetail().observe(this, venueResource -> {

            if (venueResource == null) return;

            switch (venueResource.status) {

                case SUCCESS:
                    if (venueResource.data != null && !mVenueDetailViewModel.isEventConsumed)
                        showVenueDetails(venueResource.data);
                    break;
            }
        });
    }


    @Override
    public void onClick(Venue venue) {


        String id = venue.getId();

        mVenueDetailViewModel.queryVenueDetail(id);


    }

    private void showVenueDetails(Venue venue) {
        if (mDialogFragment == null) mDialogFragment = new VenueDetailDialogFragment();

        mDialogFragment.setVenue(venue);
        mDialogFragment.show(getFragmentManager(), "venueDetail");

        // TODO FIX: to not fire SUCCESS event of Live Data
        mVenueDetailViewModel.isEventConsumed = true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // This is for preventing memory leaks
        if ((mDialogFragment != null) && mDialogFragment.isAdded() && mDialogFragment.isResumed()) {
            mDialogFragment.dismiss();
        }

        mDialogFragment = null;
    }


    /**
     * Static class to display Dialog to display Venues via DialogFragment to prevent leaks and live through device rotations
     */
    public static class VenueDetailDialogFragment extends DialogFragment {

        private Venue venue;

        public VenueDetailDialogFragment() {

        }

        public void setVenue(Venue venue) {
            this.venue = venue;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

            builder.setTitle(venue.getName())
                    .setMessage("Address: " + venue.getAddress() + ", Country: " + venue.getCountry() + ", Tips")
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });


            // Create the AlertDialog object and return it
            return builder.create();
        }
    }


}

