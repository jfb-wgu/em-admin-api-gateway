package edu.wgu.dmadmin.domain.publish;

import java.util.UUID;

import edu.wgu.dmadmin.model.publish.MimeTypeModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MimeType {
	UUID typeId;
	String extension;
	String application;
	String mimeType;
	String description;
	
	public MimeType(MimeTypeModel model) {
		this.typeId = model.getTypeId();
		this.extension = model.getExtension();
		this.application = model.getApplication();
		this.mimeType = model.getMimeType();
		this.description = model.getDescription();
	}
}
