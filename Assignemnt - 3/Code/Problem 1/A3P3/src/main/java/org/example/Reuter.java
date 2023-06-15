package org.example;

public class Reuter {
    private String title;
    private String text;

    public Reuter(String title, String text){
        this.title = title;
        this.text = text;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text){
        this.text = text;
    }
}
