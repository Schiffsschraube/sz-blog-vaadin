package com.github.warriorzz.blog.load;

import com.github.warriorzz.blog.util.Post;
import com.github.warriorzz.blog.util.PostBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class Loader {

    private final ArrayList<Post> posts = new ArrayList<>();

    public Loader() throws FileNotFoundException, UnsupportedEncodingException {
        for(File file: Objects.requireNonNull(new File("./posts/").listFiles())) { //TODO: PATH

            if(!file.isDirectory()){
                BufferedReader reader = new BufferedReader(	new InputStreamReader(new FileInputStream(file), "UTF8"));
                ArrayList<String> lines = new ArrayList<String>();
                reader.lines().forEach(lines::add);
                StringBuilder builderAtm = new StringBuilder();
                PostBuilder builder = new PostBuilder();
                boolean textActive = false;
                for(String line: lines){
                    if(line.startsWith("# author: ")){
                        builder.author(line.substring(10));
                    }
                    if(line.startsWith("# created: ")){
                        builder.created(line.substring(11));
                    }
                    if(line.startsWith("#h1 ")){
                        if(textActive){
                            builder.text(builderAtm.toString());
                            builderAtm = new StringBuilder();
                            textActive = false;
                        }
                        builder.heading1(line.substring(4));
                    }
                    if(line.startsWith("#h2 ")){
                        if(textActive){
                            builder.text(builderAtm.toString());
                            builderAtm = new StringBuilder();
                            textActive = false;
                        }

                        builder.heading2(line.substring(4));
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
                        builderAtm.append(line).append("\n");
                        textActive = true;
                    }
                }
                if(!builderAtm.equals(new StringBuilder()))
                    builder.text(builderAtm.toString());
                posts.add(builder.build());
            }
        }


    }

    public ArrayList<Post> getPosts(){ return posts; }

}
