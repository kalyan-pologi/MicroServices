package com.microservices.user.Utils;

public class Rectangle {
    private int width;
    private int height;

    public Rectangle(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public double calculateArea(){
        return width*height;
    }


}
