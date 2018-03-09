package edu.wgu.dmadmin.domain.common;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.wgu.dmadmin.domain.publish.MimeType;
import lombok.Data;

@Data
public class Attachment {
	UUID attachmentId;
	String title;
	Date createdAt;
	Date modifiedAt;
	String uploadStatus;
	String url;
	String mimeType;
	Long size;
	
	@JsonIgnore
	String path;
	
	@JsonIgnore
	String attachmentUrl;
	
	@JsonGetter("isUrl")
	public boolean getIsUrl() {
		return MimeType.URLMimeType.equalsIgnoreCase(this.getMimeType());
	}
}
