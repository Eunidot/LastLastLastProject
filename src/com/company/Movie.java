package com.company;

public class Movie {
    private String title;
    private String genre;
    private String actor;
    private String plot;

    Movie(){
        AppManager.getInstance().getDataManager().setMovie(this);
    }

    // get 메소드
    public String  getTitle(){
        return title;
    }
    public String  getGenre(){
        return genre;
    }
    public String  getActor(){
        return actor;
    }
    public String  getPlot(){
        return plot;
    }

    // set 메소드
    public void setTitle(String title){
        this.title = title;
    }
    public void setGenre(String genre){
        this.genre = genre;
    }
    public void setActor(String actor){
        this.actor = actor;
    }
    public void setPlot(String plot){
        this.plot = plot;
    }
}
