package edu.wgu.dm.tag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
}
