package com.example.hackerNews.entity;

import javax.persistence.*;

@Entity
@Table(name = "News")
public class NewsEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String name;
    private String url;
    @Column(columnDefinition = "text")
    private String content;
    private String createdAt;

    public NewsEntity() {
    }

    public NewsEntity(String title, String name, String url, String content, String createdAt) {
        this.title = title;
        this.name = name;
        this.url = url;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
