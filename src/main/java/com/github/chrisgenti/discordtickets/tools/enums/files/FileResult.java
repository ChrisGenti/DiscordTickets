package com.github.chrisgenti.discordtickets.tools.enums.files;

public enum FileResult {
    EXISTING("loaded from existing file"),
    CREATED("missing file, created a new one"),
    MALFORMED("");

    private final String reason;
    FileResult(String reason) {
        this.reason = reason;
    }

    public String reason() {
        return reason;
    }
}
