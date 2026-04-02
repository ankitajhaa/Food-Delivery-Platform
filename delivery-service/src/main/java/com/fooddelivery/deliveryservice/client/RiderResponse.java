package com.fooddelivery.deliveryservice.client;

public class RiderResponse {
    private Long id;
    private String name;
    private String zone;

    public RiderResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
}