package app.passwd.service;

import app.passwd.model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;


@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserLoginService {
    private boolean isLoggedin = Boolean.FALSE;
    private User user;

    public void setUserLoggedin(boolean isLoggedin, User user) {
        this.isLoggedin = isLoggedin;
        this.user = user;
    }

    public void setLoggedin(boolean loggedin) {
        isLoggedin = loggedin;
    }

    public boolean isLoggedin() {
        return isLoggedin;
    }

    public User getUser() {
        return user;
    }
}
