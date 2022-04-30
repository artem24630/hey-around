package ru.atproduction.heyaround;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityTestRule<LoginActivity> testRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void useAppContext()
    {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("ru.atproduction.heyaround", appContext.getPackageName());
    }

    @Test
    public void registerTest()
    {
        onView(withId(R.id.mail))
                .check(matches(isDisplayed()))
                .perform(typeText(generateString() + "@bk.ru"), closeSoftKeyboard());
        onView(withId(R.id.pass))
                .check(matches(isDisplayed()))
                .perform(typeText("qwerty123"), closeSoftKeyboard());
        onView(withId(R.id.registr))
                .check(matches(isDisplayed()))
                .perform(click());

        try
        {
            Thread.sleep(3000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }


        onView(withText("123123"))
                .check(matches(isDisplayed()))
                .perform(typeText(generateString()), closeSoftKeyboard());


        onView(withText("OK"))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void createEventTest()
    {
        onView(withId(R.id.map)).perform(longClick());

        try
        {
            Thread.sleep(3000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        onView(withId(R.id.editText))
                .check(matches(isDisplayed()))
                .perform(typeText("asdasd"), closeSoftKeyboard());

        onView(withId(R.id.editText4))
                .check(matches(isDisplayed()))
                .perform(typeText("asdasd"), closeSoftKeyboard());

        onView(withId(R.id.button))
                .perform(click());

        try
        {
            Thread.sleep(1000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        onView(withText("I understand"))
                .check(matches(isDisplayed()))
                .perform(click());

        try
        {
            Thread.sleep(3000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        onView(withId(R.id.map))
                .check(matches(isDisplayed()));
    }

    private static final Random rnd = new Random();

    private static String generateString()
    {
        char[] chars = new char[rnd.nextInt(9)];
        for (int i = 0; i < chars.length; i++)
        {
            chars[i] = (char) (rnd.nextInt('z' - 'a') + 'a');
        }
        return new String(chars);
    }
}
