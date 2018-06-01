package com.example.andriod_pan.assetkcpie.model;

/**
 * Created by Administrator on 2018/4/28.
 */

public class AssetKcData {

    int Color;
    String desc;
    float num;
    String sNum;

    public AssetKcData(int color, String desc, float num, String snum) {
        Color = color;
        this.desc = desc;
        this.num = num;
        this.sNum = snum;
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public float getNum() {
        return num;
    }

    public void setNum(float num) {
        this.num = num;
    }

    public String getSNum() {
        return sNum;
    }

    public void setSNum(String snum) {
        this.sNum = snum;
    }
}
