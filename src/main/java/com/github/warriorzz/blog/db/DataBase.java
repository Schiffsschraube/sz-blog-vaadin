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
        Document userDocument = userCollection.find(new Document("username", GFG.sha512(login.getUsername()))).first();
        if(userDocument == null) return null;
        if(Objects.requireNonNull(userDocument).get("password").equals(GFG.sha512(login.getPassword())))
            return new UserData(true, UserData.Role.fromString(String.valueOf(userDocument.get("role"))));
        return null;
    }

    public ArrayList<Post> getPosts() {
        ArrayList<Document> documents = new ArrayList<>();
        postCollection.find().forEach(documents::add);
        if(documents.size() == 0) return null;

        ArrayList<Post> posts = new ArrayList<>();

        for(Document document: documents){
            PostBuilder builder = new PostBuilder();
            builder.category((String) document.get("category"));
            builder.author((String) document.get("author"));
            builder.lastUpdate(document.get("lastupdate") != null ? LocalDateTime.parse((String) document.get("lastupdate")) : null);
            builder.created(LocalDateTime.parse((String) document.get("created")));
            builder.title((String) document.get("title"));
            builder.id(document.get("_id").toString());
            for(String line: ((String) document.get("html")).split("\n")){
                for(String line2: line.split("</p>")) {
                    if(line2.startsWith("<p>"))
                        builder.html(new Html(line2 + "</p>"));
                    else
                        builder.html(new Html(line2));
                }
            }
            posts.add(builder.build().setConfirmed(document.get("confirmed").equals("true")).setHtml(document.get("html").toString()));
        }
        return posts;
    }

    public void insertUser(UserData.UserLogin login) {
        Document document = new Document();
        document.put("password", GFG.sha512(login.getPassword()));
        document.put("username", GFG.sha512(login.getPassword()));
        if(login.getRole() != null) document.put("role", login.getRole().name());
        userCollection.insertOne(document);
    }

    public void insertPost(Post post, String html) {
        Document document = new Document();

        document.put("title", post.getTitle());
        document.put("author", post.getAuthor());
        document.put("lastupdate", post.getLastUpdate() == null? null : post.getLastUpdate().toString());
        document.put("created", post.getCreated().toString());
        document.put("category", post.getCategory());
        document.put("html", html);
        document.put("confirmed", "false");

        postCollection.insertOne(document);
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
        Document document = new Document();

        document.put("title", post.getTitle());
        document.put("author", post.getAuthor());
        document.put("lastupdate", lastUpdate ? LocalDateTime.now().toString() : post.getLastUpdate() == null ? null : post.getLastUpdate().toString());
        document.put("created", created ? LocalDateTime.now().toString() : post.getCreated().toString());
        document.put("category", post.getCategory());
        document.put("html", post.getHtml());
        document.put("confirmed", String.valueOf(post.isConfirmed()));

        postCollection.replaceOne(new Document("_id", new ObjectId(post.getID())), document);
    }

    private static DataBase instance;
    public static DataBase getInstance(){
        if(instance == null) instance = new DataBase();
        return instance;
    }
}