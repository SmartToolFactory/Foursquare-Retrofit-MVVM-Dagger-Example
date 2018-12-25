# Foursquare+Retrofit+MVVM+Dagger Example

This project uses Retrofit2 to retrieve data from Foursquare api. ViewModel and data binding is used to display data. ViewModels are created via ViewModelFactory which is injected via Dagger2.

You need to change     FOURSQUARE_CLIENT_ID, FOURSQUARE_SECRET_ID in FourSquare service class to get results from Foursquare api. To be able to display maps you need to use a valid Google api key.

Single Activity version has 2 fragments and pass data between fragments and MainActivity via ViewModels.
Multiple activity version has 2 activites and 2 fragments. List of venues is passed to second activity via bundle. And dependency injection of this example is slightly different than single activity one.

Note: There is a problem wit
