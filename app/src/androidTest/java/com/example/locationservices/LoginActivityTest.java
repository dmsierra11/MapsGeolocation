package com.example.locationservices;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.locationservices.login.LoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by danielsierraf on 12/15/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mIntentsRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void checkEmailValidation(){
        ViewInteraction editText = onView(withId(R.id.email)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        editText.check(matches(hasErrorText("This field is required")));
    }

    //TODO: This email address is invalid
    @Test
    public void checkEmailInvalid(){
        ViewInteraction editText = onView(withId(R.id.email)).perform(replaceText("1"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        editText.check(matches(hasErrorText("This email address is invalid")));
    }

    @Test
    public void checkPasswordValidation(){
        onView(withId(R.id.email)).perform(replaceText("@"), closeSoftKeyboard());
        ViewInteraction editText = onView(withId(R.id.password)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        editText.check(matches(hasErrorText("This field is required")));
    }

    //TODO: This password is too short
    @Test
    public void checkPasswordTooShort(){
        onView(withId(R.id.email)).perform(replaceText("@"), closeSoftKeyboard());
        ViewInteraction editText = onView(withId(R.id.password)).perform(replaceText("1234"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button)).perform(click());
        editText.check(matches(hasErrorText("This password is too short")));
    }

    //TODO: Check error This password is incorrect

}