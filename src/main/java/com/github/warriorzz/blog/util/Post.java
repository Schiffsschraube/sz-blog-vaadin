package com.github.warriorzz.blog.util;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Post {

    private final VerticalLayout layout;
    private final String title;
    private final String author;
    private final String created;
    private final String lastUpdate;
    private final String category;
    private boolean confirmed = false;

    public Post(VerticalLayout layout, String title, String author, String created, String lastUpdate, String category){
        this.category = category;
        this.layout = layout;
        this.title = title;
        this.author = author;
        this.created = created;
        this.lastUpdate = lastUpdate;
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

    public String getCreated() {
        return created;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public String getCategory(){ return category; }

    public boolean isConfirmed() { return confirmed; }

    public Post setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
        return this;
    }
}

