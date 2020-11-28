package com.github.warriorzz.blog.db;

import com.github.warriorzz.blog.util.GFG;
import com.github.warriorzz.blog.util.UserData;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

import java.util.HashMap;
import java.util.Objects;

public class DataBase {

    private MongoCollection<HashMap> collection;

    private DataBase() {
        Dotenv dotenv = Dotenv.load();
        MongoClient client = MongoClients.create("mongodb+srv://"
                + dotenv.get("MONGO_USERNAME")
                + ":" + dotenv.get("MONGO_PASSWORD")
                + "@" + dotenv.get("MONGO_HOST")
                + "/" + dotenv.get("MONGO_DATABASE")
                + "?retryWrites=true&w=majority");
        MongoDatabase database = client.getDatabase(dotenv.get("MONGO_DATABASE"));
        collection = database.getCollection("UserData", HashMap.class);
    }

    public UserData getUser(UserData.UserLogin login) {
        HashMap<String, String> loginDataBase = collection.find(new Document("username", GFG.sha512(login.getUsername()))).first();
        if(loginDataBase == null) return null;
        if(Objects.requireNonNull(loginDataBase).get("password").equals(GFG.sha512(login.getPassword())))
            return new UserData(true, UserData.Role.fromString(String.valueOf(loginDataBase.get("role"))));
        return null;
    }

    public void insertData(UserData.UserLogin login) {
        HashMap<String, String> loginHashMap = new HashMap<>();
        loginHashMap.put("password", GFG.sha512(login.getPassword()));
        loginHashMap.put("username", GFG.sha512(login.getPassword()));
        if(login.getRole() != null) loginHashMap.put("role", login.getRole().name());
        collection.insertOne(loginHashMap);
    }

    private static DataBase instance;
    public static DataBase getInstance(){
        if(instance == null) instance = new DataBase();
        return instance;
    }
}