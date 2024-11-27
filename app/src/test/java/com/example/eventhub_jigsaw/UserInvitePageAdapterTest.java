package com.example.eventhub_jigsaw;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

import com.example.eventhub_jigsaw.entrant.UserInvitePage;
import com.example.eventhub_jigsaw.entrant.UserInvitePageAdapter;

import org.junit.jupiter.api.Test;
import java.util.List;

public class UserInvitePageAdapterTest {
    UserInvitePageAdapter adapter;
    List<UserInvitePage> mockEventList;

    @Test
    void testGetItemCount() {
        UserInvitePage mockPage = new UserInvitePage("Mock Name", 123);
        adapter.eventList.addAll(asList(mockPage, mockPage, mockPage));
        assertEquals(adapter.getItemCount(), 3);
    }

    @Test
    void getItemAtPosition() {
        UserInvitePage firstPage = new UserInvitePage("Page 1", 001);
        UserInvitePage secondPage = new UserInvitePage("Page 2", 002);
        adapter.eventList = asList(firstPage, secondPage);
        assertEquals(adapter.getItemId(0), firstPage.hashCode());
        assertEquals(adapter.getItemId(1), secondPage.hashCode());
    }
}
