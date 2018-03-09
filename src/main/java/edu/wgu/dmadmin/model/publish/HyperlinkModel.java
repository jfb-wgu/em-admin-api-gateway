package edu.wgu.dmadmin.model.publish;

import com.datastax.driver.mapping.annotations.UDT;

import edu.wgu.dmadmin.domain.publish.Hyperlink;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@UDT(keyspace = "dm", name="hyperlink")
public class HyperlinkModel {
	String title;
	String description;
	String url;
	
	public HyperlinkModel(Hyperlink link) {
		this.title = link.getTitle();
		this.description = link.getDescription();
		this.url = link.getUrl();
	}
}
