package org.hali.common.model;

public record BuildStatus(String state, String context, String description) {
    public static BuildStatus success(String context, String description) {
        return new BuildStatus("success", context, description);
    }

    public static BuildStatus failure(String context, String description) {
        return new BuildStatus("failure", context, description);
    }

    public static BuildStatus pending(String context, String description) {
        return new BuildStatus("pending", context, description);
    }
}

