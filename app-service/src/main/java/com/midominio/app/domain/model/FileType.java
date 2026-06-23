package com.midominio.app.domain.model;

public enum FileType {
    IMAGE, VIDEO, AUDIO, DOCUMENT;

    public static FileType fromMimeType(String mimeType) {
        if (mimeType == null) return DOCUMENT;
        if (mimeType.startsWith("image/")) return IMAGE;
        if (mimeType.startsWith("video/")) return VIDEO;
        if (mimeType.startsWith("audio/")) return AUDIO;
        return DOCUMENT;
    }
}
