package com.bing.lan.fm.ui.album.bean;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * @author 蓝兵
 * @time 2017/3/2  17:02
 */
public class TracksInfoBean implements Serializable {

    public long trackId;
    public int uid;
    public String playUrl64;
    public String playUrl32;
    public String playPathHq;
    public String playPathAacv164;
    public String playPathAacv224;
    public String title;
    public int duration;
    public int albumId;
    public boolean isPaid;
    public int processState;
    public long createdAt;
    public String coverSmall;
    public String coverMiddle;
    public String coverLarge;
    public String nickname;
    public String smallLogo;
    public int userSource;
    public int opType;
    public boolean isPublic;
    public int likes;
    public int playtimes;
    public int comments;
    public int shares;
    public int status;

    public static TracksInfoBean objectFromData(String str) {

        return new Gson().fromJson(str, TracksInfoBean.class);
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPlayUrl64() {
        return playUrl64;
    }

    public void setPlayUrl64(String playUrl64) {
        this.playUrl64 = playUrl64;
    }

    public String getPlayUrl32() {
        return playUrl32;
    }

    public void setPlayUrl32(String playUrl32) {
        this.playUrl32 = playUrl32;
    }

    public String getPlayPathHq() {
        return playPathHq;
    }

    public void setPlayPathHq(String playPathHq) {
        this.playPathHq = playPathHq;
    }

    public String getPlayPathAacv164() {
        return playPathAacv164;
    }

    public void setPlayPathAacv164(String playPathAacv164) {
        this.playPathAacv164 = playPathAacv164;
    }

    public String getPlayPathAacv224() {
        return playPathAacv224;
    }

    public void setPlayPathAacv224(String playPathAacv224) {
        this.playPathAacv224 = playPathAacv224;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public boolean isIsPaid() {
        return isPaid;
    }

    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public int getProcessState() {
        return processState;
    }

    public void setProcessState(int processState) {
        this.processState = processState;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getCoverSmall() {
        return coverSmall;
    }

    public void setCoverSmall(String coverSmall) {
        this.coverSmall = coverSmall;
    }

    public String getCoverMiddle() {
        return coverMiddle;
    }

    public void setCoverMiddle(String coverMiddle) {
        this.coverMiddle = coverMiddle;
    }

    public String getCoverLarge() {
        return coverLarge;
    }

    public void setCoverLarge(String coverLarge) {
        this.coverLarge = coverLarge;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSmallLogo() {
        return smallLogo;
    }

    public void setSmallLogo(String smallLogo) {
        this.smallLogo = smallLogo;
    }

    public int getUserSource() {
        return userSource;
    }

    public void setUserSource(int userSource) {
        this.userSource = userSource;
    }

    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public boolean isIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getPlaytimes() {
        return playtimes;
    }

    public void setPlaytimes(int playtimes) {
        this.playtimes = playtimes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
