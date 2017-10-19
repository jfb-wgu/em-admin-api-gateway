package edu.wgu.dmadmin;

import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.domain.assessment.Evaluation;
import edu.wgu.dmadmin.domain.assessment.Score;
import edu.wgu.dmadmin.domain.assessment.ScoreReport;
import edu.wgu.dmadmin.domain.publish.Anchor;
import edu.wgu.dmadmin.domain.publish.Aspect;
import edu.wgu.dmadmin.domain.publish.Competency;
import edu.wgu.dmadmin.domain.publish.Rubric;
import edu.wgu.dmadmin.domain.publish.Task;
import edu.wgu.dmadmin.domain.search.SearchCriteria;
import edu.wgu.dmadmin.domain.security.Permissions;
import edu.wgu.dmadmin.domain.security.User;
import edu.wgu.dmadmin.domain.submission.Attachment;
import edu.wgu.dmadmin.domain.submission.Submission;
import edu.wgu.dmadmin.model.assessment.*;
import edu.wgu.dmadmin.model.publish.*;
import edu.wgu.dmadmin.model.security.PermissionModel;
import edu.wgu.dmadmin.model.security.RoleModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.model.submission.AttachmentModel;
import edu.wgu.dmadmin.model.submission.ReferralModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.StatusUtil;

import static edu.wgu.dmadmin.util.StatusUtil.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("boxing")
public class TestObjectFactory {
    private static Submission submission;
    private static SubmissionModel submissionModel;
    private static UUID submissionId = UUID.randomUUID();
    private static UUID taskId = UUID.randomUUID();
    private static UUID assessmentId = UUID.randomUUID();
    private static UUID evaluationId = UUID.randomUUID();
    private static String studentId = "1234567890";
    private static String evaluatorId = "E00483838";
    private static String evaluatorFirstName = "Bruce";
    private static String evaluatorLastName = "Wayne";
    private static String employeeId = "employeeId";

    private static Random random = new Random();
    private static String userId = evaluatorId;

    public static String getUserId(){
        return TestObjectFactory.userId;
    }

    public static String getEvaluatorId(){
        return TestObjectFactory.evaluatorId;
    }

    public static Submission getTestSubmission(String status) {
        submission = new Submission(getSubmissionModel(submissionId, status, taskId, studentId, evaluatorFirstName, evaluatorLastName, evaluatorId));
        return submission;
    }

    public static SubmissionModel getSubmissionModel(String status) {
        return getSubmissionModel(submissionId, status, taskId, studentId, evaluatorFirstName, evaluatorLastName, evaluatorId);
    }

    public static SubmissionByIdModel getSubmissionByIdModel(String status) {
        return new SubmissionByIdModel(getSubmissionModel(submissionId, status, UUID.randomUUID(), studentId, evaluatorFirstName, evaluatorLastName, evaluatorId));
    }

    public static SubmissionModel getSubmissionModel(UUID inSubmissionId, String status, UUID inTaskId,
                                                     String inStudentId, String firstName, String lastName, String evalId) {

        submissionModel = new SubmissionModel();
        submissionModel.setSubmissionId(inSubmissionId);
        submissionModel.setStudentId(inStudentId);
        submissionModel.setAttempt(1);
        submissionModel.setTaskId(inTaskId);
        submissionModel.setTaskName("Task1");
        submissionModel.setAspectCount(4);
        submissionModel.setCourseCode("C745");
        submissionModel.setCourseName("How to be Batman");
        submissionModel.setAssessmentId(assessmentId);
        submissionModel.setAssessmentCode("BAT1");
        submissionModel.setAssessmentName("Batman Final Test");
        submissionModel.setComments("These are some comments");
        submissionModel.setInternalComments(null);
        submissionModel.setAttachments(null);
        submissionModel.setStatus(status);
        submissionModel.setStatusGroup(getStatusGroup(status));
        submissionModel.setReferrals(null);
        submissionModel.setPreviousSubmissionId(null);
        submissionModel.setPreviousEvaluationId(null);
        submissionModel.setReviewEvaluationId(null);
        submissionModel.setPidm(new Long(234567));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, Math.negateExact(random.nextInt(1440)));
        submissionModel.setDateCreated(calendar.getTime());

        if (hasBeenSubmitted(status)) {
            calendar.add(Calendar.HOUR, random.nextInt(48));
            submissionModel.setDateSubmitted(calendar.getTime());

            calendar.add(Calendar.HOUR, 72);
            submissionModel.setDateEstimated(calendar.getTime());

            if (hasBeenClaimed(status)) {
                calendar.add(Calendar.HOUR, Math.negateExact(random.nextInt(60)));
                submissionModel.setDateStarted(calendar.getTime());

                submissionModel.setEvaluatorId(evalId);
                submissionModel.setEvaluatorFirstName(firstName);
                submissionModel.setEvaluatorLastName(lastName);
                submissionModel.setEvaluationId(evaluationId);

                if (hasBeenEvaluated(status)) {
                    calendar.add(Calendar.HOUR, random.nextInt(5));
                    submissionModel.setDateCompleted(calendar.getTime());
                }
            }
        }

        submissionModel.setDateUpdated(calendar.getTime());

        return submissionModel;
    }

    public static List<SubmissionModel> getSubmissions(List<String> studentIds, List<String> firstNames, List<String> lastNames,
                                                       List<String> statuses, List<UUID> tasks, List<String> evaluatorIds, int generate) {
        List<SubmissionModel> submissions = new ArrayList<SubmissionModel>();

        for (int i = 0; i < generate; i++) {
            UUID randSubmissionId = UUID.randomUUID();
            String randStudentId = studentIds.get(random.nextInt(studentIds.size()));
            String status = statuses.get(random.nextInt(statuses.size()));
            UUID randTaskId = tasks.get(random.nextInt(tasks.size()));
            String firstName = firstNames.get(random.nextInt(firstNames.size()));
            String lastName = lastNames.get(random.nextInt(lastNames.size()));
            String evalId = evaluatorIds.get(random.nextInt(evaluatorIds.size()));

            SubmissionModel sub = getSubmissionModel(randSubmissionId, status, randTaskId, randStudentId, firstName, lastName, evalId);
            submissions.add(sub);
        }

        return submissions;
    }

    public static TaskByIdModel getTaskByIdModel(UUID inAssessmentId, UUID inTaskId, Long inCourseId, int assessmentOrder, int taskOrder, String taskName) {
        TaskByIdModel taskModel = new TaskByIdModel();
        taskModel.setCourseId(inCourseId);
        taskModel.setAssessmentId(inAssessmentId);
        taskModel.setTaskId(inTaskId);
        taskModel.setAssessmentOrder(assessmentOrder);
        taskModel.setTaskOrder(taskOrder);
        taskModel.setTaskName(taskName);
        taskModel.setCourseName("Test Course");
        taskModel.setCourseCode("TST1");
        taskModel.setAssessmentName("Test Assessment");
        taskModel.setAssessmentCode("ASSMNT");
        taskModel.setRubric(getRubricModel("test rubric", "my testing rubric"));
        return taskModel;
    }

    public static List<CompetencyModel> getCompetenciesList(int numCompetencies) {
        numCompetencies = numCompetencies <= 0 ? 1 : numCompetencies;
        List<CompetencyModel> competencies = new ArrayList<>();

        for (int i = 0; i < numCompetencies; i++) {
            CompetencyModel comp = new CompetencyModel();
            comp.setCode("234" + i);
            comp.setDescription("This is the " + i + "th description");
            comp.setName("Competency " + i);
            competencies.add(comp);
        }

        return competencies;
    }

    public static List<Competency> getPublishCompetenciesList(int numCompetencies) {
        numCompetencies = numCompetencies <= 0 ? 1 : numCompetencies;
        List<Competency> competencies = new ArrayList<>();

        for (int i = 0; i < numCompetencies; i++) {
            Competency comp = new Competency();
            comp.setCode("234" + i);
            comp.setDescription("This is the " + i + "th description");
            comp.setName("Competency " + i);
            competencies.add(comp);
        }

        return competencies;
    }

    public static String getRequirementList(int numRequirements) {
        numRequirements = numRequirements <= 0 ? 1 : numRequirements;
        StringBuilder requirements = new StringBuilder();

        for (int i = 0; i < numRequirements; i++) {
            if (i == numRequirements - 1) {
                requirements.append("<p> Note ").append(i).append("</p> ");

            } else {
                requirements.append("<p> Note ").append(i).append("</p>, ");
            }
        }

        return requirements.toString();
    }

    public static List<String> getWebLinkList(int numWebLinks) {
        numWebLinks = numWebLinks <= 0 ? 1 : numWebLinks;
        List<String> webLinks = new ArrayList<>();

        for (int i = 0; i < numWebLinks; i++) {
            webLinks.add("WebLink " + i);
        }

        return webLinks;
    }

    public static String getNotesList(int numNotes) {
        numNotes = numNotes <= 0 ? 1 : numNotes;
        StringBuilder notes = new StringBuilder();

        for (int i = 0; i < numNotes; i++) {
            if (i == numNotes - 1) {
                notes.append("<p> Note ").append(i).append("</p> ");

            } else {
                notes.append("<p> Note ").append(i).append("</p>, ");
            }
        }

        return notes.toString();
    }

    public static Map<String, AttachmentModel> getSupportingDocuments(int numSupportingDocuments) {
        numSupportingDocuments = numSupportingDocuments <= 0 ? 1 : numSupportingDocuments;
        Map<String, AttachmentModel> suportingDocuments = new HashMap<>();

        for (int i = 0; i < numSupportingDocuments; i++) {
            String title = "Title " + i;
            AttachmentModel attachmentModel = new AttachmentModel();
            attachmentModel.setIsUrl(i % 2 == 0);
            attachmentModel.setTitle(title);
            attachmentModel.setIsTaskDocument(true);
            attachmentModel.setSize((long) (i * 2));
            attachmentModel.setMimeType("text");
            suportingDocuments.put(title, attachmentModel);
        }

        return suportingDocuments;
    }

    public static Map<String, Attachment> getPublishSupportingDocuments(int numSupportingDocuments) {
        numSupportingDocuments = numSupportingDocuments <= 0 ? 1 : numSupportingDocuments;
        Map<String, Attachment> suportingDocuments = new HashMap<>();

        for (int i = 0; i < numSupportingDocuments; i++) {
            String title = "Title " + i;
            Attachment attachment = new Attachment();
            attachment.setIsUrl(i % 2 == 0);
            attachment.setTitle(title);
            attachment.setIsTaskDocument(true);
            attachment.setSize((long) (i * 2));
            attachment.setMimeType("text");
            suportingDocuments.put(title, attachment);
        }

        return suportingDocuments;
    }

    public static TaskModel getTaskModel() {
        TaskModel taskModel = new TaskModel();
        taskModel.setCourseName("Course 1");
        taskModel.setCourseCode("C1C1");
        taskModel.setCourseId(123456L);
        taskModel.setAssessmentName("Assessment 1");
        taskModel.setAssessmentCode("A1A1");
        taskModel.setAssessmentDate("2015-07-09");
        taskModel.setAssessmentType("Performance");
        taskModel.setAssessmentOrder(1);
        taskModel.setAssessmentId(UUID.randomUUID());
        taskModel.setTaskName("Task Name");
        taskModel.setTaskId(UUID.randomUUID());
        taskModel.setTaskOrder(1);
        taskModel.setAverageTime(34523);
        taskModel.setDescription("Description");
        taskModel.setCompetencies(getCompetenciesList(3));
        taskModel.setSupportingDocuments(getSupportingDocuments(4));
        taskModel.setIntroduction("Introduction");
        taskModel.setScenario("Senario");
        taskModel.setNotes(getNotesList(3));
        taskModel.setRequirements(getRequirementList(4));
        taskModel.setCRDNotes("CRD Notes");
        taskModel.setRubric(getRubricModel("Rubric Name", "description"));
        taskModel.setAspectCount(4);
        taskModel.setWebLinks(getWebLinkList(3));
        taskModel.setOriginalityMinimum(3);
        taskModel.setOriginalityWarning(5);
        taskModel.setDateCreated(new Date());
        taskModel.setDateUpdated(new Date());
        taskModel.setDatePublished(new Date());
        taskModel.setDateRetired(null);
        taskModel.setPublicationStatus("Published");
        return taskModel;
    }

    public static Task getPublishTask() {
        Task publishTask = new Task();
        publishTask.setCourseName("Course 1");
        publishTask.setCourseCode("C1C1");
        publishTask.setCourseId(123456L);
        publishTask.setAssessmentName("Assessment 1");
        publishTask.setAssessmentCode("A1A1");
        publishTask.setAssessmentDate("2015-07-09");
        publishTask.setAssessmentType("Performance");
        publishTask.setAssessmentOrder(1);
        publishTask.setAssessmentId(UUID.randomUUID());
        publishTask.setTaskName("Task Name");
        publishTask.setTaskId(UUID.randomUUID());
        publishTask.setTaskOrder(1);
        publishTask.setAverageTime(34523);
        publishTask.setDescription("Description");
        publishTask.setCompetencies(getPublishCompetenciesList(3));
        publishTask.setSupportingDocuments(getPublishSupportingDocuments(4));
        publishTask.setIntroduction("Introduction");
        publishTask.setScenario("Senario");
        publishTask.setNotes(getNotesList(3));
        publishTask.setRequirements(getRequirementList(4));
        publishTask.setCRDNotes("CRD Notes");
        publishTask.setRubric(getPublishRubricModel("Rubric Name", "description"));
        publishTask.setAspectCount(4);
        publishTask.setWebLinks(getWebLinkList(3));
        publishTask.setOriginalityMinimum(3);
        publishTask.setOriginalityWarning(5);
        publishTask.setDateCreated(new Date());
        publishTask.setDateUpdated(new Date());
        publishTask.setDatePublished(new Date());
        publishTask.setDateRetired(null);
        publishTask.setPublicationStatus("Published");
        return publishTask;
    }

    public static RubricModel getRubricModel(String name, String description) {
        RubricModel rubric = new RubricModel();
        rubric.setName(name);
        rubric.setDescription(description);

        rubric.getAspects().add(getAspectModel("1", "first test aspect", 2, 1));
        rubric.getAspects().add(getAspectModel("2", "second test aspect", 2, 2));
        rubric.getAspects().add(getAspectModel("3", "third test aspect", 2, 3));
        rubric.getAspects().add(getAspectModel("4", "fourth test aspect", 2, 3));

        return rubric;
    }

    public static Rubric getPublishRubricModel(String name, String description) {
        Rubric rubric = new Rubric();
        rubric.setName(name);
        rubric.setDescription(description);
        rubric.setAspects(new ArrayList<>());

        rubric.getAspects().add(getPublishAspect("1", "first test aspect", 2, 1));
        rubric.getAspects().add(getPublishAspect("2", "second test aspect", 2, 2));
        rubric.getAspects().add(getPublishAspect("3", "third test aspect", 2, 3));
        rubric.getAspects().add(getPublishAspect("4", "fourth test aspect", 2, 3));
        return rubric;
    }

    public static AspectModel getAspectModel(String name, String description, int passingScore, int order) {
        AspectModel aspect = new AspectModel();
        aspect.setName(name);
        aspect.setDescription(description);
        aspect.setPassingScore(passingScore);
        aspect.setOrder(order);

        aspect.getAnchors().add(getAnchorModel("first", "first anchor", 0));
        aspect.getAnchors().add(getAnchorModel("second", "second anchor", 1));
        aspect.getAnchors().add(getAnchorModel("third", "third anchor", 2));

        return aspect;
    }

    public static Aspect getPublishAspect(String name, String description, int passingScore, int order) {
        Aspect aspect = new Aspect();
        aspect.setName(name);
        aspect.setDescription(description);
        aspect.setPassingScore(passingScore);
        aspect.setOrder(order);
        aspect.setAnchors(new ArrayList<>());

        aspect.getAnchors().add(getPublishAnchor("first", "first anchor", 0));
        aspect.getAnchors().add(getPublishAnchor("second", "second anchor", 1));
        aspect.getAnchors().add(getPublishAnchor("third", "third anchor", 2));

        return aspect;
    }

    public static AnchorModel getAnchorModel(String name, String description, int score) {
        AnchorModel anchor = new AnchorModel();
        anchor.setName(name);
        anchor.setDescription(description);
        anchor.setScore(score);

        return anchor;
    }

    public static Anchor getPublishAnchor(String name, String description, int score) {
        Anchor anchor = new Anchor();
        anchor.setName(name);
        anchor.setDescription(description);
        anchor.setScore(score);

        return anchor;
    }

    public static EvaluationByIdModel getEvaluationByIdModelPassing() {
        return getEvaluationByIdModel(false, false);
    }

    public static EvaluationByIdModel getEvaluationByIdModelFailing() {
        return getEvaluationByIdModel(true, false);
    }

    public static SearchCriteria getSearchCriteria() {
        List<UUID> taskList = new ArrayList<>();
        taskList.add(UUID.randomUUID());
        taskList.add(UUID.randomUUID());
        taskList.add(UUID.randomUUID());

        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setStatus(StatusUtil.EVALUATION_BEGUN);
        searchCriteria.setTasks(taskList);
        searchCriteria.setDateRange(DateUtil.getZonedNow().toString());
        searchCriteria.setEvaluatorLastName(evaluatorLastName);
        searchCriteria.setEvaluatorFirstName(evaluatorFirstName);
        searchCriteria.setStudentId(studentId);
        searchCriteria.setSubmissionId(submissionId.toString());

        return searchCriteria;
    }

    public EvaluationByIdModel getEvaluationByIdModelNotComplete() {
        return getEvaluationByIdModel(false, true);
    }

    public static EvaluationByEvaluatorModel getEvaluationByEvaluatorModel() {
        return new EvaluationByEvaluatorModel(getEvaluationByIdModel(false, false));
    }

    public static EvaluationByIdModel getEvaluationByIdModel(boolean failing, boolean incomplete) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DateUtil.SERVER_ZONEID));
        calendar.add(Calendar.MINUTE, Math.negateExact(new Random().nextInt(360)));

        EvaluationByIdModel evaluationByIdModel = new EvaluationByIdModel();
        evaluationByIdModel.setDateCompleted(DateUtil.getZonedNow());
        evaluationByIdModel.setStatus(COMPLETED);
        evaluationByIdModel.setDateStarted(calendar.getTime());
        evaluationByIdModel.setEvaluationId(UUID.randomUUID());
        evaluationByIdModel.setStudentId(studentId);
        evaluationByIdModel.setAttempt(1);
        evaluationByIdModel.setEvaluatorId(evaluatorId);
        evaluationByIdModel.setEvaluatorFirstName(evaluatorFirstName);
        evaluationByIdModel.setEvaluatorLastName(evaluatorLastName);
        evaluationByIdModel.setSubmissionId(UUID.randomUUID());
        evaluationByIdModel.setTaskId(UUID.randomUUID());
        evaluationByIdModel.setMinutesSpent(Math.toIntExact(TimeUnit.MILLISECONDS.toMinutes(evaluationByIdModel.getDateCompleted().getTime() -
                evaluationByIdModel.getDateStarted().getTime())));

        ScoreReportModel scoreReport = getScoreReportModel("Test report", "Just testing");

        if (failing) scoreReport.getScores().get("2").setAssignedScore(1);
        if (incomplete) scoreReport.getScores().get("3").setAssignedScore(-1);

        evaluationByIdModel.setScoreReport(scoreReport);
        return evaluationByIdModel;
    }

    public static ScoreReportModel getScoreReportModel(String name, String description) {
        ScoreReportModel scoreReport = new ScoreReportModel();
        scoreReport.setName(name);
        scoreReport.setDescription(description);

        scoreReport.getScores().put("1", getScoreModel("1", 2, 2));
        scoreReport.getScores().put("2", getScoreModel("2", 2, 2));
        scoreReport.getScores().put("3", getScoreModel("3", 2, 2));
        scoreReport.getScores().put("4", getScoreModel("4", 2, 2));

        return scoreReport;
    }

    public static ScoreReport getScoreReport(String name, String description) {
        ScoreReport scoreReport = new ScoreReport(getScoreReportModel(name, description));
        scoreReport.setName(name);
        scoreReport.setDescription(description);

        scoreReport.getScores().add(getScore("1", 2, 2));
        scoreReport.getScores().add(getScore("2", 2, 2));
        scoreReport.getScores().add(getScore("3", 2, 2));
        scoreReport.getScores().add(getScore("4", 2, 2));

        return scoreReport;
    }

    public static Score getScore(String name, int passingScore, int assignedScore) {
        Comment comment = getComment(evaluatorId, evaluatorFirstName, evaluatorLastName, CommentTypes.STUDENT, 1, assignedScore);
        return getScore(name, passingScore, assignedScore, comment);
    }

    public static Score getScore(String name, int passingScore, int assignedScore, Comment comment) {
        Score score = new Score();
        score.setAssignedScore(assignedScore);
        score.setName(name);
        score.setPassingScore(passingScore);
        score.getComments().add(comment);

        return score;
    }

    public static ScoreModel getScoreModel(String name, int passingScore, int assignedScore) {
        CommentModel comment = getCommentModel(evaluatorId, evaluatorFirstName, evaluatorLastName, CommentTypes.STUDENT, 1, assignedScore);
        return getScoreModel(name, passingScore, assignedScore, comment);
    }

    public static ScoreModel getScoreModel(String name, int passingScore, int assignedScore, CommentModel comment) {
        ScoreModel score = new ScoreModel();
        score.setAssignedScore(assignedScore);
        score.setName(name);
        score.setPassingScore(passingScore);
        score.getComments().put(comment.getCommentId(), comment);

        return score;
    }

    public static UserByIdModel getUserModel() {
        UserByIdModel user = getUserModel(evaluatorFirstName, evaluatorLastName, evaluatorId, new HashSet<UUID>(),
                new HashSet<String>(), new HashSet<UUID>(), new HashSet<String>(), employeeId);

        user.getRoles().add(UUID.randomUUID());
        user.getLandings().add("dashboard");
        user.getLandings().add("evaluator");
        user.getPermissions().add(Permissions.TASK_QUEUE);
        user.getPermissions().add(Permissions.EVALUATION_CLAIM);

        return user;
    }

    public static UserByIdModel getUserModel(String inUserId, String inEmployeeId) {
        return getUserModel(evaluatorFirstName, evaluatorLastName, inUserId, new HashSet<UUID>(),
                new HashSet<String>(), new HashSet<UUID>(), new HashSet<String>(), inEmployeeId);
    }

    public static UserByIdModel getUserModel(String firstName, String lastName, String userId,
                                             Set<UUID> roles, Set<String> permissions, Set<UUID> tasks, Set<String> landings, String inEmployeeId) {
        UserByIdModel user = new UserByIdModel();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserId(userId);
        user.setLandings(landings);
        user.setPermissions(permissions);
        user.setRoles(roles);
        user.setTasks(tasks);
        user.setEmployeeId(inEmployeeId);

        return user;
    }

    public static User getUser(String firstName, String lastName, String userId,
                               Set<UUID> roles, Set<String> permissions, Set<UUID> tasks, Set<String> landings, String inEmployeeId) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserId(userId);
        user.setLandings(landings);
        user.setPermissions(permissions);
        user.setRoles(roles);
        user.setTasks(tasks);
        user.setEmployeeId(inEmployeeId);

        return user;
    }

    public static RoleModel getRoleModel(String role) {
        return getRoleModel(role, UUID.randomUUID());
    }

    public static RoleModel getRoleModel(String role, UUID roleId) {
        return getRoleModel(role, roleId, new HashSet<UUID>(), "testing role");
    }

    public static RoleModel getRoleModel(String role, UUID roleId, Set<UUID> permissions, String description) {
        RoleModel model = new RoleModel();
        model.setDateCreated(DateUtil.getZonedNow());
        model.setRole(role);
        model.setPermissions(permissions);
        model.setRoleDescription(description);
        model.setRoleId(roleId);

        return model;
    }

    public static PermissionModel getPermission(String permission, String landing) {
        return getPermissionModel(permission, UUID.randomUUID(), "test permission", landing, "test type");
    }

    public static PermissionModel getPermissionModel(String permission, UUID permissionId, String description, String landing, String type) {
        PermissionModel model = new PermissionModel();
        model.setDateCreated(DateUtil.getZonedNow());
        model.setPermission(permission);
        model.setLanding(landing);
        model.setPermissionDescription(description);
        model.setPermissionId(permissionId);
        model.setPermissionType(type);

        return model;
    }

    public static CommentModel getCommentModel(String userId, String firstName, String lastName, String inType, int attempt, int score) {
        CommentModel commentModel = new CommentModel();
        commentModel.setAttempt(attempt);
        commentModel.setCommentId(UUID.randomUUID());
        commentModel.setComments("This is a test comment.");
        commentModel.setDateCreated(DateUtil.getZonedNow());
        commentModel.setFirstName(firstName);
        commentModel.setLastName(lastName);
        commentModel.setScore(score);
        commentModel.setType(inType);
        commentModel.setUserId(userId);
        return commentModel;
    }

    public static Comment getComment(String userId, String firstName, String lastName, String inType, int attempt, int score) {
        Comment comment = new Comment();
        comment.setAttempt(attempt);
        comment.setCommentId(UUID.randomUUID());
        comment.setComments("This is a test comment.");
        comment.setDateCreated(DateUtil.getZonedNow());
        comment.setFirstName(firstName);
        comment.setLastName(lastName);
        comment.setScore(score);
        comment.setType(inType);
        comment.setUserId(userId);
        return comment;
    }

    public static AttachmentModel getAttachmentModel(String title, Long size, Boolean isTaskDocument, String mimeType, Boolean isUrl, String url) {
        AttachmentModel attachment = new AttachmentModel();
        attachment.setSize(size);
        attachment.setTitle(title);
        attachment.setIsTaskDocument(isTaskDocument);
        attachment.setMimeType(mimeType);
        attachment.setIsUrl(isUrl);
        attachment.setUrl(url);

        return attachment;
    }

    public static List<Attachment> getAttachmentList(int numAttachments){
        List<Attachment> list = new ArrayList<>();
        for (int x = 0; x < numAttachments; x++){
            String title = "Title " + (x + 1);
            list.add(getAttachment(title, 2L, false, "type", false, ""));
        }

        return list;
    }

    public static Attachment getAttachment(String title, Long size, Boolean isTaskDocument, String mimeType, Boolean isUrl, String url) {
        Attachment attachment = new Attachment();
        attachment.setSize(size);
        attachment.setTitle(title);
        attachment.setIsTaskDocument(isTaskDocument);
        attachment.setMimeType(mimeType);
        attachment.setIsUrl(isUrl);
        attachment.setUrl(url);

        return attachment;
    }

    public static EvaluationModel getEvaluationModel(UserModel user, SubmissionModel inSubmissionModel) {
        EvaluationModel evaluationModel = new EvaluationModel(user, inSubmissionModel);
        evaluationModel.setScoreReport(getScoreReportModel("ScoreReport", "Description"));
        evaluationModel.setStatus(EVALUATION_RELEASED);
        evaluationModel.setDateCompleted(DateUtil.getZonedNow());
        return evaluationModel;
    }

    public static Evaluation getEvaluation(UserModel user, SubmissionModel inSubmissionModel) {
        return new Evaluation(getEvaluationModel(user, inSubmissionModel));
    }


    public static ReferralModel getReferralModel() {
        ReferralModel model = new ReferralModel();
        model.setCreatedBy(evaluatorId);
        model.setCreatorComments("test comment");
        model.setDateCreated(DateUtil.getZonedNow());
        model.setStatus(WORKING);
        model.setType("excellence");
        model.setURL("http://l2my.wgu.edu/");
        return model;
    }

    public static Map<UUID, CommentModel> getCommentsMap(int numComments){
        HashMap<UUID, CommentModel> comments = new HashMap<>();
        for(int x = 0; x < numComments; x++) {
            UUID uuid = UUID.randomUUID();
            CommentModel value = new CommentModel();
            value.setComments("comment " + x+1);
            value.setCommentId(uuid);
            value.setAttempt(1);
            value.setUserId(userId);
            value.setType(CommentTypes.STUDENT);
            comments.put(uuid, value);
        }

        return comments;
    }
}
