package com.example.webshop;

public class ShopingItem {
    private String id;
    private String name;
    private String description;
    private String price;
    private int imageResource;

    public ShopingItem(){};

    public ShopingItem(String name, String description, String price, int imageResource) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResource = imageResource;
    }

    public String _getId() {return id;}
    public String getName() {return name;}

    public String getDescription() {return description;}

    public String getPrice() {return price;}

    public int getImageResource() {return imageResource;}

    public void setId(String id) {this.id = id;}
}
