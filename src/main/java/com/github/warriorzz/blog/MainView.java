package com.github.warriorzz.blog;

import com.github.warriorzz.blog.db.Database;
import com.github.warriorzz.blog.util.ArticleClick;
import com.github.warriorzz.blog.util.Config;
import com.github.warriorzz.blog.util.Post;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The main view is a top-level placeholder for other views.
 */
@Route("")
@JsModule("./styles/shared-styles.js")
@CssImport("./styles/views/main/main-view.css")
@PWA(name = "sz-blog-vaadin", shortName = "Schiffsschraube", enableInstallPrompt = false)
public class MainView extends VerticalLayout implements HasDynamicTitle, BeforeEnterObserver, BeforeLeaveObserver {

    private boolean initialized = false;
    private Post currentPost = null;
    private final VerticalLayout contentGoesHere = new VerticalLayout();

    private Accordion accordion;
    private Tabs impressumEtc;

    private final HashMap<String, Tabs> tabsList = new HashMap<>();
    private final HashMap<Tabs, HashMap<Tab, Post>> layoutMap = new HashMap<>();

    private final HashMap<String, ArticleClick> clicks = new HashMap<>();

    public MainView() {

    }

    private void refresh() {
        setCurrentPostToStartArticle();

        layoutMap.keySet().forEach(it -> accordion.remove(it));
        tabsList.clear();
        layoutMap.clear();

        for (String category : Database.getInstance().getCategories()) addCategory(category);

        if (Database.getInstance().getPosts() == null) return;
        List<Post> posts = Database.getInstance().getPosts().stream().sorted().collect(Collectors.toList());
        for (Post post : posts) {
            if (!post.isConfirmed()) continue;
            for (String tabs : tabsList.keySet()) {
                if (tabs.equals(post.getCategory())) {
                    Tab tab = new Tab(post.getTitle());
                    layoutMap.get(tabsList.get(post.getCategory())).put(tab, post);
                    tabsList.get(post.getCategory()).add(tab);
                }
            }
        }
        layoutMap.put(tabsList.get("Blog"), new HashMap<>());
        tabsList.get("Blog").removeAll();
        for (Post post : posts) {
            if (!post.isConfirmed()) continue;
            if (post.getTitle().equals(Config.START_ARTICLE_NAME) || post.getTitle().equals(Config.IMPRESSUM_NAME))
                continue;
            Tab tabForBlog = new Tab(post.getTitle());
            layoutMap.get(tabsList.get("Blog")).put(tabForBlog, post);
            tabsList.get("Blog").add(tabForBlog);
        }
        for (Tabs tabs : tabsList.values()) {
            tabs.addSelectedChangeListener(event -> {
                if (event.getSelectedTab() == null) return;
                if (layoutMap.get(tabs).containsKey(event.getSelectedTab()))
                    currentPost = layoutMap.get(tabs).get(event.getSelectedTab());
                refreshPost();
                if (clicks.get(UI.getCurrent().getSession().getPushId()) != null) {
                    if (clicks.get(UI.getCurrent().getSession().getPushId()).checkTimeStamp(30 * 1000)) {
                        clicks.get(UI.getCurrent().getSession().getPushId()).addClick();
                    }
                }
                clicks.put(UI.getCurrent().getSession().getPushId(), new ArticleClick(currentPost));
                for (Tabs tabs2 : tabsList.values()) {
                    if (tabs != tabs2) {
                        tabs2.setSelectedTab(null);
                    }
                }
                impressumEtc.setSelectedTab(null);
            });
        }
    }

    private void initialize() throws IOException {
        setId("layout");

        HorizontalLayout mainlayout = new HorizontalLayout();
        mainlayout.setId("no-margin-padding");

        byte[] imageBytesHeading = Objects.requireNonNull(this.getClass().getClassLoader().getResource("sz_schrift.jpeg")).openStream().readAllBytes();
        StreamResource resourceHeading = new StreamResource("LOGO.png", () -> new ByteArrayInputStream(imageBytesHeading));
        Image imageHeading = new Image(resourceHeading, "Schriftzug");
        imageHeading.setId("heading-name");

        byte[] imageBytes = Objects.requireNonNull(this.getClass().getClassLoader().getResource("LOGO.png")).openStream().readAllBytes();
        StreamResource resource = new StreamResource("LOGO.png", () -> new ByteArrayInputStream(imageBytes));
        Image image = new Image(resource, "logo");
        image.setId("logo");

        // Content
        HorizontalLayout content = new HorizontalLayout();

        VerticalLayout accordionLayout = new VerticalLayout();
        accordionLayout.setId("sidebar-layout");

        accordion = new Accordion();
        accordion.setId("accordion");
        accordionLayout.add(accordion);

        accordion.close();

        impressumEtc = new Tabs();
        accordionLayout.add(impressumEtc);
        impressumEtc.setOrientation(Tabs.Orientation.VERTICAL);

        Tab startArticle = new Tab("Unser Blog!");
        impressumEtc.add(startArticle);

        Tab impressum = new Tab("Impressum");
        impressumEtc.add(impressum);

        Tab intern = new Tab("Intern");
        impressumEtc.add(intern);

        impressumEtc.addSelectedChangeListener((ComponentEventListener<Tabs.SelectedChangeEvent>) event -> {
            if (event.getSelectedTab() == null)
                return;
            if (event.getSelectedTab().equals(startArticle)) {
                setCurrentPostToStartArticle();
            }
            if (event.getSelectedTab().equals(impressum)) {
                setCurrentPostToImpressum();
            }
            if (event.getSelectedTab().equals(intern)) UI.getCurrent().navigate("intern");
            for (Tabs tabs2 : tabsList.values()) {
                if (impressumEtc != tabs2) {
                    tabs2.setSelectedTab(null);
                }
            }
        });
        impressumEtc.setSelectedTab(startArticle);

        content.add(accordionLayout);
        content.add(contentGoesHere);

        VerticalLayout layoutContent2 = new VerticalLayout();
        layoutContent2.add(imageHeading);
        layoutContent2.add(content);
        layoutContent2.setId("layout-content");
        mainlayout.add(layoutContent2);
        mainlayout.add(image);
        add(mainlayout);
    }

    private void refreshPost() {
        if (currentPost == null)
            return;
        contentGoesHere.removeAll();

        H2 heading = new H2(currentPost.getTitle());
        heading.setId("post-heading");
        Span text = new Span("von " + currentPost.getAuthor());
        text.setId("post-author");
        HorizontalLayout titleAndAuthor = new HorizontalLayout();
        titleAndAuthor.add(heading);
        if (!currentPost.getAuthor().equals("")) titleAndAuthor.add(text);

        Paragraph times = new Paragraph(currentPost.getCreated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")) +
                " Uhr" + ((currentPost.getLastUpdate() != null) ? (", zuletzt bearbeitet: " + currentPost.getLastUpdate()) : ""));
        times.setId("post-times");
        contentGoesHere.add(titleAndAuthor);
        contentGoesHere.add(currentPost.getLayout());
        if (currentPost.getCreated() != null) contentGoesHere.add(times);
    }

    private void addCategory(String name) {
        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.setAutoselect(false);
        accordion.add(name, tabs);
        tabsList.put(name, tabs);
        layoutMap.put(tabs, new HashMap<>());
    }

    private void setCurrentPostToStartArticle() {
        if (Database.getInstance().getPosts().stream().filter(post -> post.getCategory().equals("")).anyMatch(post -> post.getTitle().equals(Config.START_ARTICLE_NAME)))
            currentPost = Database.getInstance().getPosts().stream().filter(post -> post.getCategory().equals("")).filter(post -> post.getTitle().equals(Config.START_ARTICLE_NAME)).findFirst().get();
        refreshPost();
    }

    private void setCurrentPostToImpressum() {
        if (Database.getInstance().getPosts().stream().filter(post -> post.getCategory().equals("")).anyMatch(post -> post.getTitle().equals(Config.IMPRESSUM_NAME)))
            currentPost = Database.getInstance().getPosts().stream().filter(post -> post.getCategory().equals("")).filter(post -> post.getTitle().equals(Config.IMPRESSUM_NAME)).findFirst().get();
        refreshPost();
    }

    @Override
    public String getPageTitle() {
        return "schiffsschraube - " + currentPost.getTitle();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (!initialized) {
            try {
                initialize();
            } catch (IOException e) {
                e.printStackTrace();
            }
            initialized = true;
        }
        refresh();
        for(String pushId: clicks.keySet()) {
            if(clicks.get(pushId).checkTimeStamp(30 * 1000)) {
                clicks.get(pushId).addClick();
            }
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        if (clicks.get(UI.getCurrent().getSession().getPushId()) != null) {
            if (clicks.get(UI.getCurrent().getSession().getPushId()).checkTimeStamp(30 * 1000)) {
                clicks.get(UI.getCurrent().getSession().getPushId()).addClick();
            }
        }

    }
}
