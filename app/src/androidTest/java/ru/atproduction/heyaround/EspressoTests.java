package ru.atproduction.heyaround;


import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.maps.SupportMapFragment;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import ru.atproduction.heyaround.IdleResource.IdlingResources;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class EspressoTests {
    private static final String EVENT_NAME = "test_event";
    public static final String EMAIL = Utils.generateString() + "@bk.ru";
    private IdlingResource mIdlingResource;

    @Rule
    public ActivityTestRule<LoginActivity> testRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void useAppContext()
    {
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("ru.atproduction.heyaround", appContext.getPackageName());
    }

    @Test
    public void registerTest() throws InterruptedException
    {
        onView(withId(R.id.mail))
                .check(matches(isDisplayed()))
                .perform(typeText(EMAIL), closeSoftKeyboard());
        onView(withId(R.id.pass))
                .check(matches(isDisplayed()))
                .perform(typeText("qwerty123"), closeSoftKeyboard());
        onView(withId(R.id.registr))
                .check(matches(isDisplayed()))
                .perform(click());

        mIdlingResource = IdlingResources.dialogIdlingResource;
        IdlingRegistry.getInstance().register(mIdlingResource);

        onView(withId(R.id.edit_text_name))
                .check(matches(isDisplayed()))
                .perform(typeText(Utils.generateString()), closeSoftKeyboard());
        onView(withText("OK"))
                .check(matches(isDisplayed()))
                .perform(click());

        IdlingRegistry.getInstance().unregister(mIdlingResource);

        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void createEventTest() throws InterruptedException
    {
        onView(withId(R.id.map)).perform(longClick());

        Thread.sleep(3000);

        onView(withId(R.id.editText))
                .check(matches(isDisplayed()))
                .perform(typeText(EVENT_NAME), closeSoftKeyboard());

        onView(withId(R.id.editText4))
                .check(matches(isDisplayed()))
                .perform(typeText(EVENT_NAME), closeSoftKeyboard());

        onView(withId(R.id.button))
                .perform(click());


        Thread.sleep(1000);


        onView(withText("I understand"))
                .check(matches(isDisplayed()))
                .perform(click());

        Thread.sleep(3000);

        onView(withId(R.id.map))
                .check(matches(isDisplayed()));
    }

    @Test
    public void emailCorrectInProfileTest() throws InterruptedException
    {
        MapsActivity currentActivity = (MapsActivity) getCurrentActivity();
        Fragment newFragment = new AccountFragment();
        SupportMapFragment mapFragment = (SupportMapFragment) currentActivity
                .getSupportFragmentManager().findFragmentById(R.id.map);
        FragmentTransaction transaction = currentActivity
                .getSupportFragmentManager().beginTransaction();
        transaction.hide(mapFragment).add(R.id.container, newFragment).commitAllowingStateLoss();
        Thread.sleep(5000);

        onView(withId(R.id.textView9)).check(matches(withText("E-mail: " + EMAIL)));
    }

    @Test
    public void eventAppearedInProfileTest() throws InterruptedException
    {
        MapsActivity currentActivity = (MapsActivity) getCurrentActivity();
        Fragment newFragment = new AccountFragment();
        SupportMapFragment mapFragment = (SupportMapFragment) currentActivity
                .getSupportFragmentManager().findFragmentById(R.id.map);
        FragmentTransaction transaction = currentActivity
                .getSupportFragmentManager().beginTransaction();
        transaction.hide(mapFragment).add(R.id.container, newFragment).commitAllowingStateLoss();
        Thread.sleep(5000);
        onView(withId(R.id.rv))
                .check(matches(atPosition(0, hasDescendant(withText(EVENT_NAME)))));
    }


    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher)
    {

        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description)
            {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view)
            {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null)
                {
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }


    public static Activity getCurrentActivity()
    {
        final Activity[] currentActivity = {null};
        getInstrumentation().runOnMainSync(() -> {
            Collection<Activity> resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(RESUMED);
            if (resumedActivities.iterator().hasNext())
            {
                currentActivity[0] = (Activity) resumedActivities.iterator().next();
            }
        });
        return currentActivity[0];
    }

/*
        mIdlingResource = ((MapsActivity) getCurrentActivity()).getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
        mIdlingResource.registerIdleTransitionCallback(new IdlingResource.ResourceCallback() {
            @Override
            public void onTransitionToIdle()
            {

            }
        });
 */

}
