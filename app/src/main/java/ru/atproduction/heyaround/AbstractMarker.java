package ru.atproduction.heyaround;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

public class AbstractMarker implements ClusterItem {

    private double latitude;
    private double longitude;
    private String name;

    private String id;
    private MarkerOptions marker;
    private String owner;

    public AbstractMarker(double latitude, double longitude, String name, String id,String owner) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.id = id;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    protected AbstractMarker(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }

    private void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public MarkerOptions getMarker() {
        return marker;
    }

    public void setMarker(MarkerOptions marker) {
        this.marker = marker;
    }

    public String getOwner() {
        return owner;
    }
}
