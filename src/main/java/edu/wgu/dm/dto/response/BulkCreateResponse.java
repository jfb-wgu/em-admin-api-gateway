package edu.wgu.dm.dto.response;

import edu.wgu.dm.dto.security.User;
import java.util.List;

public class BulkCreateResponse {

    private List<User> users;
    private List<String> failed;

    public BulkCreateResponse(List<User> users, List<String> failed) {
        this.users = users;
        this.failed = failed;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<String> getFailed() {
        return failed;
    }

    public void setFailed(List<String> failed) {
        this.failed = failed;
    }
}
