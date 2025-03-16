package com.example.oriedita_core.origami.folding.fold.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FoldFile {
    private final Map<String, Object> customPropertyMap = new HashMap();
    private double spec = 1.1;
    private String creator;
    private String author;
    private String title;
    private String description;
    private List<String> classes = new ArrayList();
    private FoldFrame rootFrame = new FoldFrame();
    private List<FoldFrame> frames = new ArrayList();

    public FoldFile() {
    }

    public Map<String, Object> getCustomPropertyMap() {
        return this.customPropertyMap;
    }

    public void setCustomProperty(String namespace, String key, Object value) {
        this.customPropertyMap.put(this.formatCustomProperty(namespace, key), value);
    }

    public Object getCustomProperty(String namespace, String key) {
        return this.customPropertyMap.getOrDefault(this.formatCustomProperty(namespace, key), (Object)null);
    }

    public void removeCustomProperty(String namespace, String key) {
        this.customPropertyMap.remove(this.formatCustomProperty(namespace, key));
    }

    private String formatCustomProperty(String namespace, String key) {
        return String.format("%s:%s", namespace, key);
    }

    public double getSpec() {
        return this.spec;
    }

    public void setSpec(double spec) {
        this.spec = spec;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getClasses() {
        return this.classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public FoldFrame getRootFrame() {
        return this.rootFrame;
    }

    public void setRootFrame(FoldFrame rootFrame) {
        this.rootFrame = rootFrame;
    }

    public List<FoldFrame> getFrames() {
        return this.frames;
    }

    public void setFrames(List<FoldFrame> frames) {
        this.frames = frames;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            FoldFile foldFile = (FoldFile)o;
            return Double.compare(foldFile.getSpec(), this.getSpec()) == 0 && Objects.equals(this.getCustomPropertyMap(), foldFile.getCustomPropertyMap()) && Objects.equals(this.getCreator(), foldFile.getCreator()) && Objects.equals(this.getAuthor(), foldFile.getAuthor()) && Objects.equals(this.getTitle(), foldFile.getTitle()) && Objects.equals(this.getDescription(), foldFile.getDescription()) && Objects.equals(this.getClasses(), foldFile.getClasses()) && Objects.equals(this.getFrames(), foldFile.getFrames());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getCustomPropertyMap(), this.getSpec(), this.getCreator(), this.getAuthor(), this.getTitle(), this.getDescription(), this.getClasses(), this.getFrames()});
    }
}
