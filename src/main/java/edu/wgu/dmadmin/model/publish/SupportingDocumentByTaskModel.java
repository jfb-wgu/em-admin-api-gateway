package edu.wgu.dmadmin.model.publish;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name = "supporting_document")
public class SupportingDocumentByTaskModel extends SupportingDocumentModel {
	
	@PartitionKey(0)
	public UUID getTaskId() {
		return this.taskId;
	}
	
	@PartitionKey(1)
	public UUID getAttachmentId() {
		return this.attachmentId;
	}
	
	public SupportingDocumentByTaskModel(SupportingDocumentModel model) {
		this.populate(model);
	}
}
