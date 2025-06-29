package com.example.guardify;

import java.util.List;

public class User {
    public String uid;
    public String name;
    public String bio;
    public List<String> skills;
    public String profileImageUrl; // NEW

    public User() {}

    public User(String uid, String name, String bio, List<String> skills, String profileImageUrl) {
        this.uid = uid;
        this.name = name;
        this.bio = bio;
        this.skills = skills;
        this.profileImageUrl = profileImageUrl;
    }
}



