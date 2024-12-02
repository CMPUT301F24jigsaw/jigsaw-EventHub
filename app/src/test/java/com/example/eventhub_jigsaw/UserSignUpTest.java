package com.example.eventhub_jigsaw;

import org.junit.jupiter.api.Test;

/**
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;


public class UserSignUpTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // Launch the UserSignUp activity
        ActivityScenario.launch(UserSignUp.class);
    }

    @Test
    public void testEmptyFieldsValidation() {
        // Attempt to register without entering any data
        onView(withId(R.id.button_signup)).perform(click());

        // Verify error messages are displayed for mandatory fields
        onView(withId(R.id.edit_name)).check(matches(withText("Name is required!")));
        onView(withId(R.id.edit_email)).check(matches(withText("Email is required!")));
    }

    @Test
    public void testValidUserRegistration() {
        // Input valid data
        onView(withId(R.id.edit_name)).perform(typeText("John Doe"));
        onView(withId(R.id.edit_email)).perform(typeText("john.doe@example.com"));
        onView(withId(R.id.edit_phone)).perform(typeText("1234567890"));
        onView(withId(R.id.spinner_user)).perform(click());
        onView(withText("ENTRANT")).perform(click());

        // Click the Sign Up button
        onView(withId(R.id.button_signup)).perform(click());

        // Verify navigation to the correct home page or success message
        // Mocking or additional setup may be required for Firebase interactions
        // This example assumes a toast message for success
        onView(withText("User registered successfully!")).check(matches(isDisplayed()));
    }
}

 */