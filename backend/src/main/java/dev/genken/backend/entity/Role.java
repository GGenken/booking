package dev.genken.backend.entity;

public enum Role {
    USER("user"),
    ADMIN("admin"),
    GUEST("guest");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    @Override
    public String toString() {
        return authority;
    }
}
