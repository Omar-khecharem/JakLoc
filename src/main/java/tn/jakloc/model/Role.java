package tn.jakloc.model;

/**
 * Application roles. Keep this enum as the single source of truth for role
 * names so both modules (authentication and vehicles) use the same values.
 */
public enum Role {
    ADMIN,
    CLIENT
}
