package com.github.warriorzz.blog.util;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDateTime;

public class PostBuilder {

    VerticalLayout layout;
    private String title;
    private String author;
    private LocalDateTime created;
    private String lastUpdate;
    private String category;
    private String id;
    private String html;

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
        this.html += html + "\r\n";
        return this;
    }

    public PostBuilder author(String author) {
        this.author = author;
        return this;
    }

    public PostBuilder created(LocalDateTime created) {
        this.created = created;
        return this;
    }

    public PostBuilder lastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    public PostBuilder id(String id) {
        this.id = id;
        return this;
    }

    public Post build() {
        return new Post(layout, title, author, created, lastUpdate, category, id, html);
    }
}
