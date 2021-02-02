package com.github.warriorzz.blog.util;

import com.github.warriorzz.blog.db.Database;

public class ArticleClick {

    private final long timeStamp = System.currentTimeMillis();
    private final Post post;

    public ArticleClick(Post post) {
        this.post = post;
    }

    public void addClick() {
        if (Database.getInstance().getPosts().stream().noneMatch(post -> post.getID().equals(this.post.getID())))
            return;
        Database.getInstance().updatePost(Database.getInstance().getPosts().stream().filter(post -> post.getID().equals(this.post.getID())).findFirst().get().addClick(), false, false);
    }

    public boolean checkTimeStamp(long duration) {
        return System.currentTimeMillis() - timeStamp >= duration;
    }
}
