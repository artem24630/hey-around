package ru.atproduction.heyaround.screen;

import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

import org.hamcrest.Matcher;

import ru.atproduction.heyaround.R;

public class PersonalAccountScreen {

    public static final Matcher<View> EDIT_USERNAME_VIEW = withId(R.id.textView9);
    public static final Matcher<View> EVENTS = withId(R.id.rv);
}
