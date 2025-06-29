package com.example.guardify.ui;

import java.io.Serializable;
import java.util.List;

public class Team implements Serializable {
    private String id;
    private String name;
    private String description;
    private String createdBy;
    private long createdAt;
    private List<String> members;

    public Team() {} // Required for Firestore

    public Team(String name, String description, String createdBy, long createdAt, List<String> members) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.members = members;
    }

    // Getters and setters
    public void setId(String id) { this.id = id; }
    public String getId() { return id; }

    // other getters & setters...

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
