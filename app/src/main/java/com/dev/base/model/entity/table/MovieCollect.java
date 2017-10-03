package com.dev.base.model.entity.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * author:  ljy
 * date:    2017/9/28
 * description: 电影收藏表，根据该实体结构映射生成相应的数据表
 */

@Entity
public class MovieCollect {

    @Id
    private Long id;
    private String image;
    private String title;
    private String year;

    @Generated(hash = 770475887)
    public MovieCollect(Long id, String image, String title, String year) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.year = year;
    }

    @Generated(hash = 432838481)
    public MovieCollect() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }


}
