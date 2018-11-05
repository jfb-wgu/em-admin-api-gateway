package edu.wgu.dmadmin.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.text.ParseException;
import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import edu.wgu.dm.admin.repository.UserRepo;
import edu.wgu.dm.admin.service.UserInfoService;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.service.feign.PersonService;
import edu.wgu.dmadmin.test.TestObjectFactory;

@RunWith(MockitoJUnitRunner.class)
public class UserInfoServiceTest {

    @InjectMocks
    UserInfoService service;

    @Mock
    UserRepo repo;

    @Mock
    PersonService pService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    User user1 = TestObjectFactory.getUser("first", "last");

    Person person1 = TestObjectFactory.getPerson("first", "last");

    @Before
    public void initialize() {
        when(this.repo.getUserById(this.user1.getUserId())).thenReturn(Optional.of(this.user1));

        when(this.pService.getPersonByBannerId(this.user1.getUserId())).thenReturn(this.person1);
    }

    @Test
    public void testGetPersonFromRequestEmployee() throws ParseException {
        String authToken =
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImFjY2Vzcy5kZXYud2d1LmVkdSJ9.eyJzY29wZSI6W10sImNsaWVudF9pZCI6IndndV9tb2JpbGUiLCJ3Z3VVVUlEIjoiZjI3M2IzYTQtMWM2OC00MDdiLTllZTEtMmQxNDM2MTdkOTc5Iiwid2d1QmFubmVySUQiOiJFMDAxMDc0NDgiLCJiYW5uZXJfaWQiOiJFMDAxMDc0NDgiLCJwaWRtIjoiNTQ2NDQxIiwiZ2l2ZW5OYW1lIjoiSmVzc2ljYSIsIndndUxldmVsT25lUm9sZSI6IkVtcGxveWVlIiwid2d1UElETSI6IjU0NjQ0MSIsIndndV9yb2xlX29uZSI6IkVtcGxveWVlIiwiY24iOiJKZXNzaWNhIFBhbWRldGgiLCJzbiI6IlBhbWRldGgiLCJ1c2VybmFtZSI6Implc3NpY2EucGFtZGV0aCIsImV4cCI6MTUwNjY0MDUwOH0.BgidTYhM-9hMTDhOjqjoo2wfybqsuhH7WuRsAGty-edY6l162LNVIoOFZboo1jCtb-hxbZYIDHXe_efa1K9fqTQJQ1lbq8TpgGbyTOJ3jjffj7YHw0n-qLR1c2DcwMi1d_N3ytd7kCC65E9-SUbr2dcGx5fsbIhNW5Zqpu3P9IIIjuueNbbBqMFxK4sTgiCStaLPJH2qH3iNGUTi29zaulV5zhIXQmtjsJrp54K62PR8wOKN4FUNDDPwLLbOr8R2abOtGV_SVkTcWHs500KnTFXhkgVO9HtBj-sz10HONUIPU3OlPh3jjWnwt6SyXc-1otY71oEDK4X5bK7HFRmScw";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("authorization", authToken);
        when(this.repo.getUserById("E00107448")).thenReturn(Optional.of(this.user1));
        Person result = this.service.getPersonFromRequest(request, this.user1.getUserId());
        assertEquals("Jessica", result.getFirstName());
    }

    @Test
    public void testGetPersonFromRequestStudent() throws ParseException {
        String authToken =
                "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImFjY2Vzcy5kZXYud2d1LmVkdSJ9.eyJzY29wZSI6W10sImNsaWVudF9pZCI6IndndV9tb2JpbGUiLCJ3Z3VVVUlEIjoiZWM3ZWQ4MGMtNGRlNS00NWFjLTgwZDEtZDdkYmNlMTMzOTMyIiwiYmFubmVyX2lkIjoiUUEwMDAwMDEzIiwiZ2l2ZW5OYW1lIjoiS3Jpc3RpbiIsImNuIjoiS3Jpc3RpbiBQb2luZGV4dGVyIiwid2d1TGV2ZWxUd29Sb2xlIjoiU3R1ZGVudCIsIndndUJhbm5lcklEIjoiUUEwMDAwMDEzIiwid2d1X3JvbGVfdHdvIjoiU3R1ZGVudCIsInBpZG0iOiIxMDY4MDYiLCJ3Z3VMZXZlbE9uZVJvbGUiOiJTdHVkZW50Iiwid2d1UElETSI6IjEwNjgwNiIsIndndV9yb2xlX29uZSI6IlN0dWRlbnQiLCJzbiI6IlBvaW5kZXh0ZXIiLCJ1c2VybmFtZSI6Imtwb2luZGV4dGVyIiwiZXhwIjoxNTA2NjQwNzM1fQ.CY4mwbLl9Q8aphSdgI3uLaCOPpam7CRaaY09tmKCb0mfhE229UTvs_DSpKbgOCdrS-i5EWcsaBcyw7b3hIBegBRru-EuhYGAxjWtZxL9Rj9-zT7ijpEk8a6V8mGZH-CXhOHE_abQXlW818lZOlS-ZeLgaAIW4BUnQm0Bvl-CYHXIfZmR-wsl_ZY1hO27qxW8Gb8YSXw9AZnqgjFKSiWiKBoYrlCYmsUU5vD7WSqArNcIifWnxfjKrmCCQSMD4DoxAZwyIphB-aM1MjRkjMYoRmDe3I3eorjPhp6ZvBbyp5VWUIHf2b7RHhVyJFBiN8fo_55rcNYZYdYMEuJWiSq4dw";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("authorization", authToken);
        Person result = this.service.getPersonFromRequest(request, this.user1.getUserId());
        assertEquals("Kristin", result.getFirstName());
    }

    @Test
    public void testGetPersonByUserId() {
        this.person1.setIsEmployee(Boolean.FALSE);

        Person result = this.service.getPersonByUserId(this.user1.getUserId());
        verify(this.repo, never()).getUserById(this.user1.getUserId());
        assertEquals(result.getPidm(), this.person1.getPidm());
    }

    @Test
    public void testGetPersonByUserIdNotFound() {
        this.person1.setIsEmployee(Boolean.TRUE);
        when(this.repo.getUserById(this.user1.getUserId())).thenReturn(Optional.empty());
        this.service.getPersonByUserId(this.user1.getUserId());
    }

    @Test
    public void testGetPersonByUserIdEmployee() {
        this.person1.setIsEmployee(Boolean.TRUE);

        Person result = this.service.getPersonByUserId(this.user1.getUserId());
        verify(this.repo).getUserById(this.user1.getUserId());
        assertEquals(result.getPidm(), this.person1.getPidm());
    }
}
