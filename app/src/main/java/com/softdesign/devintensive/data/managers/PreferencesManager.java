package com.softdesign.devintensive.data.managers;


import android.content.SharedPreferences;
import android.net.Uri;

import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevintensiveApplication;

import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {

    private SharedPreferences mSharedPreferences;

    /** Default value for uninitialized shared preferences. */
    public static final String NONE_VALUE = "null";

    private static final String[] USER_FIELDS = {
            ConstantManager.USER_PHONE_KEY,
            ConstantManager.USER_MAIL_KEY,
            ConstantManager.USER_GIT_KEY,
            ConstantManager.USER_VK_KEY,
            ConstantManager.USER_BIO_KEY
    };

    public PreferencesManager() {
        this.mSharedPreferences = DevintensiveApplication.getSharedPreferences();
    }

    /** Saves user profile data to shared preferences.
     * @param userFields User profile data. It has 5 elements and the strict order of this elements:
     *                    user phone number
     *                   ({@link com.softdesign.devintensive.utils.ConstantManager#USER_PHONE_KEY}),
     *                    user mail address
     *                   ({@link com.softdesign.devintensive.utils.ConstantManager#USER_MAIL_KEY}),
     *                    link to user vk.com profile
     *                   ({@link com.softdesign.devintensive.utils.ConstantManager#USER_VK_KEY}),
     *                    link to user github.com profile
     *                   ({@link com.softdesign.devintensive.utils.ConstantManager#USER_GIT_KEY}),
     *                    information about user
     *                   ({@link com.softdesign.devintensive.utils.ConstantManager#USER_BIO_KEY}),
     */
    public void saveUserProfileData(List<String> userFields) {
        if (userFields.size() != 5) {
            throw new IllegalArgumentException("The given list of user data " +
                    "has to contain 5 elements");
        }

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i = 0; i < USER_FIELDS.length; i++) {
            editor.putString(USER_FIELDS[i], userFields.get(i));
        }
        editor.apply();
    }

    /** @return The list of profile data fields. */
    public List<String> loadUserProfileData() {
        List<String> userFields = new ArrayList<>();
        for (int i = 0; i < USER_FIELDS.length; i++) {
            userFields.add(mSharedPreferences.getString(USER_FIELDS[i], NONE_VALUE));
        }

        return userFields;
    }

    public String getUserPnoneNumber() {
        return mSharedPreferences.getString(ConstantManager.USER_PHONE_KEY, NONE_VALUE);
    }

    public String getMailAddress() {
        return mSharedPreferences.getString(ConstantManager.USER_MAIL_KEY, NONE_VALUE);
    }

    public String getVkProfileAddress() {
        return mSharedPreferences.getString(ConstantManager.USER_VK_KEY, NONE_VALUE);
    }

    public String getGitProfileAddress() {
        return mSharedPreferences.getString(ConstantManager.USER_GIT_KEY, NONE_VALUE);
    }

    public String getAboutProfileInfo() {
        return mSharedPreferences.getString(ConstantManager.USER_BIO_KEY, NONE_VALUE);
    }

    public void saveUserPhoto(Uri uri) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY, uri.toString());
        editor.apply();
    }

    public Uri loadUserPhoto() {
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY,
                "android.resource://com.softdesign.devintensive/drawable/user_photo"));
    }
}
