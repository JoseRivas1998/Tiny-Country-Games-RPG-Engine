package com.tcg.rpgengine.common.data;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Objects;

public class Project implements JSONDocument {

    private static final String TITLE_JSON_FIELD = "title";
    private static final String CREATION_DATE_JSON_FIELD = "creation_date";
    public final String title;
    public final LocalDateTime creationDate;

    private Project(String title, LocalDateTime creationDate) {
        this.title = title;
        this.creationDate = creationDate;
    }

    public static Project generateNewProject(String title) {
        return new Project(title, LocalDateTime.now());
    }

    public static Project generateFromJSON(String jsonString) {
        final JSONObject jsonObject = new JSONObject(jsonString);
        final String title = jsonObject.getString(TITLE_JSON_FIELD);
        final LocalDateTime creationDate = LocalDateTime.parse(jsonObject.getString(CREATION_DATE_JSON_FIELD));
        return new Project(title, creationDate);
    }

    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(TITLE_JSON_FIELD, this.title);
        jsonObject.put(CREATION_DATE_JSON_FIELD, this.creationDate.toString());
        return jsonObject;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.title, this.creationDate);
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        if (!result) {
            if (obj == null || obj.getClass() != this.getClass()) {
                result = false;
            } else {
                final Project other = (Project) obj;
                result = this.title.equals(other.title) && this.creationDate.equals(other.creationDate);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return this.jsonString();
    }
}
