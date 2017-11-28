package com.example.leila.smartk.Bean;

/**
 * 事件实体类，就是传递的事件，一个组件向另一个组件发送的信息可以储存在一个类中，
 * 该类就是一个事件，会被EventBus发送给订阅者
 * Created by Leila on 2017/11/14.
 * Define events
 */

public class MessageBean {
    private String title;
    private String description;


    private String time;

    public MessageBean(String title, String description, String time) {
        this.title = title;
        this.description = description;
        this.time = time;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

}
