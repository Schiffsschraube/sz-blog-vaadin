package com.github.warriorzz.blog.util;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDateTime;

public class Post implements Comparable<Post> {

    private final VerticalLayout layout;
    private final String title;
    private final String author;
    private final LocalDateTime created;
    private final String lastUpdate;
    private final String category;
    private final String id;
    private boolean confirmed = false;

    public Post(VerticalLayout layout, String title, String author, LocalDateTime created, String lastUpdate, String category, String id){
        this.category = category;
        this.layout = layout;
        this.title = title;
        this.author = author;
        this.created = created;
        this.lastUpdate = lastUpdate;
        this.id = id;
        layout.setId("post-layout");
    }

    public VerticalLayout getLayout() {
        return layout;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public String getCategory(){ return category; }

    public String getID() { return id; }

    public boolean isConfirmed() { return confirmed; }

    public Post setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
        return this;
    }

    @Override
    public int compareTo(Post o) {
        if(o.getCreated().equals(created)) return 0;
        if(o.getCreated().isBefore(created)) return -1;
        return 1;
    }
}