package edu.wgu.dm.entity.security;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "tag")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
public class TagEntity {

    @Id
    @Column(name = "tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @Column(name = "name", length = 60)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "active")
    private Boolean active = Boolean.TRUE;

    @Column(name = "role_id")
    private Long roleId;

    @CreatedDate
    @Column(name = "date_created", updatable = false)
    private Date dateCreated;

    @LastModifiedDate
    @Column(name = "date_updated")
    private Date dateUpdated;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Override
    public String toString() {
        return "TagEntity{" +
                   "tagId=" + tagId +
                   ", name='" + name + '\'' +
                   ", active=" + active +
                   '}';
    }
}
