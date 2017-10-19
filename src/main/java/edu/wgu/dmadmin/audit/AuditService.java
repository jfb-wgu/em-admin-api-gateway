package edu.wgu.dmadmin.audit;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import edu.wgu.dmadmin.exception.UserIdNotFoundException;
import edu.wgu.dmadmin.model.audit.ActivityLogByUserModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.IdentityUtil;

@Service
public class AuditService {
	
	@Autowired
	CassandraRepo cassandraRepo;
	
	@Autowired
	private IdentityUtil iUtil;
	
	Pattern uuid = Pattern.compile("[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89aAbB][a-f0-9]{3}-[a-f0-9]{12}", Pattern.CASE_INSENSITIVE);

	public void audit(JoinPoint joinPoint, @SuppressWarnings("unused") Audit auditable) throws UserIdNotFoundException {

		ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = sra.getRequest();

		Matcher matcher = uuid.matcher(request.getRequestURI());
		UUID itemId = null;
		if (matcher.find()) {
			itemId = UUID.fromString(matcher.group());
		}

		MethodSignature signature = (MethodSignature)joinPoint.getSignature();

		String methodName = signature.getName();
		String userId = iUtil.getUserId();

		ActivityLogByUserModel model = new ActivityLogByUserModel();
		model.setMethod(methodName);
		model.setLogId(UUID.randomUUID());
		model.setActivityDate(DateUtil.getZonedNow());
		model.setUserId(userId);
		model.setItemId(itemId);

		cassandraRepo.saveActivityLogEntry(model);
	}
	
    public void setCassandraRepo(CassandraRepo repo) {
    	this.cassandraRepo = repo;
    }
    
    public void setIdentityUtil(IdentityUtil utility) {
    	this.iUtil = utility;
    }
}
