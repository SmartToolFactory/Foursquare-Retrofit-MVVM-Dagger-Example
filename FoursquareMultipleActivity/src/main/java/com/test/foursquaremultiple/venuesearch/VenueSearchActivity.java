package com.test.foursquaremultiple.venuesearch;

import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.test.foursquaremultiple.R;
import com.test.foursquaremultiple.databinding.ActivityVenueSearchBinding;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * This activity is host to {@link VenueSearchFragment} to search Venues
 * and {@link VenueSearchFragment} to list results inside a RecyclerView
 */
public class VenueSearchActivity extends DaggerAppCompatActivity {


    private ActivityVenueSearchBinding mDataBinding;

    // private VenueSearchViewModel mVenueListViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_venue_search);
        // Create ViewModel to share data between fragments and
        //mVenueListViewModel = ViewModelProviders.of(this, viewModelFactory).get(VenueSearchViewModel.class);

        // Set view properties
        initViews();

    }

    private void initViews() {

        // Set Toolbar
        Toolbar toolbar = mDataBinding.toolbar;
        setSupportActionBar(toolbar);
        // Hide arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitle(R.string.toolbar_title);


        // Add search fragment
        VenueSearchFragment fragment = VenueSearchFragment.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_venue_search, fragment)
                .commit();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


}
