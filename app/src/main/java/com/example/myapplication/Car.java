package com.example.myapplication;

public class Car
{
    String carID;
    String category;
    String description;
    String km;
    String manufacturer;
    String model;
    String owner;
    String price;
    boolean relevant;
    String userID;
    String year;

    public Car(String category, String manufacturer, String model, String year, String owner, String km, String price, String description, String carID, String userID, boolean relevant) {
        this.category = category;
        this.manufacturer = manufacturer;
        this.model = model;
        this.year = year;
        this.owner = owner;
        this.km = km;
        this.price = price;
        this.description = description;
        this.carID = carID;
        this.userID = userID;
        this.relevant = relevant;
    }
    public Car()
    {
        this.category = "";
        this.manufacturer = "";
        this.model = "";
        this.year = "";
        this.owner = "";
        this.km = "";
        this.price = "";
        this.description = "";
        this.carID = "";
        this.userID = "";
        this.relevant = false;

    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getKm() {
        return this.km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCarID() {
        return this.carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isRelevant() {
        return this.relevant;
    }

    public void setRelevant(boolean relevant)
    {
        this.relevant = relevant;
    }


}
