package edu.wgu.dmadmin.domain.submission;

import java.util.Date;

import edu.wgu.dmadmin.model.submission.ReferralModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Referral {

    String type;
    String URL;
    String status;
    String createdBy;
    Date dateCreated;
    String creatorComments;
    String reviewedBy;
    Date dateReviewed;
    String reviewerComments;
    boolean returnToCreator;
    
    public Referral(ReferralModel referral) {
        this.type = referral.getType();
        this.URL = referral.getURL();
        this.status = referral.getStatus();
        this.createdBy = referral.getCreatedBy();
        this.dateCreated = referral.getDateCreated();
        this.creatorComments = referral.getCreatorComments();
        this.reviewedBy = referral.getReviewedBy();
        this.dateReviewed = referral.getDateReviewed();
        this.reviewerComments = referral.getReviewerComments();
        this.returnToCreator = referral.isReturnToCreator();
    }
}
