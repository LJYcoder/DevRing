package com.dev.base.model.entity.table;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * author:  ljy
 * date:    2017/9/28
 * description: 电影收藏表，根据该实体结构映射生成相应的数据表
 *              更多的注解介绍请阅读我的博客 http://blog.csdn.net/ljy_programmer/article/details/78257528
 */

@Entity
public class MovieCollect {

    @Id
    private Long id;
    private String movieImage;
    private String title;
    private String year;

    @Generated(hash = 1053700259)
    public MovieCollect(Long id, String movieImage, String title, String year) {
        this.id = id;
        this.movieImage = movieImage;
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

    public String getMovieImage() {
        return this.movieImage;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
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
