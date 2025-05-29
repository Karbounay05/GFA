package com.firstsetup.myapplication.model;

public class User {
    private String nom;
    private String prenom;
    private String tel;
    private String email;
    private String password;
    private String region;
    private String ville;
    private String zone ;
    public User(String nom, String prenom, String tel, String email,String password, String region, String ville, String zone) {
        this.nom = nom;
        this.prenom = prenom;
        this.tel = tel;
        this.email = email;
        this.password = password;
        this.region = region;
        this.ville = ville;
        this.zone = zone;
    }
    public User() {
        // Constructeur par défaut
    }
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public String getVille() {
        return ville;
    }
    public void setVille(String ville) {
        this.ville = ville;
    }
    public String getZone() {
        return zone;
    }
    public void setZone(String zone) {
        this.zone = zone;
    }
}

