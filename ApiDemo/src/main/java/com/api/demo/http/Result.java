package com.api.demo.http;

import java.util.List;

/**
 * author:  ljy
 * date:    2018/11/27
 * description: 豆瓣接口请求后返回的实体，去除了其他没用到的字段
 */

public class Result {

    private List<Res> subjects;

    public List<Res> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Res> subjects) {
        this.subjects = subjects;
    }

    public class Res{
        private String title;

        public String getTitle() {
            return title == null ? "" : title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
