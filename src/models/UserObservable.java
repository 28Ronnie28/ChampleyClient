package models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class UserObservable {

    private User user;
    public volatile BooleanProperty update;

    public UserObservable(User user) {
        this.user = user;
        update = new SimpleBooleanProperty(false);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void update() {
        update.setValue(false);
        update.setValue(true);
    }
}
