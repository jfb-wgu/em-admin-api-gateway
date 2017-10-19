package edu.wgu.dmadmin.model.publish;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import edu.wgu.dmadmin.domain.publish.MimeType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(keyspace = "dm", name="mime_type", readConsistency = "QUORUM", writeConsistency = "QUORUM")
public class MimeTypeModel {
	
	@PartitionKey(0)
	String extension;
	
	@PartitionKey(1)
	@Column(name="type_id")
	UUID typeId;

	String application;
	
	@Column(name="mime_type")
	String mimeType;
	
	String description;
	
	public MimeTypeModel(MimeType type) {
		this.typeId = type.getTypeId();
		this.extension = type.getExtension();
		this.application = type.getApplication();
		this.mimeType = type.getMimeType();
		this.description = type.getDescription();
	}
}
