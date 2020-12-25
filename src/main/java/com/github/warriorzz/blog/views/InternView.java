package com.github.warriorzz.blog.views;

import com.github.warriorzz.blog.db.DataBase;
import com.github.warriorzz.blog.util.Post;
import com.github.warriorzz.blog.util.PostBuilder;
import com.github.warriorzz.blog.util.UserData;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

@Route("intern")
@PageTitle("schiffsschraube - Intern")
public class InternView extends VerticalLayout implements BeforeEnterObserver {

    private boolean initialized = false;
    private VerticalLayout confirmLayout;


    private void refresh() {
        confirmLayout.removeAll();
        if(DataBase.getInstance().getPosts() == null || DataBase.getInstance().getPosts().stream().filter(post -> !post.isConfirmed()).toArray().length == 0)
            confirmLayout.add(new Span("Nothing in here. :)"));
        else {
            Accordion accordion = new Accordion();
            DataBase.getInstance().getPosts().stream().filter(post -> !post.isConfirmed()).forEach(post -> addPostToConfirmLayout(post, accordion));
            confirmLayout.add(accordion);
            accordion.close();
        }
        confirmLayout.setVisible(false);
    }

    private void initialize() throws IOException {
        setId("layout");

        HorizontalLayout headBar = new HorizontalLayout();
        headBar.setId("headbar");

        H1 heading = new H1("schiffsschraube - Intern");
        heading.setId("heading-name");
        headBar.add(heading);

        byte[] imageBytes = Objects.requireNonNull(this.getClass().getClassLoader().getResource("Logo_rot.png")).openStream().readAllBytes();
        StreamResource resource = new StreamResource("Logo_rot.jpg", () -> new ByteArrayInputStream(imageBytes));
        Image image = new Image(resource, "logo");
        image.setId("logo");
        headBar.add(image);

        // Content
        HorizontalLayout content = new HorizontalLayout();

        VerticalLayout tabsLayout = new VerticalLayout();
        content.add(tabsLayout);
        tabsLayout.setId("sidebar-layout");

        Tabs tabs = new Tabs();
        tabsLayout.add(tabs);
        tabs.setOrientation(Tabs.Orientation.VERTICAL);

        Tab confirmTab = new Tab("Confirm");

        VerticalLayout contentGoesHere = new VerticalLayout();

        if(UI.getCurrent().getSession().getAttribute(UserData.class).getRole() == UserData.Role.ADMIN){
            tabs.add(confirmTab);
        }

        Tab insertTab = new Tab("Insert");
        tabs.add(insertTab);
        tabs.setSelectedTab(insertTab);

        VerticalLayout insertLayout = new VerticalLayout();
        confirmLayout = new VerticalLayout();

        Tab adminTab = new Tab("Admin-Bereich");
        if(UI.getCurrent().getSession().getAttribute(UserData.class).getRole() == UserData.Role.ADMIN){
            tabs.add(adminTab);
        }

        VerticalLayout adminLayout = new VerticalLayout();
        confirmLayout.setWidth("50vw");

        Tab leaveTab = new Tab("Leave");
        tabs.add(leaveTab);

        tabs.addSelectedChangeListener(event -> {
            if(event.getSelectedTab().equals(insertTab)) {
                confirmLayout.setVisible(false);
                insertLayout.setVisible(true);
                adminLayout.setVisible(false);
            }
            if(event.getSelectedTab().equals(confirmTab)) {
                confirmLayout.setVisible(true);
                insertLayout.setVisible(false);
                adminLayout.setVisible(false);
            }
            if(event.getSelectedTab().equals(adminTab)){
                confirmLayout.setVisible(false);
                insertLayout.setVisible(false);
                adminLayout.setVisible(true);
            }
            if(event.getSelectedTab() .equals(leaveTab)) {
                UI.getCurrent().navigate("");
            }
        });

        //CONFIRM
        // -> refresh

        //INSERT
        TextField authorField = new TextField();
        authorField.setPlaceholder("Autor");

        TextField titleField = new TextField();
        titleField.setPlaceholder("Titel");

        DateTimePicker createdPicker = new DateTimePicker();
        createdPicker.setLocale(Locale.GERMANY);

        ListDataProvider<String> providerCategory = DataProvider.fromStream(DataBase.getInstance().getCategories().stream());
        ComboBox<String> checkboxCategory = new ComboBox<>();
        checkboxCategory.setDataProvider(providerCategory);
        checkboxCategory.setPlaceholder("Kategorie");

        HorizontalLayout infoLayout = new HorizontalLayout();
        infoLayout.add(titleField, authorField,  /*createdPicker, */checkboxCategory);

        RichTextEditor editor = new RichTextEditor();
        editor.setWidth("50vw");
        editor.setI18n(getEditorI18n());

        VerticalLayout previewLayout = new VerticalLayout();

        Button previewButton = new Button("Preview");
        previewButton.addClickListener(event -> {
            previewLayout.removeAll();
            for(String line: editor.getHtmlValue().split("\n")) {
                previewLayout.add(new Html(line));
            }
        });

        Button uploadButton = new Button("Upload");
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setDraggable(false);
        VerticalLayout dialogLayout = new VerticalLayout();

        Button dialogUpload = new Button("Ja, hochladen.");
        dialogUpload.addClickListener(event -> {
            if(/*createdPicker.isEmpty() || */titleField.isEmpty() || checkboxCategory.isEmpty()){
                dialog.close();
                Notification.show("Bitte trage oben alles ein!");
                return;
            }

            createFileFromEditor(editor.getHtmlValue(), authorField.isEmpty() ? "" : authorField.getValue(),
                    titleField.getValue(),
                    LocalDateTime.now(),
                    null, checkboxCategory.getValue());
            dialog.close();
            editor.clear();
            authorField.clear();
            titleField.clear();
            checkboxCategory.clear();
            createdPicker.clear();
        });

        Button dialogStop = new Button("Nein, Vorgang abbrechen.");
        dialogStop.addClickListener(event -> dialog.close());

        dialogLayout.add(new H3("Bist du dir sicher?"), dialogUpload, dialogStop);
        dialog.add(dialogLayout);

        uploadButton.addClickListener(event -> dialog.setOpened(true));

        insertLayout.add(infoLayout);
        insertLayout.add(editor);
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(previewButton, uploadButton);
        insertLayout.add(buttonLayout);
        insertLayout.add(previewLayout);

        //ADMIN TODO
        adminLayout.setWidth("50vw");

        if(UI.getCurrent().getSession().getAttribute(UserData.class).getRole() == UserData.Role.ADMIN)
            contentGoesHere.add(confirmLayout, insertLayout, adminLayout);
        else if(UI.getCurrent().getSession().getAttribute(UserData.class).getRole() == UserData.Role.USER)
            contentGoesHere.add(insertLayout);
        content.add(contentGoesHere);
        add(headBar);
        add(content);
    }

    private void createFileFromEditor(String htmlContent, String author, String title, LocalDateTime created, LocalDateTime lastUpdate, String category){
        PostBuilder builder = new PostBuilder();
        builder.author(author);
        builder.title(title);
        builder.created(created);
        builder.lastUpdate(lastUpdate);
        builder.category(category);

        DataBase.getInstance().insertPost(builder.build(), htmlContent);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(UI.getCurrent().getSession().getAttribute(UserData.class) == null) {
            UI.getCurrent().navigate("login");
            return;
        }
        if(!UI.getCurrent().getSession().getAttribute(UserData.class).isLoggedIn()) UI.getCurrent().navigate("login");

        if(!initialized) {
            try {
                this.removeAll();
                initialize();
                initialized = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        refresh();
    }

    private void addPostToConfirmLayout(Post post, Accordion accordion) {
        VerticalLayout postLayout = new VerticalLayout();

        H2 heading = new H2(post.getTitle());
        heading.setId("post-heading");
        Span text = new Span("von " + post.getAuthor());
        text.setId("post-author");
        HorizontalLayout titleAndAuthor = new HorizontalLayout();
        titleAndAuthor.add(heading);
        if(post.getAuthor() != null) titleAndAuthor.add(text);

        Paragraph times = new Paragraph(post.getCreated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")) + " Uhr" + ((post.getLastUpdate() != null) ? (", zuletzt bearbeitet: " + post.getLastUpdate()) : ""));
        times.setId("post-times");
        postLayout.add(titleAndAuthor);
        postLayout.add(post.getLayout());
        if(post.getCreated() != null) postLayout.add(times);

        Button acceptButton = new Button("Accept");
        acceptButton.addClickListener(event -> {
            Dialog dialog = new Dialog();
            dialog.setCloseOnEsc(false);
            dialog.setDraggable(false);
            VerticalLayout dialogLayout = new VerticalLayout();
            dialogLayout.add("Sind Sie sich sicher?");

            Button confirm = new Button("Ja, der Post soll erscheinen.");
            confirm.addClickListener(clickEvent -> {
                DataBase.getInstance().updatePost(post.setConfirmed(true), true, false);
                accordion.remove(postLayout);
                dialog.close();
            });
            dialogLayout.add(confirm);

            Button decline = new Button("Nein, soll er nicht.");
            decline.addClickListener(clickEvent -> dialog.close());
            dialogLayout.add(decline);

            dialog.add(dialogLayout);
            dialog.open();
        });

        Button declineButton = new Button("Decline");
        declineButton.addClickListener(event -> {
            Dialog dialog = new Dialog();
            dialog.setCloseOnEsc(false);
            dialog.setDraggable(false);

            VerticalLayout dialogLayout = new VerticalLayout();
            dialogLayout.add("Sind Sie sich sicher?");

            Button confirm = new Button("Ja, der Post soll gelöscht werden.");
            confirm.addClickListener(clickEvent -> {

            });
            dialogLayout.add(confirm);

            Button decline1 = new Button("Nein, soll er nicht.");
            decline1.addClickListener(clickEvent -> dialog.close());
            dialogLayout.add(decline1);

            dialog.add(dialogLayout);
            dialog.open();
        });

        Button editButton = new Button("Edit");

        RichTextEditor editEditor = new RichTextEditor();
        editEditor.setI18n(getEditorI18n());
        editEditor.asHtml().setValue(post.getHtml());
        editEditor.setVisible(false);
        postLayout.add(editEditor);

        editButton.addClickListener(event -> {
            if(editEditor.isVisible()) {
                DataBase.getInstance().updatePost(post.setHtml(editEditor.getHtmlValue()), false, false);
                VerticalLayout newLayout = new VerticalLayout();
                for(String html: editEditor.getHtmlValue().split("\r\n")) newLayout.add(new Html(html));
                postLayout.replace(post.getLayout(), newLayout);
                post.setLayout(newLayout);
                editEditor.setVisible(false);
            } else {
                editEditor.setVisible(true);
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(acceptButton, declineButton, editButton);

        postLayout.add(buttonLayout);
        accordion.add(post.getTitle() + " - von " + post.getAuthor(), postLayout);
    }

    private RichTextEditor.RichTextEditorI18n getEditorI18n(){
        return new RichTextEditor.RichTextEditorI18n().setClean("Reset").setAlignCenter("Mittig").setAlignLeft("Links").setAlignRight("Rechts")
                .setCodeBlock("Block").setBlockquote("Eingerückt").setBold("Fett")
                .setH1("Überschrift 1").setH2("Überschrift 2").setH3("Überschrift 3")
                .setImage("Bild einfügen").setLink("Link einfügen").setLink("Liste").setListBullet("List Punkt")
                .setListOrdered("Liste Zahlen").setRedo("Wiederherstellen").setUndo("Zurück").setUnderline("Unterstrichen")
                .setSuperscript("Hochgestellt").setStrike("Durchgestrichen").setSubscript("Tiefgestellt").setItalic("Schräg");
    }
}
