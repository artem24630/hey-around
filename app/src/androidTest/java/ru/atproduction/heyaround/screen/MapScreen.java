package ru.atproduction.heyaround.screen;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import org.hamcrest.Matcher;

import ru.atproduction.heyaround.R;

public class MapScreen {

    public static final Matcher<View> EDIT_NICKNAME_VIEW = withId(R.id.edit_text_name);
    public static final Matcher<View> SUCCESS_VIEW = withText("OK");
    public static final Matcher<View> MAP_VIEW = withId(R.id.map);
}
