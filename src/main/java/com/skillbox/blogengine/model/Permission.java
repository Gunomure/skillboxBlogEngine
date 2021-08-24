package com.skillbox.blogengine.model;

public enum Permission {

    USER("user:write"),
    MODERATOR("user:moderate");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
