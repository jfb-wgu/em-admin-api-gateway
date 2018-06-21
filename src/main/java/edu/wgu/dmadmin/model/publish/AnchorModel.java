package edu.wgu.dmadmin.model.publish;

import com.datastax.driver.mapping.annotations.UDT;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@UDT(keyspace = "dm", name="anchor")
public class AnchorModel {
	String name;
	String description;
	int score;
}
