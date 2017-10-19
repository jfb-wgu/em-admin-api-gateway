package edu.wgu.dmadmin.domain.submission;

import edu.wgu.dmadmin.domain.security.Permissions;

public class Referrals {
	
	public static final String LEAD = "TeamLead";
	public static final String ARTICUALATION = "Articulation";
	public static final String ORIGINALITY = "Originality";
	public static final String OPEN = "Open";
	
	public static String getPermissionForHold(String referral) {
		String permission;
		
		switch (referral) {
		case LEAD:
			permission = Permissions.LEAD_QUEUE;
			break;
		case ARTICUALATION:
			permission =  Permissions.ARTICULATION_QUEUE;
			break;
		case ORIGINALITY:
			permission =  Permissions.ORIGINALITY_QUEUE;
			break;
		case OPEN:
			permission =  Permissions.OPEN_QUEUE;
			break;
		default:
			throw new IllegalArgumentException("hold not recognized.");
		}
		
		return permission;
	}
}