package com.github.warriorzz.blog.util;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;

public class Config {

    private static final Dotenv dotenv = new DotenvBuilder().ignoreIfMissing().load();

    public static String MONGO_USERNAME = dotenv.get("MONGO_USERNAME");
    public static String MONGO_PASSWORD = dotenv.get("MONGO_PASSWORD");
    public static String MONGO_HOST = dotenv.get("MONGO_HOST");
    public static String MONGO_DATABASE = dotenv.get("MONGO_DATABASE");
    public static String START_ARTICLE_NAME = dotenv.get("START_ARTICLE_NAME");
    public static String IMPRESSUM_NAME = dotenv.get("IMPRESSUM_NAME");

    private Config() {}
}
