package com.dev.base.model.entity;

import java.io.File;

/**
 * author:  ljy
 * date:    2017/9/15
 * description:
 */

public class FileEntity {
    private String name;
    private File file;

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
