package com.peoplereport.peoplereport.ClassModels;

import java.util.HashMap;

/**
 * creado por  Christian en la fecha 2018-05-24.
 */
public class Reports {

    private String foto;
    private String pictureProfile;
    private String ubication;
    private String descripcion;
    private String usuario;
    private int views;
    private HashMap<String, LikesModels> likes;
    private HashMap<String, LikesModels> comments;
    private String titulo;
    private String postid;
    private String date;
    private long posttime;

    public Reports() {
    }

    public Reports(String foto, String pictureProfile, String ubication, String descripcion, String usuario, int views, HashMap<String, LikesModels> likes, HashMap<String, LikesModels> comments, String titulo, String postid, String date, long posttime) {
        this.foto = foto;
        this.pictureProfile = pictureProfile;
        this.ubication = ubication;
        this.descripcion = descripcion;
        this.usuario = usuario;
        this.views = views;
        this.likes = likes;
        this.comments = comments;
        this.titulo = titulo;
        this.postid = postid;
        this.date = date;
        this.posttime = posttime;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getPictureProfile() {
        return pictureProfile;
    }

    public void setPictureProfile(String pictureProfile) {
        this.pictureProfile = pictureProfile;
    }

    public String getUbication() {
        return ubication;
    }

    public void setUbication(String ubication) {
        this.ubication = ubication;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public HashMap<String, LikesModels> getLikes() {
        return likes;
    }

    public void setLikes(HashMap<String, LikesModels> likes) {
        this.likes = likes;
    }

    public HashMap<String, LikesModels> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, LikesModels> comments) {
        this.comments = comments;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getPosttime() {
        return posttime;
    }

    public void setPosttime(long posttime) {
        this.posttime = posttime;
    }
}