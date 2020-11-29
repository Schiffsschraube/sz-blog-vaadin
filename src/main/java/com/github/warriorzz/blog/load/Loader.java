package com.github.warriorzz.blog.load;

import com.github.warriorzz.blog.util.Post;
import com.github.warriorzz.blog.util.PostBuilder;
import com.vaadin.flow.component.Html;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class Loader {

    private final ArrayList<Post> posts = new ArrayList<>();

    public Loader() throws FileNotFoundException {
        for(File file: Objects.requireNonNull(new File("./posts/").listFiles())) { //TODO: PATH

            if(!file.isDirectory()){
                BufferedReader reader = new BufferedReader(	new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                ArrayList<String> lines = new ArrayList<>();
                reader.lines().forEach(lines::add);
                PostBuilder builder = new PostBuilder();
                for(String line: lines){
                    if(line.startsWith("# author: ")){
                        builder.author(line.substring(10));
                    }
                    if(line.startsWith("# created: ")){
                        builder.created(line.substring(11));
                    }
                    if(line.startsWith("# title: ")){
                        builder.title(line.substring(9));
                    }
                    if(line.startsWith("# lastupdate: ")){
                        builder.lastUpdate(line.substring(14));
                    }
                    if(line.startsWith("# category: ")){
                        builder.category(line.substring(12));
                    }
                    if(!line.startsWith("#")){
                        builder.html(new Html(line));
                    }
                }
                posts.add(builder.build());
            }
        }
    }

    public ArrayList<Post> getPosts(){ return posts; }

}
