package com.tcg.rpgengine.editor.context;

import com.badlogic.gdx.files.FileHandle;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AppData {

    private static final String RECENT_PROJECTS_JSON_FIELD = "recent_projects";
    private static final String RECENT_PROJECT_TITLE_JSON_FIELD = "title";
    private static final String RECENT_PROJECT_PATH_JSON_FIELD = "path";

    public List<Pair<String, String>> getRecentlyOpenProjects() {
        final List<Pair<String, String>> recentlyOpenProjects = new ArrayList<>();

        final FileHandle appDataFile = this.getAppDataFile();
        if (appDataFile.exists()) {
            JSONObject appData = new JSONObject(appDataFile.readString());
            final JSONArray recentProjects;
            if (appData.has(RECENT_PROJECTS_JSON_FIELD)) {
                recentProjects = appData.getJSONArray(RECENT_PROJECTS_JSON_FIELD);
            } else {
                recentProjects = new JSONArray();
            }
            for (int i = 0; i < recentProjects.length(); i++) {
                final JSONObject recentProject = recentProjects.getJSONObject(i);
                final String title = recentProject.getString(RECENT_PROJECT_TITLE_JSON_FIELD);
                final String path = recentProject.getString(RECENT_PROJECT_PATH_JSON_FIELD);
                final Pair<String, String> recentOpenProjectPair = new Pair<>(title, path);
                if (!recentlyOpenProjects.contains(recentOpenProjectPair)) {
                    recentlyOpenProjects.add(recentOpenProjectPair);
                }
            }
        }

        return new ArrayList<>(recentlyOpenProjects);
    }

    public void addOpenProject(String title, String path) {

        final List<Pair<String, String>> recentlyOpenProjects = this.getRecentlyOpenProjects();
        final Pair<String, String> recentOpenProject = new Pair<>(title, path);
        recentlyOpenProjects.remove(recentOpenProject);
        recentlyOpenProjects.add(0, recentOpenProject);

        final FileHandle appDataFile = this.getAppDataFile();
        final JSONObject appData = appDataFile.exists() ? new JSONObject(appDataFile.readString()) : new JSONObject();
        final JSONArray recentOpenProjectArray = new JSONArray();
        recentlyOpenProjects.stream()
                .map(this::recentOpenProjectPairToJSONObject)
                .forEach(recentOpenProjectArray::put);
        appData.put(RECENT_PROJECTS_JSON_FIELD, recentOpenProjectArray);
        appDataFile.writeString(appData.toString(), false);

    }

    private JSONObject recentOpenProjectPairToJSONObject(Pair<String, String> recentOpenObjectPair) {
        final JSONObject recentProject = new JSONObject();
        recentProject.put(RECENT_PROJECT_TITLE_JSON_FIELD, recentOpenObjectPair.getKey());
        recentProject.put(RECENT_PROJECT_PATH_JSON_FIELD, recentOpenObjectPair.getValue());
        return recentProject;
    }

    private FileHandle getAppDataFile() {
        return ApplicationContext.context().files.external(".tcg/rpgengine/appdata.json");
    }

}
