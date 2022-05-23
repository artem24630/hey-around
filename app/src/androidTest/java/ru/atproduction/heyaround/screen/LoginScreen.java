package ru.atproduction.heyaround.screen;

import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

import org.hamcrest.Matcher;

import ru.atproduction.heyaround.R;

public class LoginScreen {

    public static final Matcher<View> MAIL_VIEW = withId(R.id.mail);
    public static final Matcher<View> PASS_VIEW = withId(R.id.pass);
    public static final Matcher<View> SIGNIN_VIEW = withId(R.id.registr);
}
