package edu.wgu.dmadmin.domain.publish;

import edu.wgu.dmadmin.model.publish.HyperlinkModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Hyperlink {
	String title;
	String description;
	String url;
	
	public Hyperlink(HyperlinkModel link) {
		this.title = link.getTitle();
		this.description = link.getDescription();
		this.url = link.getUrl();
	}
}
