package com.github.warriorzz.blog.util;

import com.vaadin.flow.component.html.*;
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

    public PostBuilder heading1(String header) {
        H3 h3 = new H3(header);
        h3.setId("no-margin-padding");
        layout.add(h3);
        return this;
    }

    public PostBuilder title(String title) {
        this.title = title;
        return this;
    }

    public PostBuilder heading2(String header) {
        H4 h4 = new H4(header);
        h4.setId("no-margin-padding");
        layout.add(h4);
        return this;
    }

    public PostBuilder text(String text) {
        Span paragraph = new Span(text);
        paragraph.setId("no-margin-padding");
        layout.add(paragraph);
        layout.setSizeFull();
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
