package edu.wgu.dmadmin.model.publish;

import com.datastax.driver.mapping.annotations.UDT;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@UDT(keyspace = "dm", name="competency")
public class CompetencyModel {
	String code;
	String name;
	String description;
}
