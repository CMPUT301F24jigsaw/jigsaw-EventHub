package com.example.eventhub_jigsaw;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.eventhub_jigsaw.admin.Events;


public class EventTest {
    @Test
    void testGetEventName() {
        String expectedEventName = "blobEvent";
        Events myEvent = new Events(expectedEventName, 0);
        String resultEventName = myEvent.getEventName();
        assertEquals(resultEventName, expectedEventName);
    }

    @Test
    void testGetEventImage() {
        int expectedEventImageId = 013;
        Events myEvent = new Events("Event 1", expectedEventImageId);
        int resultEventImageId = myEvent.getEventImage();
        assertEquals(resultEventImageId, expectedEventImageId);
    }
}
