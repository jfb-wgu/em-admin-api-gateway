package edu.wgu.dm.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.Objects;

public class Tag {

    private Long tagId;
    private String name;
    private String description;
    private Long roleId;
    private Boolean active = Boolean.TRUE;

    @JsonIgnore
    protected Date dateCreated;

    @JsonIgnore
    protected Date dateUpdated;

    @JsonIgnore
    protected String createdBy;

    @JsonIgnore
    protected String lastModifiedBy;

    @Override
    public String toString() {
        return "Tag[" +
                   "tagId=" + tagId +
                   ", name='" + name + '\'' +
                   ", description='" + description + '\'' +
                   ", roleId=" + roleId +
                   ", active=" + active +
                   ']';
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

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

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tag tag = (Tag) o;
        return tagId.equals(tag.tagId) && Objects.equals(name, tag.name) && Objects.equals(description,
                                                                                           tag.description)
                   && roleId.equals(tag.roleId) && active.equals(tag.active) && Objects.equals(dateCreated,
                                                                                               tag.dateCreated)
                   && Objects.equals(dateUpdated, tag.dateUpdated) && Objects.equals(createdBy,
                                                                                     tag.createdBy)
                   && Objects.equals(lastModifiedBy, tag.lastModifiedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId, name, description, roleId, active, dateCreated, dateUpdated, createdBy,
                            lastModifiedBy);
    }
}
