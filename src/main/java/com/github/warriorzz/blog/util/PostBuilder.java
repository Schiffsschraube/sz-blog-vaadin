package com.github.warriorzz.blog.util;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class PostBuilder {

    VerticalLayout layout;
    private String title;
    private String author;
    private String created;
    private String lastUpdate;
    private String category;

    public PostBuilder() {
        layout = new VerticalLayout();
    }

    public PostBuilder category(String category) {
        this.category = category;
        return this;
    }

    public PostBuilder title(String title) {
        this.title = title;
        return this;
    }

    public PostBuilder html(Html html) {
        layout.add(html);
        return this;
    }

    public PostBuilder author(String author) {
        this.author = author;
        return this;
    }

    public PostBuilder created(String created) {
        this.created = created;
        return this;
    }

    public PostBuilder lastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    public Post build() {
        return new Post(layout, title, author, created, lastUpdate, category);
    }
}
