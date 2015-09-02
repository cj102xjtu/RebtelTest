package com.example.junc.rebteltest;

import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.test.InstrumentationTestCase;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;

import org.junit.Test;


/**
 * Created by JunC on 2015-08-30.
 */


public class UiAutomatorTest extends InstrumentationTestCase {

    static private String LOG_TAG = "UiAutomatorTest";
    static private String REBTEL = "Rebtel";
    static private String PHONE_NUMBER = "0739527959";
    static private String PIN = "7887";
    static private String DIALING_NUMBER = "+465643678";
    static private String ZERO_BTN_ID = "com.rebtel.android:id/button11";
    static private String DIAL_BTN_ID_PREFIX = "com.rebtel.android:id/button";

    private UiDevice mDevice;


    public static class CommonSelector {
        public static final UiSelector MENU_BUTTON = new UiSelector().description("Open navigation drawer");
    }

    public static class LauncherHelper {
        public static final UiSelector ALL_APPS_BUTTON = new UiSelector().description("Apps");
        public static final UiSelector LAUNCHER_CONTAINER = new UiSelector().scrollable(true);
        public static final UiSelector LAUNCHER_ITEM =
                new UiSelector().className(android.widget.TextView.class.getName());
    }

    public static class LoginHelper{
        public static final UiSelector FIRST_LOGIN_BUTTON = new UiSelector().resourceId("com.rebtel.android:id/loginFlowButton");
        public static final UiSelector LOGIN_BUTTON = new UiSelector().resourceId("com.rebtel.android:id/auth_button");
        public static final UiSelector NUMBER_EDITOR = new UiSelector().resourceId("com.rebtel.android:id/loginPhoneNumber");
        public static final UiSelector PIN_EDITOR = new UiSelector().resourceId("com.rebtel.android:id/loginEnterPin");
        public static final UiSelector NEXT_BUTTON = new UiSelector().resourceId("com.rebtel.android:id/btnLoginFragmentNext");
    }

    public static class LogoutHelper{
        public static final UiSelector ACCOUNT_BUTTON = new UiSelector().resourceId("com.rebtel.android:id/drawerMenuItem").index(6);
        public static final UiSelector LOGOUT_BUTTON = new UiSelector().className("android.widget.RelativeLayout").index(4);
        public static final UiSelector OK_BUTTON = new UiSelector().resourceId("android:id/button1");
    }

    public static class DialNumberHelper{
        public static final UiSelector DIALPAD_BUTTON = new UiSelector().resourceId("com.rebtel.android:id/drawerMenuItem").index(4);
        public static final UiSelector CALL_BUTTON = new UiSelector().resourceId("com.rebtel.android:id/button14");
        public static final UiSelector END_CALL = new UiSelector().resourceId("com.rebtel.android:id/hangupButton");
        public static final UiSelector FIRST_CALL_COMFIRM_BUTTON =  new UiSelector().resourceId("com.rebtel.android:id/next_button");
    }

    public static class CallSwitchHelper{
        public static final UiSelector CALL_SWITCH_BUTTON = new UiSelector().description("Call switch");
        public static final UiSelector WIFI_CALL_BUTTON = new UiSelector().resourceId("com.rebtel.android:id/relContainerTravelMode");
    }

    public static class RecentCallHelper{
        public static final UiSelector RECENT_BUTTON = new UiSelector().resourceId("com.rebtel.android:id/drawerMenuItem").index(3);
        public static final UiSelector RECENT_LIST = new UiSelector().resourceId("com.rebtel.android:id/listview_calllog");
        public static final UiSelector FIRST_ITEM = new UiSelector().index(0).className(android.widget.RelativeLayout.class);
        public static final UiSelector PHONE_NUMBER = new UiSelector().resourceId("com.rebtel.android:id/phoneNumber");

    }

    @Override
    protected void setUp() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(getInstrumentation());
        try {
            launchApp(REBTEL);
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "setUp(): can't launch Rebtel. Error: " + e.getMessage());
            fail("Can't launch Rebtel.");
        }
    }

    @Override
    protected void tearDown() throws Exception {
        logout();
        mDevice.pressHome();
        super.tearDown();
    }

    @Test
    public void testMakeCallAndCheckCallLog()  {

        try {
            login(PHONE_NUMBER, PIN);

            setWifiCallMode();

            dialNumber(DIALING_NUMBER);

            gotoRecentPage();

            checkFirstRecentCall();

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "testCallInRecent(): Object not found. Error: " + e.getMessage());
            fail("Object not found.Error: " + e.getMessage());
        }
    }

    private void gotoRecentPage() throws UiObjectNotFoundException{
        // Goto Menu page
        UiObject menuBtn = mDevice.findObject(CommonSelector.MENU_BUTTON);
        menuBtn.click();

        // Open recent page
        UiObject recentBtn = mDevice.findObject(RecentCallHelper.RECENT_BUTTON);
        recentBtn.click();
    }

    private void checkFirstRecentCall() throws UiObjectNotFoundException{
        // Get the first call log item phone number view
        UiScrollable recentCallList = new UiScrollable(RecentCallHelper.RECENT_LIST);
        UiObject firstCallLog = recentCallList.getChildByInstance(RecentCallHelper.FIRST_ITEM, 1);
        UiObject phoneNumberView = firstCallLog.getChild(RecentCallHelper.PHONE_NUMBER);

        // Check the result
        assertEquals(DIALING_NUMBER, phoneNumberView.getText());
    }

    private void setWifiCallMode() throws UiObjectNotFoundException {
        UiObject callSwitchBtn = mDevice.findObject(CallSwitchHelper.CALL_SWITCH_BUTTON);
        callSwitchBtn.click();

        UiObject wifiCallBtn = mDevice.findObject(CallSwitchHelper.WIFI_CALL_BUTTON);
        wifiCallBtn.click();
    }

    private void dialNumber(String number) throws UiObjectNotFoundException {
        // Goto Menu page
        UiObject menuBtn = mDevice.findObject(CommonSelector.MENU_BUTTON);
        menuBtn.click();

        // Open dial pad
        UiObject dialPadBtn = mDevice.findObject(DialNumberHelper.DIALPAD_BUTTON);
        dialPadBtn.click();

        // Make a call
        dialingNumberWithDialPad(number);


        // End call
        UiObject endCallBtn = mDevice.findObject(DialNumberHelper.END_CALL);
        try{
            endCallBtn.click();
        } catch (UiObjectNotFoundException e){
            Log.i(LOG_TAG, "dialNumber(): try to end the first call");
            endTheFirstCall();
        }
    }

    private void endTheFirstCall() throws UiObjectNotFoundException
    {
        // Comfirm the first call
        UiObject firstCallButton = mDevice.findObject(DialNumberHelper.FIRST_CALL_COMFIRM_BUTTON);
        firstCallButton.click();

        // End call
        UiObject endCallBtn = mDevice.findObject(DialNumberHelper.END_CALL);
        endCallBtn.click();
    }

    private void dialingNumberWithDialPad(String number) throws UiObjectNotFoundException{
        // Dial phone number
        for (char phoneNumber : number.toCharArray()) {
            if (phoneNumber == '+') {
                UiObject zeroButton = mDevice.findObject(new UiSelector().resourceId(ZERO_BTN_ID));
                zeroButton.longClick();
            } else {
                dialingDigitalNumberWithDialPad(phoneNumber);
            }
        }

        // Make a call
        UiObject callButton = mDevice.findObject(DialNumberHelper.CALL_BUTTON);
        callButton.clickAndWaitForNewWindow();
    }

    private void dialingDigitalNumberWithDialPad(char digital) throws UiObjectNotFoundException {
        String resourceId = "";
        if (digital == '0') {
            resourceId = ZERO_BTN_ID;
        } else {
            resourceId = DIAL_BTN_ID_PREFIX + digital;
        }

        UiObject digitalButton = mDevice.findObject(new UiSelector().resourceId(resourceId));
        digitalButton.click();
    }

    private void logout() throws UiObjectNotFoundException{
        // Goto Menu page
        UiObject menuBtn = mDevice.findObject(CommonSelector.MENU_BUTTON);
        menuBtn.click();

        // Goto Account page
        UiObject accountBtn = mDevice.findObject(LogoutHelper.ACCOUNT_BUTTON);
        accountBtn.click();

        // Logout
        UiObject logoutBtn = mDevice.findObject(LogoutHelper.LOGOUT_BUTTON);
        logoutBtn.click();

        UiObject okBtn = mDevice.findObject(LogoutHelper.OK_BUTTON);
        okBtn.click();
    }


    private void login(String userName, String passWord) throws UiObjectNotFoundException {

        try {
            // Goto Login page
            UiObject loginButton = mDevice.findObject(LoginHelper.LOGIN_BUTTON);
            loginButton.click();
        } catch (UiObjectNotFoundException e) {
            // Do the first time login.
            UiObject firstLoginBtn = mDevice.findObject(LoginHelper.FIRST_LOGIN_BUTTON);
            firstLoginBtn.click();
            Log.i(LOG_TAG, "login(): First time login");
        }

        //Config account
        UiObject phoneNumberEditor = mDevice.findObject(LoginHelper.NUMBER_EDITOR);
        phoneNumberEditor.setText(userName);

        UiObject pinEditor = mDevice.findObject(LoginHelper.PIN_EDITOR);
        pinEditor.setText(passWord);

        UiObject nextBtn = mDevice.findObject(LoginHelper.NEXT_BUTTON);
        nextBtn.clickAndWaitForNewWindow();
    }

    private void launchApp(String nameOfAppToLaunch) throws UiObjectNotFoundException {
        // Start from here
        mDevice.pressHome();

        // Open the All Apps view
        UiObject allAppsButton =  mDevice.findObject(LauncherHelper.ALL_APPS_BUTTON);
        allAppsButton.click();

        UiScrollable appViews = new UiScrollable(LauncherHelper.LAUNCHER_CONTAINER);
        // Set the swiping mode to horizontal (the default is vertical)
        appViews.setAsHorizontalList();
        appViews.scrollToBeginning(10);  // Otherwise the Apps may be on a later page of apps.
        int maxSearchSwipes = appViews.getMaxSearchSwipes();

        UiObject appToLaunch;
        // The following loop is to workaround a bug in Android 4.2.2 which
        // fails to scroll more than once into view.
        for (int i = 0; i < maxSearchSwipes; i++) {

            try {
                appToLaunch = appViews.getChildByText(LauncherHelper.LAUNCHER_ITEM, nameOfAppToLaunch);
                if (appToLaunch != null) {
                    // Create a UiSelector to find the Settings app and simulate
                    // a user click to launch the app.
                    appToLaunch.clickAndWaitForNewWindow();
                    break;
                }
            } catch (UiObjectNotFoundException e) {
                System.out.println("Did not find match for " + e.getLocalizedMessage());
            }

            for (int j = 0; j < i; j++) {
                appViews.scrollForward();
                System.out.println("scrolling forward 1 page of apps.");
            }
        }
    }


}
