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
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class DataBase {

    private final MongoCollection<Document> userCollection;
    private final MongoCollection<Document> postCollection;
    private final MongoCollection<Document> categoryCollection;

    private DataBase() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        MongoClient client = MongoClients.create("mongodb://"
                + dotenv.get("MONGO_USERNAME")
                + ":" + dotenv.get("MONGO_PASSWORD")
                + "@" + dotenv.get("MONGO_HOST")
                + "/" + dotenv.get("MONGO_DATABASE")
                + "?retryWrites=true&w=majority");
        MongoDatabase database = client.getDatabase(dotenv.get("MONGO_DATABASE"));
        userCollection = database.getCollection("UserData", Document.class);
        postCollection = database.getCollection("PostData", Document.class);
        categoryCollection = database.getCollection("CategoryData", Document.class);
    }

    public UserData getUser(UserData.UserLogin login) {
        Document loginDataBase = userCollection.find(new Document("username", GFG.sha512(login.getUsername()))).first();
        if(loginDataBase == null) return null;
        if(Objects.requireNonNull(loginDataBase).get("password").equals(GFG.sha512(login.getPassword())))
            return new UserData(true, UserData.Role.fromString(String.valueOf(loginDataBase.get("role"))));
        return null;
    }

    public ArrayList<Post> getPosts() {
        ArrayList<Document> postsHashMaps = new ArrayList<>();
        postCollection.find().forEach(postsHashMaps::add);
        if(postsHashMaps.size() == 0) return null;

        ArrayList<Post> posts = new ArrayList<>();

        for(Document postMap: postsHashMaps){
            PostBuilder builder = new PostBuilder();
            builder.category((String) postMap.get("category"));
            builder.author((String) postMap.get("author"));
            builder.lastUpdate(postMap.get("lastupdate") != null ? LocalDateTime.parse((String) postMap.get("lastupdate")) : null);
            builder.created(LocalDateTime.parse((String) postMap.get("created")));
            builder.title((String) postMap.get("title"));
            builder.id(postMap.get("_id").toString());
            for(String line: ((String) postMap.get("html")).split("\n")){
                for(String lline: line.split("</p>")) {
                    if(lline.startsWith("<p>"))
                        builder.html(new Html(lline + "</p>"));
                    else
                        builder.html(new Html(lline));
                }
            }
            posts.add(builder.build().setConfirmed(postMap.get("confirmed").equals("true")).setHtml(postMap.get("html").toString()));
        }
        return posts;
    }

    public void insertUser(UserData.UserLogin login) {
        Document loginHashMap = new Document();
        loginHashMap.put("password", GFG.sha512(login.getPassword()));
        loginHashMap.put("username", GFG.sha512(login.getPassword()));
        if(login.getRole() != null) loginHashMap.put("role", login.getRole().name());
        userCollection.insertOne(loginHashMap);
    }

    public void insertPost(Post post, String html) {
        Document postloginHashMap = new Document();

        postloginHashMap.put("title", post.getTitle());
        postloginHashMap.put("author", post.getAuthor());
        postloginHashMap.put("lastupdate", post.getLastUpdate() == null? null : post.getLastUpdate().toString());
        postloginHashMap.put("created", post.getCreated().toString());
        postloginHashMap.put("category", post.getCategory());
        postloginHashMap.put("html", html);
        postloginHashMap.put("confirmed", "false");

        postCollection.insertOne(postloginHashMap);
    }

    public ArrayList<String> getCategories(){
        ArrayList<String> categories = new ArrayList<>();
        Document map = categoryCollection.find().first();
        assert map != null;
        map.keySet().forEach(it -> {
            if(!it.equals("_id")) categories.add(map.get(it).toString());
        });
        return categories;
    }

    public void updatePost(Post post, boolean created, boolean lastUpdate) {
        Document postloginHashMap = new Document();

        postloginHashMap.put("title", post.getTitle());
        postloginHashMap.put("author", post.getAuthor());
        postloginHashMap.put("lastupdate", lastUpdate ? LocalDateTime.now().toString() : post.getLastUpdate() == null ? null : post.getLastUpdate().toString());
        postloginHashMap.put("created", created ? LocalDateTime.now().toString() : post.getCreated().toString());
        postloginHashMap.put("category", post.getCategory());
        postloginHashMap.put("html", post.getHtml());
        postloginHashMap.put("confirmed", String.valueOf(post.isConfirmed()));

        postCollection.replaceOne(new Document("_id", new ObjectId(post.getID())), postloginHashMap);
    }

    private static DataBase instance;
    public static DataBase getInstance(){
        if(instance == null) instance = new DataBase();
        return instance;
    }
}