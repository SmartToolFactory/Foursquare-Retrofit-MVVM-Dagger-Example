package com.test.foursquaremultiple.di;




import com.test.foursquaremultiple.venuesearch.VenueSearchActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityContributorModule {

    @ContributesAndroidInjector(modules = SearchFragmentContributorModule.class)
    abstract VenueSearchActivity contributeVenueSearchActivity();

}
