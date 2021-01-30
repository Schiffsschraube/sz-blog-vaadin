package com.github.warriorzz.blog.db;

import com.github.warriorzz.blog.util.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vaadin.flow.component.Html;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Database {

    private final MongoCollection<Document> userCollection;
    private final MongoCollection<Document> postCollection;
    private final MongoCollection<Document> categoryCollection;
    private final MongoCollection<Document> deletedPostCollection;

    private Database() {
        MongoClient client = MongoClients.create("mongodb://"
                + Config.MONGO_USERNAME
                + ":" + Config.MONGO_PASSWORD
                + "@" + Config.MONGO_HOST
                + "/" + Config.MONGO_DATABASE
                + "?retryWrites=true&w=majority");
        MongoDatabase database = client.getDatabase(Config.MONGO_DATABASE);
        userCollection = database.getCollection("UserData", Document.class);
        postCollection = database.getCollection("PostData", Document.class);
        categoryCollection = database.getCollection("CategoryData", Document.class);
        deletedPostCollection = database.getCollection("DeletedPostCollection", Document.class);
    }

    public UserData getUser(UserData.UserLogin login) {
        Document userDocument = userCollection.find(new Document("username", GFG.sha512(login.getUsername()))).first();
        if(userDocument == null) return null;
        if(Objects.requireNonNull(userDocument).get("password").equals(GFG.sha512(login.getPassword())))
            return new UserData(true, UserData.Role.fromString(String.valueOf(userDocument.get("role"))), userDocument.get("_id").toString());
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
            builder.author((String) document.get("author"), document.get("authorId") == null ? "" : document.get("authorId").toString());
            builder.lastUpdate(document.get("lastupdate") != null ? LocalDateTime.parse((String) document.get("lastupdate")) : null);
            builder.created(LocalDateTime.parse((String) document.get("created")));
            builder.title((String) document.get("title"));
            builder.id(document.get("_id").toString());
            builder.clickCounter(document.getInteger("clicks") == null? 0 : document.getInteger("clicks"));
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
        document.put("authorId", post.getAuthorID());
        document.put("lastupdate", post.getLastUpdate() == null? null : post.getLastUpdate().toString());
        document.put("created", post.getCreated().toString());
        document.put("category", post.getCategory());
        document.put("html", html);
        document.put("confirmed", "false");
        document.put("clicks", post.getClickCounter());

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
        document.put("authorId", post.getAuthorID());
        document.put("lastupdate", lastUpdate ? LocalDateTime.now().toString() : post.getLastUpdate() == null ? null : post.getLastUpdate().toString());
        document.put("created", created ? LocalDateTime.now().toString() : post.getCreated().toString());
        document.put("category", post.getCategory());
        document.put("html", post.getHtml());
        document.put("confirmed", String.valueOf(post.isConfirmed()));
        document.put("clicks", post.getClickCounter());

        postCollection.replaceOne(new Document("_id", new ObjectId(post.getID())), document);
    }

    public void deletePost(Post post) {
        postCollection.deleteOne(new Document("id_", post.getID()));

        Document document = new Document();

        document.put("title", post.getTitle());
        document.put("author", post.getAuthor());
        document.put("authorId", post.getAuthorID());
        document.put("lastupdate", post.getLastUpdate() == null? null : post.getLastUpdate().toString());
        document.put("created", post.getCreated().toString());
        document.put("category", post.getCategory());
        document.put("html", post.getHtml());
        document.put("confirmed", "false");
        document.put("clicks", post.getClickCounter());

        deletedPostCollection.insertOne(document);
    }

    public void insertCategory(String category) {
        ArrayList<String> categories = getCategories();
        categories.add(category);
        categoryCollection.deleteOne(new Document("_id", Objects.requireNonNull(categoryCollection.find().first()).get("_id")));
        Document document = new Document("0", categories.get(0));
        int count = 0;
        for(String categoryS: categories) {
            if(count == 0) {
                count++;
                continue;
            }
            document.append(String.valueOf(count), categoryS);
            count++;
        }
        categoryCollection.insertOne(document);
    }

    private static Database instance;
    public static Database getInstance(){
        if(instance == null) instance = new Database();
        return instance;
    }
}