package com.tcg.rpgengine.common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

import static org.junit.Assert.*;


public final class TestUtils {

    public static void assertJSONObjectsEquals(JSONObject expected, JSONObject actual) {
        final String[] expectedNames = namesArrayToStringArray(expected.names());
        final String[] actualNames = namesArrayToStringArray(actual.names());
        assertArrayEquals("JSONObjects must have the same fields", expectedNames, actualNames);
        for (String name : expectedNames) {
            final Object expectedValue = expected.get(name);
            final Object actualValue = actual.get(name);
            assertJSONValuesEqual(String.format("Elements at \"%s\" must match", name), expectedValue, actualValue);
        }
    }

    public static void assertJSONArraysEquals(JSONArray expected, JSONArray actual) {
        assertEquals(expected.length(), actual.length());
        for (int i = 0; i < expected.length(); i++) {
            final Object expectedValue = expected.get(i);
            final Object actualValue = actual.get(i);
            assertJSONValuesEqual(String.format("Elements at %d must match.", i), expectedValue, actualValue);
        }
    }

    private static void assertJSONValuesEqual(String message, Object expectedValue, Object actualValue) {
        if (expectedValue instanceof JSONObject && actualValue instanceof JSONObject) {
            assertJSONObjectsEquals((JSONObject) expectedValue, (JSONObject) actualValue);
        } else if (expectedValue instanceof JSONArray && actualValue instanceof JSONArray) {
            assertJSONArraysEquals((JSONArray) expectedValue, (JSONArray) actualValue);
        } else {
            assertEquals(message, expectedValue, actualValue);
        }
    }

    private static String[] namesArrayToStringArray(JSONArray names) {
        final String[] namesArray = new String[names.length()];
        for (int i = 0; i < names.length(); i++) {
            namesArray[i] = names.getString(i);
        }
        Arrays.sort(namesArray);
        return namesArray;
    }

}
