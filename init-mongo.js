db.auth("root", "root");

db.createUser(
    {
        user: "databaseuserlol",
        pwd: "databaseuserlol",
        roles: [
            {
                role: "readWrite",
                db: "sz-database"
            }
        ]
    }
);

db.CategoryData.insert({
    News: "News",
    Blog: "Blog",
    "Gastbeiträge": "Gastbeiträge"
});

db.PostData.insert({
    "author": "",
    "created": "2020-12-21T15:02:42.000060700",
    "html": "<p>Das ist unser neuer Blog, bei Kritik, Fragen und Anmerkungen meldet euch unter schiffsschraube@whgw.de!</p>",
    "lastupdate": null,
    "title": "Unser Blog!",
    "category": "",
    "confirmed": "true"
});

db.PostData.insert({
    "author": "",
    "created": "2020-12-21T15:02:37.868532100",
    "html": "<p>--- TEXT ---</p>",
    "lastupdate": null,
    "title": "Impressum",
    "category": "",
    "confirmed": "true"
});

db.UserData.insert({
    "username": "c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec",
    "password": "d565a88da9e0473dcd31b58f9327bb3dfc2b12063568ccaac3a916cdbbc8b03bde77681647b625775b14251b207a6b895844d7f527d74e3b036a2232de45edc2",
    "role": "admin"
});

db.UserData.insert({
    "username": "b14361404c078ffd549c03db443c3fede2f3e534d73f78f77301ed97d4a436a9fd9db05ee8b325c0ad36438b43fec8510c204fc1c1edb21d0941c00e9e2c1ce2",
    "password": "b8982993e8bcc0a13929222c2a4bf0f39571f935548f16bf454e2e1bf498184a1b2f2a34a4915dfaf61affce558aab69aba2be8c6e3078136732ebe925e29635",
    "role": "user"
});