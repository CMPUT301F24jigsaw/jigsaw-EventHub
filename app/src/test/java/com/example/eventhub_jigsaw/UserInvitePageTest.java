package com.example.eventhub_jigsaw;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.eventhub_jigsaw.entrant.UserInvitePage;

public class UserInvitePageTest {
    @Test
    void testGetEventName_user() {
        String mockEventName_user = "Mock Name";
        int mockEventImage_user = 002;
        UserInvitePage mockPage = new UserInvitePage(mockEventName_user, mockEventImage_user);

        assertEquals(mockEventName_user, mockPage.getEventName_user());
    }

    @Test
    void testGetEventImage_user() {
        String mockEventName_user = "Mock Name";
        int mockEventImage_user = 002;
        UserInvitePage mockPage = new UserInvitePage(mockEventName_user, mockEventImage_user);

        assertEquals(mockEventImage_user, mockPage.getEventImage_user());
    }
}
