package com.dev.base.model.entity.res;

import java.util.List;

/**
 * author:  ljy
 * date:    2017/9/27
 * description: 根据豆瓣结构返回的内容数据结构而定的实体
 */

public class MovieRes {

    private String id;
    private String title;
    private String year;
    private Rating rating;
    private List<String> genres;
    private MovieImage images;

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year == null ? "" : year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public MovieImage getImages() {
        return images;
    }

    public void setImages(MovieImage images) {
        this.images = images;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public class MovieImage {
        private String small;
        private String medium;
        private String large;

        public String getSmall() {
            return small == null ? "" : small;
        }

        public void setSmall(String small) {
            this.small = small;
        }

        public String getMedium() {
            return medium == null ? "" : medium;
        }

        public void setMedium(String medium) {
            this.medium = medium;
        }

        public String getLarge() {
            return large == null ? "" : large;
        }

        public void setLarge(String large) {
            this.large = large;
        }
    }

    public class Rating {
        private int min;
        private int max;
        private float average;
        private String stars;

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public float getAverage() {
            return average;
        }

        public void setAverage(float average) {
            this.average = average;
        }

        public String getStars() {
            return stars == null ? "" : stars;
        }

        public void setStars(String stars) {
            this.stars = stars;
        }
    }
}
