package com.github.warriorzz.blog.views;

import com.github.warriorzz.blog.db.DataBase;
import com.github.warriorzz.blog.util.UserData;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("Login")
public class LogIn extends VerticalLayout implements BeforeLeaveObserver {

    private final LoginOverlay overlay;

    public LogIn(){
        LoginI18n login = LoginI18n.createDefault();
        overlay = new LoginOverlay();

        LoginI18n.ErrorMessage errorMessage = new LoginI18n.ErrorMessage();
        errorMessage.setMessage("Username oder Passwort falsch!");
        errorMessage.setTitle("Fehler!");

        login.setErrorMessage(errorMessage);
        login.getForm().setForgotPassword("Passwort vergessen");
        login.getForm().setSubmit("Log In");
        overlay.addLoginListener(event -> passCredentials(event.getUsername(), event.getPassword()));
        overlay.addForgotPasswordListener(event -> forgotPassword());
        overlay.setTitle("schiffsschraube");
        overlay.setDescription("Logge dich ein, um Zugriff auf die Tools zu bekommen!");
        overlay.setI18n(login);
        overlay.setOpened(true);
    }

    public void passCredentials(String username, String password) {
        UserData data = DataBase.getInstance().getUser(new UserData.UserLogin(username, password));
        if (data == null){
            overlay.setError(true);
            return;
        }
        UI.getCurrent().getSession().setAttribute(UserData.class, data);
        UI.getCurrent().navigate("intern");
        overlay.close();
    }

    public void forgotPassword(){

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        overlay.close();
    }
}
