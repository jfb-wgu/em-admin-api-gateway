package edu.wgu.dm.dto.security;

import java.util.List;

public class BulkUsers {

    private List<String> usernames;
    private List<Long> roles;
    private List<Long> tasks;

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public List<Long> getRoles() {
        return roles;
    }

    public void setRoles(List<Long> roles) {
        this.roles = roles;
    }

    public List<Long> getTasks() {
        return tasks;
    }

    public void setTasks(List<Long> tasks) {
        this.tasks = tasks;
    }
}
