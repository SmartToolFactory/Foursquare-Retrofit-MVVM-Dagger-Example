package com.test.foursquaremultiple.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Venue implements Parcelable {
    private String id;
    private String name;
    private Location location;
    private Tips tips;
    private Photo bestPhoto;

    public Venue() {
    }


    public String getName() {
        return name;
    }

    public String getAddress() {

        if (location == null)
            return null;

        return location.getAddress();
    }

    public String getCountry() {

        if (location == null)
            return null;

        return location.getCountry();
    }

    public Location getLocation() {
        return location;
    }



    public String getId() {
        return id;
    }

    public Tips getTips() {
        return tips;
    }

    public Photo getBestPhoto() {
        return bestPhoto;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeParcelable(this.location, flags);
        dest.writeParcelable(this.tips, flags);
        dest.writeParcelable(this.bestPhoto, flags);
    }

    protected Venue(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.tips = in.readParcelable(Tips.class.getClassLoader());
        this.bestPhoto = in.readParcelable(Photo.class.getClassLoader());
    }

    public static final Creator<Venue> CREATOR = new Creator<Venue>() {
        @Override
        public Venue createFromParcel(Parcel source) {
            return new Venue(source);
        }

        @Override
        public Venue[] newArray(int size) {
            return new Venue[size];
        }
    };
}