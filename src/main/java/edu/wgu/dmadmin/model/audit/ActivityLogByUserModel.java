package edu.wgu.dmadmin.model.audit;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(keyspace = "dm", name="activity_log_by_user")
public class ActivityLogByUserModel {	
	@PartitionKey(0)
	@Column(name="user_id")
	String userId;

	@PartitionKey(1)
	@Column(name="log_year_month")
	String logYearMonth;

	@PartitionKey(2)
	@Column(name="activity_date")
	Date activityDate;
	
	@PartitionKey(3)
	@Column(name="log_id")
	UUID logId;

	String method;
	
	@Column(name="item_id")
	UUID itemId;
}
