package com.api.demo.bus.event;

/**
 * author:  ljy
 * date:    2018/12/14
 * description:
 */

public class CommonEvent {

    private String content;
    private String createTime;

    public CommonEvent(String content, String createTime) {
        this.content = content;
        this.createTime = createTime;
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime == null ? "" : createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
