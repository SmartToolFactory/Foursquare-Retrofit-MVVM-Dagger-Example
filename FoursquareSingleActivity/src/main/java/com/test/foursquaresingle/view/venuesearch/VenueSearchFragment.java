package com.test.foursquaresingle.view.venuesearch;

import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.SettingsClient;
import com.test.foursquaresingle.R;
import com.test.foursquaresingle.databinding.FragmentVenueSearchBinding;
import com.test.foursquaresingle.utils.QueryValidator;
import com.test.foursquaresingle.view.callback.IQuery;
import com.test.foursquaresingle.viewmodel.VenueSearchViewModel;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class VenueSearchFragment extends DaggerFragment implements IQuery {

    /**
     *
     */
    private String mUserCurrentLocation;

    private static final int REQUEST_LOCATION = 100;


    @Inject
    ViewModelProvider.Factory viewModelFactory;


    private FragmentVenueSearchBinding fragmentBinding;

    private VenueSearchViewModel mVenueListViewModel;

    private MaterialDialog mProgressbar;


    public static VenueSearchFragment newInstance() {
        return new VenueSearchFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_venue_search, container, false);

        return fragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // getActivity() returns same ViewModel with Activity
        mVenueListViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(VenueSearchViewModel.class);

        fragmentBinding.setViewModel(mVenueListViewModel);
        fragmentBinding.setIQuery(this);
        observeVenueSearch();

    }

    /**
     * This method observes query progress and shows loading progress bar while data retrieval progress is going on.
     * and hides progress bar when query has finished.
     * It's Activities responsibility to change fragments
     */
    private void observeVenueSearch() {
        // List Resource is a wrapper class that contains web request status and response data of venues if successful
        mVenueListViewModel.getVenueListResource().observe(this, listResource -> {

            if (listResource == null) return;

            switch (listResource.status) {

                case LOADING:
                    System.out.println("Search Fragment LOADING");
                    showProgressBar(getString(R.string.searching));
                    break;

                case ERROR:
                    hideProgressBar();
                    showApiFailError();
                    break;

                case SUCCESS:

                    System.out.println("Search Fragment SUCCESS mVenueListViewModel.isEventConsumed: "
                            + mVenueListViewModel.isEventConsumed);

                    if (!mVenueListViewModel.isEventConsumed) {
                        hideProgressBar();
                        mVenueListViewModel.isEventConsumed = true;
                    }

                    break;
            }
        });
    }


    private void showProgressBar(String message) {

        if (mProgressbar == null) {
            mProgressbar = new MaterialDialog.Builder(getActivity())
                    .progress(true, 0).content(message).cancelable(false)
                    .build();
        }

        mProgressbar.show();

    }

    private void hideProgressBar() {

        if (mProgressbar != null) {
            mProgressbar.hide();
        }
    }

    private void showApiFailError() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.popup_search_fail_title)
                .content(R.string.popup_search_fail_content)
                .positiveText(R.string.ok).show();
    }

    private void showWarnUserInput() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.popup_invalid_input_title)
                .content(R.string.popup_invalid_input_content)
                .positiveText(R.string.ok).show();
    }


    /**
     * Runs string validation and query if valida
     */
    private void query() {
        String venueType = fragmentBinding.venueType.getEditableText().toString();
        String venueLocation = fragmentBinding.venueLocation.getEditableText().toString();

        // Check if this is a valid queryVenues
        if (!QueryValidator.isValidQuery(venueType)) {
            // Display error message
            showWarnUserInput();
        } else {

            // Run a query via ViewModel
            if (!venueLocation.isEmpty()) {
                // Venue location is not empty so use it
                mVenueListViewModel.queryVenues(venueType, venueLocation);
                mVenueListViewModel.isEventConsumed = false;

            } else {
                // Venue location is empty use current location
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    requestPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                } else {

                    requestLocation();

                    mVenueListViewModel.queryVenuesByLocation(venueType, mUserCurrentLocation);
                    mVenueListViewModel.isEventConsumed = false;

                }

            }
        }

    }

    private void requestLocation() {

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

      /*  fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {

                    System.out.println("VenueSearchFragment requestLocation() -> getLastLocation(): " + location);

                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object

                        mUserCurrentLocation = (location.getLatitude() + "," + location.getLongitude());
                    }
                });*/

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                System.out.println("VenueSearchActivity requestLocation() -> onLocationResult() location: " + locationResult);

                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data

                }
            }

        };

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.getMainLooper());

        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());


    }

    @Override
    public void onQuery() {

        query();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        query();
    }

    private void requestPermission(final String[] permissions, final int requestCode) {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.CAMERA);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            requestPermissions(permissions, requestCode);
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            requestPermissions(permissions, requestCode);
        }
    }

    /*
     * ********* Location Methods
     */


}

