package com.dev.base.model.entity.res;

/**
 * author:  ljy
 * date:    2017/9/27
 * description: （用于豆瓣电影接口）网络请求回调的统一实体。这里是为了配合豆瓣接口返回的数据结构。
 *               开发中，服务器返回的数据结构一般为“状态值”，“描述信息”，“内容数据实体”。请根据具体结构进行调整
 */

public class HttpResult<T> {

    private int count;//请求的数量
    private int start;//请求的起始页码
    private int total;//得到的数据总数
    private String title;//请求结果的描述
    private T subjects;//返回的数据内容，类型不确定，使用泛型T表示


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public T getSubjects() {
        return subjects;
    }

    public void setSubjects(T subjects) {
        this.subjects = subjects;
    }
}
