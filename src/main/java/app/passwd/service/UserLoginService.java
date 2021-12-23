package app.passwd.service;

import app.passwd.model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;


@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserLoginService {
    private boolean isLogin = Boolean.FALSE;
    private User user;

    public void setUserLogin(boolean isLogin, User user) {
        this.isLogin = isLogin;
        this.user = user;
    }

    public void setLoggedin(boolean loggedin) {
        isLogin = loggedin;
    }

    public boolean isLoggedin() {
        return isLogin;
    }

    public User getUser() {
        return user;
    }

    public void setUserLogout() {
        this.isLogin = false;
        this.user = null;
    }
}
