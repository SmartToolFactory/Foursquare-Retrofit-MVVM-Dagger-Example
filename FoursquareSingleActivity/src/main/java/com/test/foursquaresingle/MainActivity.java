package com.test.foursquaresingle;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

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
        mVenueListViewModel = ViewModelProviders.of(this, viewModelFactory).get(VenueSearchViewModel.class);

        // Set view properties
        initViews();

        // Listen changes onBackStack, add/replace/remove fragment events
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {

            int fragmentCount = getSupportFragmentManager().getFragments().size();

            Toast.makeText(MainActivity.this, "MainActivity onBackStackChange() FRAG Count: "
                    + fragmentCount, Toast.LENGTH_SHORT).show();

            System.out.println("MainActivity onBackStackChange() frag count: " + fragmentCount);


        });


        // Add search fragment only the first app is created
        // After device rotation savedInstanceState is not null
        if (savedInstanceState == null)
            navigateToVenueSearch();


    }

    private void initViews() {

        // Set Toolbar
        Toolbar toolbar = mDataBinding.toolbar;
        setSupportActionBar(toolbar);
        // Hide arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitle(R.string.toolbar_title);

    }


    private void navigateToVenueSearch() {

        int fragmentCount = getSupportFragmentManager().getFragments().size();

        Toast.makeText(this, "MainActivity navigateToVenueSearch() frag count: " + fragmentCount, Toast.LENGTH_SHORT).show();


        VenueSearchFragment fragment = VenueSearchFragment.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_venue_search, fragment)
                .commit();

    }


    private void navigateToVenueList() {


        int fragmentCount = getSupportFragmentManager().getFragments().size();

        Toast.makeText(this, "MainActivity navigateToVenueList() frag count: " + fragmentCount, Toast.LENGTH_SHORT).show();


        VenueListFragment venueListFragment = new VenueListFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_venue_search, venueListFragment)
                .addToBackStack(null)
                .commit();


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
                    System.out.println("Activity SUCCESS");

                    if (!mVenueListViewModel.isEventConsumed) {
                        navigateToVenueList();
                    }

                    break;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is toolbar's back arrow touch
            case android.R.id.home:
                goBack();
        }
        return super.onOptionsItemSelected(item);
    }


    public void goBack() {
        onBackPressed();
        int fragmentCount = getSupportFragmentManager().getBackStackEntryCount();

        System.out.println("MainActivity onBackPressed() frag count: " + fragmentCount);
    }
}
