package com.ktc.media.media.video;

public class SubtitleEntity {

    public static final int CLOSE = 0;
    public static final int INNER = 1;
    public static final int EXTERNAL = 2;

    private int type;
    private String name;
    private String path;

    public SubtitleEntity() {

    }

    public SubtitleEntity(int type, String name, String path) {
        this.type = type;
        this.name = name;
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
