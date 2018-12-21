package com.test.foursquaresingle;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.test.foursquaresingle.databinding.ActivityVenueSearchBinding;
import com.test.foursquaresingle.view.venuelist.VenueListFragment;
import com.test.foursquaresingle.view.venuesearch.VenueSearchFragment;
import com.test.foursquaresingle.viewmodel.VenueSearchViewModel;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * This activity is host to {@link VenueSearchFragment} to search Venues
 * and {@link VenueListFragment} to list results inside a RecyclerView
 */
public class MainActivity extends DaggerAppCompatActivity {


    private ActivityVenueSearchBinding mDataBinding;

    private VenueSearchViewModel mVenueListViewModel;


    @Inject
    ViewModelProvider.Factory viewModelFactory;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_venue_search);

        // Create ViewModel to share data between fragments and this activity
        // Search fragment shares venue list with list fragment and venue type with activity to change toolbar title
        mVenueListViewModel = ViewModelProviders.of(this, viewModelFactory).get(VenueSearchViewModel.class);

        // Set view properties
        initViews();

        // Listen fragment change of layout to set toolbar title and home button
        listenFragmentChanges();


        // Observe venue search result to change current fragment
        observeVenueSearchResult();

        // Add search fragment only the first app is created
        // After device rotation savedInstanceState is not null change to list fragment
        // fragment manager and viewModel survives through activity life cycle
        if (savedInstanceState == null) {
            navigateToVenueSearch();
        }

    }

    /**
     * Listen changes onBackStack, add/replace/remove fragment events
     * This is invoked when back button is pressed to return previous fragment
     */
    private void listenFragmentChanges() {

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {


            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.layout_venue_search);


            if (currentFragment != null) {

                System.out.println("MainActivity onBackStackChanged() currentFragment: " + currentFragment);

                try {

                    // Change Toolbar properties on fragment changes
                    if (currentFragment instanceof VenueSearchFragment) {

                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        getSupportActionBar().setDisplayShowHomeEnabled(false);

                        mDataBinding.toolbar.setTitle(R.string.toolbar_title);

                    } else {

                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setDisplayShowHomeEnabled(true);

                        if (mVenueListViewModel.queryLiveData.getValue() != null
                                && mVenueListViewModel.queryLiveData.getValue().venueType != null) {
                            String toolbarTitle = mVenueListViewModel.queryLiveData.getValue().venueType;
                            mDataBinding.toolbar.setTitle(toolbarTitle);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void initViews() {

        // Set Toolbar
        Toolbar toolbar = mDataBinding.toolbar;
        setSupportActionBar(toolbar);

    }


    private void navigateToVenueSearch() {

        int fragmentCount = getSupportFragmentManager().getFragments().size();


        System.out.println("MainActivity navigateToVenueSearch() frag count: " + fragmentCount);

        VenueSearchFragment fragment = VenueSearchFragment.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_venue_search, fragment)
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        mDataBinding.toolbar.setTitle(R.string.toolbar_title);
    }


    private void navigateToVenueList() {

        int fragmentCount = getSupportFragmentManager().getFragments().size();

        System.out.println("MainActivity navigateToVenueList() frag count: " + fragmentCount);


        VenueListFragment venueListFragment = new VenueListFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_venue_search, venueListFragment)
                .addToBackStack(null)
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (mVenueListViewModel.queryLiveData.getValue() != null
                && mVenueListViewModel.queryLiveData.getValue().venueType != null) {
            String toolbarTitle = mVenueListViewModel.queryLiveData.getValue().venueType;
            mDataBinding.toolbar.setTitle(toolbarTitle);
        }


    }


    /**
     * Activity only observes end of query to change fragment to list data if data is retrieved successfully
     */
    private void observeVenueSearchResult() {
        // List Resource is a wrapper class that contains web request status and response data of venues if successful
        mVenueListViewModel.getVenueListResource().observe(this, listResource -> {

            if (listResource == null) return;

            switch (listResource.status) {

                case SUCCESS:
                    System.out.println("Activity SUCCESS mVenueListViewModel.isEventConsumed: "
                            + mVenueListViewModel.isEventConsumed);

                    if (!mVenueListViewModel.isEventConsumed) {
                        navigateToVenueList();
                    }

                    break;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is toolbar's back arrow touch
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


}
