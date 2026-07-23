package com.example.smartplaygroundbookingequipmentrentalapp;

public class PlaygroundModel {
    private String id;
    private String name;
    private String address;
    private double lat;
    private double lng;
    private String sports;
    private double price;
    private float rating;
    private double distance;

    public PlaygroundModel(String id, String name, String address, double lat, double lng, String sports, double price, float rating, double distance) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.sports = sports;
        this.price = price;
        this.rating = rating;
        this.distance = distance;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
    public String getSports() { return sports; }
    public double getPrice() { return price; }
    public float getRating() { return rating; }
    public double getDistance() { return distance; }
}
