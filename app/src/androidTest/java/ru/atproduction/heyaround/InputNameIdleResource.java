//package ru.atproduction.heyaround;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.espresso.IdlingResource;
//
//public class InputNameIdleResource implements IdlingResource {
//    private ResourceCallback resourceCallback;
//    private boolean isIdle;
//
//    @Override
//    public String getName()
//    {
//        return InputNameIdleResource.class.getName();
//    }
//
//    @Override
//    public boolean isIdleNow()
//    {
//        if (isIdle) return true;
//        if (getCurrentActivity() == null) return false;
//
//        AlertDialog f = (AlertDialog) getCurrentActivity().
//                getFragmentManager().findFragmentByTag(LoadingDialog.TAG);
//
//        isIdle = f == null;
//        if (isIdle)
//        {
//            resourceCallback.onTransitionToIdle();
//        }
//        return isIdle;
//    }
//
//    public Activity getCurrentActivity()
//    {
//        return  InstrumentationRegistry.
//                getTargetContext().getApplicationContext().getCurrentActivity();
//    }
//
//    @Override
//    public void registerIdleTransitionCallback(
//            ResourceCallback resourceCallback)
//    {
//        this.resourceCallback = resourceCallback;
//    }
//}
