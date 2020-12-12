package com.github.warriorzz.blog.db;

import com.github.warriorzz.blog.util.GFG;
import com.github.warriorzz.blog.util.Post;
import com.github.warriorzz.blog.util.PostBuilder;
import com.github.warriorzz.blog.util.UserData;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vaadin.flow.component.Html;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DataBase {

    private final MongoCollection<HashMap> userCollection;
    private final MongoCollection<HashMap> postCollection;

    private DataBase() {
        Dotenv dotenv = Dotenv.load();
        MongoClient client = MongoClients.create("mongodb+srv://"
                + dotenv.get("MONGO_USERNAME")
                + ":" + dotenv.get("MONGO_PASSWORD")
                + "@" + dotenv.get("MONGO_HOST")
                + "/" + dotenv.get("MONGO_DATABASE")
                + "?retryWrites=true&w=majority");
        MongoDatabase database = client.getDatabase(dotenv.get("MONGO_DATABASE"));
        userCollection = database.getCollection("UserData", HashMap.class);
        postCollection = database.getCollection("PostData", HashMap.class);
    }

    public UserData getUser(UserData.UserLogin login) {
        HashMap loginDataBase = userCollection.find(new Document("username", GFG.sha512(login.getUsername()))).first();
        if(loginDataBase == null) return null;
        if(Objects.requireNonNull(loginDataBase).get("password").equals(GFG.sha512(login.getPassword())))
            return new UserData(true, UserData.Role.fromString(String.valueOf(loginDataBase.get("role"))));
        return null;
    }

    public ArrayList<Post> getPosts() {
        ArrayList<HashMap> postsHashMaps = new ArrayList<>();
        postCollection.find().forEach(postsHashMaps::add);
        if(postsHashMaps.size() == 0) return null;

        ArrayList<Post> posts = new ArrayList<>();

        for(HashMap postMap: postsHashMaps){
            PostBuilder builder = new PostBuilder();
            builder.category((String) postMap.get("category"));
            builder.author((String) postMap.get("author"));
            builder.lastUpdate((String) postMap.get("lastupdate"));
            builder.created((String) postMap.get("created"));
            builder.title((String) postMap.get("title"));
            for(String line: ((String) postMap.get("html")).split("\n")){
                for(String lline: line.split("</p>")) {
                    if(lline.startsWith("<p>"))
                        builder.html(new Html(lline + "</p>"));
                    else
                        builder.html(new Html(lline));
                }
            }
            posts.add(builder.build().setConfirmed(!postMap.get("confirmed").equals("false")));
        }
        return posts;
    }

    public void insertUser(UserData.UserLogin login) {
        HashMap<String, String> loginHashMap = new HashMap<>();
        loginHashMap.put("password", GFG.sha512(login.getPassword()));
        loginHashMap.put("username", GFG.sha512(login.getPassword()));
        if(login.getRole() != null) loginHashMap.put("role", login.getRole().name());
        userCollection.insertOne(loginHashMap);
    }

    public void insertPost(Post post, String html) {
        HashMap<String, String> postloginHashMap = new HashMap<>();

        postloginHashMap.put("title", post.getTitle());
        postloginHashMap.put("author", post.getAuthor());
        postloginHashMap.put("lastupdate", post.getLastUpdate());
        postloginHashMap.put("created", post.getCreated());
        postloginHashMap.put("category", post.getCategory());
        postloginHashMap.put("html", html);
        postloginHashMap.put("confirmed", "false");

        postCollection.insertOne(postloginHashMap);
    }

    private static DataBase instance;
    public static DataBase getInstance(){
        if(instance == null) instance = new DataBase();
        return instance;
    }
}