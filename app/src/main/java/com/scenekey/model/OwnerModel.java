package com.scenekey.model;

import java.io.Serializable;

public class OwnerModel implements Serializable {
    public String DisplayName;
    public String ID;

    public OwnerModel(String displayName, String ID) {
        DisplayName = displayName;
        this.ID = ID;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
