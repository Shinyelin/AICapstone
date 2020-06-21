package com.hansung.android.smart_parking;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class PicamData {

    private String piName;
    private String piPorD;
    private String piTime;
    private String piId;
    private String piTF;

    private String piExtract;
    private Bitmap[] piBlob;

    public String get_piId() {
        return piId;
    }

    public String get_piTF() {
        return piTF;
    }

    public String get_piName() {
        return piName;
    }

    public String get_piPorD() {
        return piPorD;
    }

    public String get_piTime() {
        return piTime;
    }

    public String get_piExtract() {
        return piExtract;
    }

    public Bitmap[] set_piBlob() {
        return piBlob;
    }

    public void set_piName(String piname) {
        this.piName = piname;
    }

    public void set_piPorD(String pipord) {
        this.piPorD = pipord;
    }

    public void set_piTime(String pitime) {
        this.piTime = pitime;
    }

    public void set_piExtract(String piextract) {
        this.piExtract = piextract;
    }

    public void set_piBlob(Bitmap[] data) {
        piBlob = data;
    }

    public void set_piId(String piid) {
        piId = piid;
    }

    public void set_piTF(String pitf) {
        piTF = pitf;
    }


}