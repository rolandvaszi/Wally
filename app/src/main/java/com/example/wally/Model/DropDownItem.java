package com.example.wally.Model;

public class DropDownItem {

    private String text;
    private Integer imageId;

    public DropDownItem(String text, Integer imageId){
        this.text = text;
        this.imageId = imageId;
    }

    public String getText(){
        return text;
    }

    public Integer getImageId(){
        return imageId;
    }

}
