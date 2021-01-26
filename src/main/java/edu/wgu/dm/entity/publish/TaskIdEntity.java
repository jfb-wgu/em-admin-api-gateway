package edu.wgu.dm.entity.publish;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Table(name = "task")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class TaskIdEntity implements Serializable {

    private static final long serialVersionUID = 6546742962854317714L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id", updatable = false, insertable = false)
    @Access(AccessType.PROPERTY)
    private Long taskId;

    public TaskIdEntity(Long id) {
        this.setTaskId(id);
    }

    public TaskIdEntity() {
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
}
