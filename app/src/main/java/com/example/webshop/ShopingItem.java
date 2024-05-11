package com.example.webshop;

public class ShopingItem {
    private String name;
    private String description;
    private String price;
    private final int imageResource;

    public ShopingItem(String name, String description, String price, int imageResource) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResource = imageResource;
    }

    public String getName() {return name;}

    public String getDescription() {return description;}

    public String getPrice() {return price;}

    public int getImageResource() {return imageResource;}
}
