package edu.wgu.dmadmin.model.submission;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.UDT;
import edu.wgu.dmadmin.domain.submission.Attachment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@UDT(keyspace = "dm", name = "attachment")
public class AttachmentModel {

    String title;
    String url;
    Long size;

    @Field(name = "is_url")
    Boolean isUrl;

    @Field(name = "is_task_document")
    Boolean isTaskDocument;

    @Field(name="mime_type")
    String mimeType;
    
    public AttachmentModel(Attachment attachment) {
        this.title = attachment.getTitle();
        this.url = attachment.getUrl();
        this.mimeType = attachment.getMimeType();
        this.size = attachment.getSize();
        this.isTaskDocument = attachment.getIsTaskDocument();
        this.isUrl = attachment.getIsUrl();
    }
}
