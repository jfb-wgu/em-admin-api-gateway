package edu.wgu.dmadmin.model.publish;

import java.util.List;

import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.UDT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@UDT(keyspace = "dm", name="rubric")
public class RubricModel {
	String name;
	String description;
	
	@Frozen
	List<AspectModel> aspects;
}
