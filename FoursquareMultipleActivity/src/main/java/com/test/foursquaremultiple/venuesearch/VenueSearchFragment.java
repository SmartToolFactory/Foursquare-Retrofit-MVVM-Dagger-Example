package com.test.foursquaremultiple.venuesearch;

import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.test.foursquaremultiple.R;
import com.test.foursquaremultiple.databinding.FragmentVenueSearchBinding;
import com.test.foursquaremultiple.utils.QueryValidator;
import com.test.foursquaremultiple.view.callback.IQuery;

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
        //  fragmentBinding.setLifecycleOwner(this);
        fragmentBinding.setIQuery(this);

        observe();


    }

    private void observe() {
        // List Resource is a wrapper class that contains web request status and response data of venues if successful
        mVenueListViewModel.getVenueListResource().observe(this, listResource -> {

            if (listResource == null) return;

            switch (listResource.status) {

                case LOADING:
                    showLoadingDialog();
                    break;

                case ERROR:
                    hideLoadingDialog();
                    showApiFailError();
                    break;

                case SUCCESS:
                    if (!mVenueListViewModel.isEventConsumed) {
                        hideLoadingDialog();

                        mVenueListViewModel.isEventConsumed = true;


                    }
                    break;
            }
        });
    }


    private void showLoadingDialog() {

        if (mProgressbar == null) {
            mProgressbar = new MaterialDialog.Builder(getActivity())
                    .progress(true, 0).content(R.string.searching).cancelable(false)
                    .build();
        }

        mProgressbar.show();

    }

    private void hideLoadingDialog() {

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

        System.out.println("VenueSearchFragment requestLocation()");

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }


        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object

                        Toast.makeText(getActivity(), "VenueSearchFragment requestLocation() onSuccess() location: "
                                + location, Toast.LENGTH_SHORT).show();
                        mUserCurrentLocation = (location.getLatitude() + "," + location.getLongitude());
                    }
                });

    }

    @Override
    public void onQuery() {

        System.out.println("VenueSearchFragment onClick()");

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
}

