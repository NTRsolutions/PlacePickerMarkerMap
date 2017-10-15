package com.comp3617.placepickermarkermap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * FinalProject : com.comp3617.placepickermarkermap
 * File: Location.java
 * Created by G.E. Eidsness on 11/30/2016.
 */
class Location implements Parcelable {
    private long id;
    private String googleId;
    private double latitude;
    private double longitude;
    private String name;
    private String address;
    private String remarks;

    Location(){}

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    String getGoogleId() {
        return googleId;
    }
    void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    double getLatitude() {
        return latitude;
    }
    void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    double getLongitude() {
        return longitude;
    }
    void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    String getAddress() {
        return address;
    }
    void setAddress(String address) {
        this.address = address;
    }

    String getRemarks() { return remarks; }
    void setRemarks(String remarks) { this.remarks = remarks;    }

    public static Creator<Location> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s, googleId=%s, latitude=%s, longitude=%s, name=%s, address=%s, remarks=%s]",
                getClass().getSimpleName(), id, googleId, latitude, longitude, name, address, remarks);
    }

    private Location(Parcel in) {
        id = in.readLong();
        googleId = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        name = in.readString();
        address = in.readString();
        remarks = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(getGoogleId());
        dest.writeDouble(getLatitude());
        dest.writeDouble(getLongitude());
        dest.writeString(getName());
        dest.writeString(getAddress());
        dest.writeString(getRemarks());
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }
        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}

