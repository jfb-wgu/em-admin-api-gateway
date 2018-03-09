package edu.wgu.dmadmin.model.submission;

import java.util.Date;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.UDT;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@UDT(keyspace = "dm", name = "referral")
public class ReferralModel {

    String type;
    String URL;    
    String status;
    
    @Field(name="created_by")
    String createdBy;
    
    @Field(name="date_created")
    Date dateCreated;
    
    @Field(name="creator_comments")
    String creatorComments;
    
    @Field(name="reviewed_by")
    String reviewedBy;
    
    @Field(name="date_reviewed")
    Date dateReviewed;

    @Field(name="reviewer_comments")
    String reviewerComments;
    
    @Field(name="return_to_creator")
    boolean returnToCreator;
}
