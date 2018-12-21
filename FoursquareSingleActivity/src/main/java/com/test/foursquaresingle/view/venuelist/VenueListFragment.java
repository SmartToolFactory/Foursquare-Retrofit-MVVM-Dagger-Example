package com.test.foursquaresingle.view.venuelist;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.test.foursquaresingle.view.venuedetail.VenueDetailDialogFragment;
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

    public static VenueListFragment newInstance(String title) {

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

        System.out.println("! LIST FRAGMENT onViewCreated()");

        mVenueDetailViewModel = ViewModelProviders.of(this, viewModelFactory).get(VenueDetailViewModel.class);

        mVenueListViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(VenueSearchViewModel.class);

        // Needed after rotation changes
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (mVenueListViewModel.queryLiveData.getValue() != null
                && mVenueListViewModel.queryLiveData.getValue().venueType != null) {
            String toolbarTitle = mVenueListViewModel.queryLiveData.getValue().venueType;
            getActivity().setTitle(toolbarTitle);
        }


        fragmentBinding.setViewModel(mVenueListViewModel);


        RecyclerView recyclerView = fragmentBinding.recyclerView;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL);

        recyclerView.addItemDecoration(itemDecoration);

        // Create adapter and bind data of ViewModel
        VenueListAdapter adapter = new VenueListAdapter(mVenueListViewModel.getVenueListResource().getValue().data, this);

        // Bind data to RecyclerView from adapter
        recyclerView.setAdapter(adapter);

        // List Resource is a wrapper class that contains web request status and response data of venues if successful
        mVenueListViewModel.getVenueListResource().observe(this, listResource -> {


            if (listResource == null) return;

            switch (listResource.status) {

                case SUCCESS:
                    System.out.println("List Fragment SUCCESS mVenueListViewModel.isEventConsumed: "
                            + mVenueListViewModel.isEventConsumed);

                    adapter.setVenueList(listResource.data);

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
        if (mDialogFragment == null) mDialogFragment = VenueDetailDialogFragment.newInstance(venue);

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


}

