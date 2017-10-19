package edu.wgu.dmadmin.repo;

import com.datastax.driver.core.Session;
import org.junit.Test;

public class CassandraRepoTest {
    CassandraRepo cassandraRepo;
    Session session;

//    @Before
//    public void setUp() throws Exception {
//        //session.getCluster().getConfiguration().getProtocolOptions().getProtocolVersion()
//        this.session = Mockito.mock(Session.class);
//        Cluster cluster = Mockito.mock(Cluster.class);
//        Configuration configuration = Mockito.mock(Configuration.class);
//        ProtocolOptions protocolOptions = Mockito.mock(ProtocolOptions.class);
//        ProtocolVersion protocolVersion = ProtocolVersion.V3;
//
//        when(protocolOptions.getProtocolVersion()).thenReturn(protocolVersion);
//        when(configuration.getProtocolOptions()).thenReturn(protocolOptions);
//        when(cluster.getConfiguration()).thenReturn(configuration);
//        when(this.session.getCluster()).thenReturn(cluster);
//
//
//        this.cassandraRepo = new CassandraRepo(this.session);
//    }

    @Test
    public void testGetUser() throws Exception {
    }
//
//    @Test
//    public void testGetPermissionsForUser() throws Exception {
//    }
//
//    @Test
//    public void testGetUserQualifications() throws Exception {
//    }
//
//    @Test
//    public void testGetUsers() throws Exception {
//    }
//
//    @Test
//    public void testGetUsersById() throws Exception {
//    }
//
//    @Test
//    public void testGetUsersByLastName() throws Exception {
//    }
//
//    @Test
//    public void testGetUsersByFirstName() throws Exception {
//    }
//
//    @Test
//    public void testGetUsersForRole() throws Exception {
//    }
//
//    @Test
//    public void testGetUsersForPermission() throws Exception {
//    }
//
//    @Test
//    public void testSaveUser() throws Exception {
//    }
//
//    @Test
//    public void testSaveUsers() throws Exception {
//    }
//
//    @Test
//    public void testDeleteUser() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionsByStatusGroup() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionsByStatusGroupAndTask() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionsByStatusGroupAndTasks() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionsByStatusesAndTasks() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionsByStatuses() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionsByEvaluator() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionsByEvaluatorAndTask() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionsByEvaluatorsAndTasks() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionsByEvaluators() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionsByStatus() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionsByStatusAndTask() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionsByStatusAndEvaluator() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionByStudentByTask() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionByStudentByTasks() throws Exception {
//    }
//
//    @Test
//    public void testGetLastSubmissionByStudentAndTaskId() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionHistoryByStudentAndTask() throws Exception {
//    }
//
//    @Test
//    public void testGetEvaluation() throws Exception {
//    }
//
//    @Test
//    public void testGetEvaluationById() throws Exception {
//    }
//
//    @Test
//    public void testGetScoreReport() throws Exception {
//    }
//
//    @Test
//    public void testGetEvaluationByEvaluaatorAndSubmission() throws Exception {
//    }
//
//    @Test
//    public void testSaveScoreReport() throws Exception {
//    }
//
//    @Test
//    public void testGetEvaluationsBySubmission() throws Exception {
//    }
//
//    @Test
//    public void testDeleteEvaluation() throws Exception {
//    }
//
//    @Test
//    public void testSaveEvaluation() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionsByStudentId() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionByStudentById() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionById() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionsById() throws Exception {
//    }
//
//    @Test
//    public void testGetConfirmationSubmission() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionStatus() throws Exception {
//    }
//
//    @Test
//    public void testGetCountSubmissionsByStudentAndAssessment() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionReferrals() throws Exception {
//    }
//
//    @Test
//    public void testGetTasks() throws Exception {
//    }
//
//    @Test
//    public void testGetTaskBasics() throws Exception {
//    }
//
//    @Test
//    public void testGetTasksByCourseId() throws Exception {
//        cassandraRepo.getTasksByCourseId(23423L);
//    }
//
//    @Test
//    public void testGetTasksByAssessment() throws Exception {
//    }
//
//    @Test
//    public void testGetBasicTasksByAssessment() throws Exception {
//    }
//
//    @Test
//    public void testGetTaskById() throws Exception {
//    }
//
//    @Test
//    public void testGetTaskKeys() throws Exception {
//    }
//
//    @Test
//    public void testGetTaskRubric() throws Exception {
//    }
//
//    @Test
//    public void testGetTaskBasics1() throws Exception {
//    }
//
//    @Test
//    public void testGetTaskForSubmission() throws Exception {
//    }
//
//    @Test
//    public void testGetSupportingDocuments() throws Exception {
//    }
//
//    @Test
//    public void testSaveSupportingDocuments() throws Exception {
//    }
//
//    @Test
//    public void testDeleteTask() throws Exception {
//    }
//
//    @Test
//    public void testSaveTask() throws Exception {
//    }
//
//    @Test
//    public void testSaveSubmissionLock() throws Exception {
//    }
//
//    @Test
//    public void testDeleteSubmissionLock() throws Exception {
//    }
//
//    @Test
//    public void testGetSubmissionLocks() throws Exception {
//    }
//
//    @Test
//    public void testSaveSubmission() throws Exception {
//    }
//
//    @Test
//    public void testSaveSubmission1() throws Exception {
//    }
//
//    @Test
//    public void testDeleteSubmission() throws Exception {
//    }
//
//    @Test
//    public void testGetStatusLogByAssessment() throws Exception {
//    }
//
//    @Test
//    public void testGetLastStatusForSubmission() throws Exception {
//    }
//
//    @Test
//    public void testSaveActivityLogEntry() throws Exception {
//    }
//
//    @Test
//    public void testSaveMimeType() throws Exception {
//    }
//
//    @Test
//    public void testGetMimeTypes() throws Exception {
//    }
//
//    @Test
//    public void testSaveStudentFeedback() throws Exception {
//    }
//
//    @Test
//    public void testGetFeedbackFromStudent() throws Exception {
//    }
//
//    @Test
//    public void testSavePermission() throws Exception {
//    }
//
//    @Test
//    public void testGetPermissions() throws Exception {
//    }
//
//    @Test
//    public void testGetPermissions1() throws Exception {
//    }
//
//    @Test
//    public void testGetPermission() throws Exception {
//    }
//
//    @Test
//    public void testSaveRole() throws Exception {
//    }
//
//    @Test
//    public void testDeleteRole() throws Exception {
//    }
//
//    @Test
//    public void testDeletePermission() throws Exception {
//    }
//
//    @Test
//    public void testGetRole() throws Exception {
//    }
//
//    @Test
//    public void testGetRoles() throws Exception {
//    }
//
//    @Test
//    public void testGetRoles1() throws Exception {
//    }
//
//    @Test
//    public void testGetInternalComments() throws Exception {
//    }

}