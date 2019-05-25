package com.ervinxie.alue_client.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Pictures {
    @PrimaryKey(autoGenerate = true)
    public int PictureId;

    public String Id;
    public String urlRaw;
    public String urlFull;
    public String urlRegular;
    public String urlSmall;
    public String urlThumb;

    public String title;
    public String description;

    public String FilePath;
    public Boolean Liked;

    public String update_at;

    public Pictures() {
    }


    public Pictures(String id, String urlRaw, String urlFull, String urlRegular, String urlSmall, String urlThumb, String filePath, Boolean liked, String date) {
        Id = id;
        this.urlRaw = urlRaw;
        this.urlFull = urlFull;
        this.urlRegular = urlRegular;
        this.urlSmall = urlSmall;
        this.urlThumb = urlThumb;
        FilePath = filePath;
        Liked = liked;
        this.update_at = date;
    }

    @Ignore
    public String info() {
        return "Pid:"+PictureId+" Id:" + Id + " regularUrl: " + urlRegular + " FilePath:" + FilePath + " Liked:" + Liked + "\n";
    }

    public void setId(String id) {
        Id = id;
    }

    public void setUrlRaw(String urlRaw) {
        this.urlRaw = urlRaw;
    }

    public void setUrlFull(String urlFull) {
        this.urlFull = urlFull;
    }

    public void setUrlRegular(String urlRegular) {
        this.urlRegular = urlRegular;
    }

    public void setUrlSmall(String urlSmall) {
        this.urlSmall = urlSmall;
    }

    public void setUrlThumb(String urlThumb) {
        this.urlThumb = urlThumb;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public void setLiked(Boolean liked) {
        Liked = liked;
    }

    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }

    public int getPictureId() {
        return PictureId;
    }

    public String getId() {
        return Id;
    }

    public String getUrlRaw() {
        return urlRaw;
    }

    public String getUrlFull() {
        return urlFull;
    }

    public String getUrlRegular() {
        return urlRegular;
    }

    public String getUrlSmall() {
        return urlSmall;
    }

    public String getUrlThumb() {
        return urlThumb;
    }

    public String getFilePath() {
        return FilePath;
    }

    public Boolean getLiked() {
        return Liked;
    }

    public String getUpdate_at() {
        return update_at;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPictureId(int pictureId) {
        PictureId = pictureId;
    }
}
