package de.lmu.cis.ocrd.ml.features;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class FeatureClassFilter {
    private final List<String>  classes;

    public FeatureClassFilter() {
        classes = new ArrayList<>();
    }

    public FeatureClassFilter(List<String> classes) {
        this.classes = classes;
    }

    public boolean filter(JsonObject o) {
        if (!o.has("class")) { // missing class; do not filter it
            return false;
        }
        final String objectClass = o.get("class").getAsString().toLowerCase();
        for (String clazz : classes) {
            if (objectClass.startsWith(clazz.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
