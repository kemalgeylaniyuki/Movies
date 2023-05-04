package com.kemalgeylani.newmovies;

public class Movie {

    byte[] image;
    int id;
    String nameText;
    String explanationText;
    String urlText;

    public Movie(byte[] image, int id, String nameText, String explanationText, String urlText) {
        this.image = image;
        this.id = id;
        this.nameText = nameText;
        this.explanationText = explanationText;
        this.urlText = urlText;
    }
}
