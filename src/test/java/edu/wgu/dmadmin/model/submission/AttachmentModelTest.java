package edu.wgu.dmadmin.model.submission;

import edu.wgu.dmadmin.domain.submission.Attachment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by joshua.barnett on 5/25/17.
 */
public class AttachmentModelTest {
    private AttachmentModel attachmentModel;

    @Before
    public void setUp() throws Exception {
        attachmentModel = new AttachmentModel();
    }

    @Test
    public void testBuildUrl() throws Exception {
        assertNull(attachmentModel.getUrl());
    }

    @Test
    public void testBuildUrlIsUrlNotNull() throws Exception {
        String url = "www.starwars.com";
        attachmentModel.setIsUrl(Boolean.TRUE);
        attachmentModel.setUrl(url);
        assertEquals(url, attachmentModel.getUrl());
    }

    @Test
    public void testBuildUrlNotUrlStudentAttachment() throws Exception {
        String url = "www.starwars.com";
        attachmentModel.setIsUrl(Boolean.FALSE);
        attachmentModel.setIsTaskDocument(Boolean.FALSE);
        attachmentModel.setUrl(url);
        assertEquals(url, attachmentModel.getUrl());
    }

    @Test
    public void testBuildUrlNotUrlIsTaskDocument() throws Exception {
        String url = "www.starwars.com";
        attachmentModel.setIsUrl(Boolean.FALSE);
        attachmentModel.setIsTaskDocument(Boolean.TRUE);
        attachmentModel.setUrl(url);
        assertEquals(url, attachmentModel.getUrl());
    }

    @Test
    public void testGetTitle() throws Exception {
        String title = "title";
        attachmentModel.setTitle(title);

        assertEquals(title, attachmentModel.getTitle());
    }

    @Test
    public void testGetUrl() throws Exception {
        String url = "www.starwars.com";
        attachmentModel.setUrl(url);

        assertEquals(url, attachmentModel.getUrl());
    }

    @Test
    public void testGetMimeType() throws Exception {
        String mimeType = "mime/type";
        attachmentModel.setMimeType(mimeType);

        assertEquals(mimeType, attachmentModel.getMimeType());
    }

    @Test
    public void testGetSize() throws Exception {
        Long size = new Long(23432);
        attachmentModel.setSize(size);

        assertEquals(size, attachmentModel.getSize());
    }

    @Test
    public void testGetIsUrl() throws Exception {
        attachmentModel.setIsUrl(Boolean.FALSE);

        assertFalse(attachmentModel.getIsUrl().booleanValue());
    }

    @Test
    public void testGetIsTaskDocument() throws Exception {
        attachmentModel.setIsTaskDocument(Boolean.FALSE);

        assertFalse(attachmentModel.getIsTaskDocument().booleanValue());
    }

    @Test
    public void testConstructor() throws Exception {
        String mimeType = "mimeType";
        String title = "title";
        String url = "www.starwars.com";
        Long size = new Long(2343);

        Attachment attachment = new Attachment();
        attachment.setIsTaskDocument(Boolean.FALSE);
        attachment.setIsUrl(Boolean.FALSE);
        attachment.setMimeType(mimeType);
        attachment.setSize(size);
        attachment.setTitle(title);
        attachment.setUrl(url);

        AttachmentModel attachment1 = new AttachmentModel(attachment);

        assertFalse(attachment1.getIsTaskDocument().booleanValue());
        assertFalse(attachment1.getIsUrl().booleanValue());
        assertEquals(mimeType, attachment1.getMimeType());
        assertEquals(title, attachment1.getTitle());
        assertEquals(url, attachment1.getUrl());
        assertEquals(size, attachment1.getSize());

    }

}