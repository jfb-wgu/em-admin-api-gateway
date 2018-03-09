package edu.wgu.dmadmin.model.publish;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(keyspace = "dm", name = "supporting_document_by_id")
public class SupportingDocumentByIdModel extends SupportingDocumentModel {
	
	@PartitionKey(0)
	public UUID getAttachmentId() {
		return this.attachmentId;
	}
	
	@PartitionKey(1)
	public UUID getTaskId() {
		return this.taskId;
	}
	
	public SupportingDocumentByIdModel(SupportingDocumentModel model) {
		this.populate(model);
	}
}
