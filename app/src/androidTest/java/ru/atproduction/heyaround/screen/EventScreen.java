package ru.atproduction.heyaround.screen;

import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

import org.hamcrest.Matcher;

import ru.atproduction.heyaround.R;

public class EventScreen {

    public static final Matcher<View> EDIT_EVENT_NAME_VIEW = withId(R.id.editText4);
    public static final Matcher<View> CREATE_EVENT_BTN = withId(R.id.button);
}
