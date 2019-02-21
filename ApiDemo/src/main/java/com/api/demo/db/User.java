package com.api.demo.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * author:  ljy
 * date:    2018/12/13
 * description: 用户表
 * 根据该实体结构映射生成相应的数据表
 * 更多的注解介绍请阅读我的博客 <a>https://www.jianshu.com/p/11bdd9d761e6</a>
 */
@Entity
public class User {

    @Id
    private Long id;
    private String name;
    private Integer age;

    @Generated(hash = 1499888241)
    public User(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


}
