package com.aldidwikip.kontak.Model;

import com.google.gson.annotations.SerializedName;

public class Kontak {
    @SerializedName("id")
    private String id;
    @SerializedName("nama")
    private String nama;
    @SerializedName("nomor")
    private String nomor;
    @SerializedName("alamat")
    private String alamat;
    @SerializedName("avatar")
    private String avatar;

    public Kontak(String id, String nama, String nomor, String alamat, String avatar) {
        this.id = id;
        this.nama = nama;
        this.nomor = nomor;
        this.alamat = alamat;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}