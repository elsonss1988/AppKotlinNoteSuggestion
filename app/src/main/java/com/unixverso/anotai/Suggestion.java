package com.unixverso.anotai;

import java.io.Serializable;
import java.util.Objects;

public class Suggestion implements Serializable {

    private long id;
    private String name;
    private String Category;
    private EnumPriority priority;
    private boolean isFriendSuggestion;
    private boolean isUrgent;
    private boolean isSerie;
    private String Obs;

    public Suggestion() {
    }

    public Suggestion (
            Long id,
            String name,
            String category,
            EnumPriority priority,
            boolean isFriendSuggestion,
            boolean isUrgent,
            boolean isSerie,
            String Obs) {
        this.id = id;
        this.name = name;
        Category = category;
        this.priority = priority;
        this.isFriendSuggestion = isFriendSuggestion;
        this.isUrgent = isUrgent;
        this.isSerie = isSerie;
        this.Obs =Obs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public EnumPriority getPriority() {
        return priority;
    }

    public void setPriority(EnumPriority priority) {
        this.priority = priority;
    }

    public boolean isFriendSuggestion() {
        return isFriendSuggestion;
    }

    public void setFriendSuggestion(boolean friendSuggestion) {
        isFriendSuggestion = friendSuggestion;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public void setUrgent(boolean urgent) {
        isUrgent = urgent;
    }

    public boolean isSerie() {
        return isSerie;
    }

    public void setSerie(boolean serie) {
        isSerie = serie;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getObs() {
        return Obs;
    }

    public void setObs(String obs) {
        Obs = obs;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Suggestion that = (Suggestion) o;
        return id == that.id && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return " " + name + '\n' +
                " Category=" + Category +"\n"+
                " priority=" + priority +"\n"+
                " isFriendSuggestion=" + isFriendSuggestion +"\n"+
                " isUrgent=" + isUrgent +"\n"+
                " isSerie=" + isSerie+"\n" ;
    }
}
