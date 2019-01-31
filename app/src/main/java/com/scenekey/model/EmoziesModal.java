package com.scenekey.model;

import java.util.List;

public class EmoziesModal {
    /**
     * character : 😚
     * aliases : []
     * groups : ["face"]
     * name : kissing closed eyes
     */

    private String character="";
    private String name="";
    private List<String> aliases;
    private List<String> groups;

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }




}
