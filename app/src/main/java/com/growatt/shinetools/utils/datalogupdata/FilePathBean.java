package com.growatt.shinetools.utils.datalogupdata;

import androidx.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FilePathBean  extends RealmObject {

    @PrimaryKey
    private int id;

    private String shineX_version;
    private String shineX_user1;
    private String shineX_user2;

    private String shineS_version;
    private String shineS_user1;
    private String shineS_user2;

    public String getShineX_user1() {
        return shineX_user1;
    }

    public void setShineX_user1(String shineX_user1) {
        this.shineX_user1 = shineX_user1;
    }

    public String getShineX_user2() {
        return shineX_user2;
    }

    public void setShineX_user2(String shineX_user2) {
        this.shineX_user2 = shineX_user2;
    }

    public String getShineS_user1() {
        return shineS_user1;
    }

    public void setShineS_user1(String shineS_user1) {
        this.shineS_user1 = shineS_user1;
    }

    public String getShineS_user2() {
        return shineS_user2;
    }

    public void setShineS_user2(String shineS_user2) {
        this.shineS_user2 = shineS_user2;
    }

    public String getShineX_version() {
        return shineX_version;
    }

    public void setShineX_version(String shineX_version) {
        this.shineX_version = shineX_version;
    }

    public String getShineS_version() {
        return shineS_version;
    }

    public void setShineS_version(String shineS_version) {
        this.shineS_version = shineS_version;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @NonNull
    @Override
    public String toString() {
        return "FilePathBean:{"+"id:"+id+"\nshineX_version:"
                +shineX_version +"\nshineX_user1"+shineX_user1+"\nshineX_user2"+shineX_user2+
                "\nshineS_version"+shineS_version +"\nshineS_user1"+shineS_user1+"\nshineS_user2"+shineS_user2;
    }
}
