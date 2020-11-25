package com.example.contactkapp.Models;

import com.google.firebase.database.ServerValue;

public class UserModel {

    private String userKey;
    private String userName;
    private String backgroundUser;
    private String phone;
    private String gmail;
    private String zalo;
    private String facebook;
    private String instagram;
    private String tiktok;
    private String twitter;
    private String wechat;
    private String github;
    private String QRCode;
    private String DisplayName;
    private String Avatar;
    private Object timestamp;

    public UserModel() {

    }

    public  UserModel (String userName, String displayName){
        this.DisplayName = displayName;
        this.userName = userName;
        this.phone = "phone";
        this.gmail = userName;
        this.zalo = "zalo";
        this.facebook = "facebook";
        this.instagram = "instagram";
        this.tiktok = "tiktok";
        this.twitter = "twitter";
        this.wechat = "wechat";
        this.github = "github";
        this.Avatar = "https://firebasestorage.googleapis.com/v0/b/contactkapp-46957.appspot.com/o/users_photos%2Fimage-k2%40gmail.com-1606286497.jpg?alt=media&token=68b7aeb3-e6e2-4743-bfc4-841e213dc7d4";
        this.backgroundUser = "https://firebasestorage.googleapis.com/v0/b/contactkapp-46957.appspot.com/o/blog_images%2Fimage-background-kiet%40gmail.com1606145632.jpg?alt=media&token=9d02aa65-0c27-4718-ab2c-d0a4144f50ef";
        this.timestamp = ServerValue.TIMESTAMP;
    }
    public UserModel(String userName, String phone, String gmail, String zalo, String facebook, String instagram, String tiktok, String twitter, String wechat, String github) {
        this.userName = userName;
        this.phone = phone;
        this.gmail = gmail;
        this.zalo = zalo;
        this.facebook = facebook;
        this.instagram = instagram;
        this.tiktok = tiktok;
        this.twitter = twitter;
        this.wechat = wechat;
        this.github = github;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBackgroundUser() {
        return backgroundUser;
    }

    public void setBackgroundUser(String background) {
        this.backgroundUser = background;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getZalo() {
        return zalo;
    }

    public void setZalo(String zalo) {
        this.zalo = zalo;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getTiktok() {
        return tiktok;
    }

    public void setTiktok(String tiktok) {
        this.tiktok = tiktok;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }
}
