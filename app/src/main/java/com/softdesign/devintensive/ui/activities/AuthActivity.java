package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + " Auth Activity";


    @BindView(R.id.auth_login_btn) Button mSignInBtn;
    @BindView(R.id.auth_remember_txt) TextView mRememberPassword;
    @BindView(R.id.auth_login_email_et) EditText mLogin;
    @BindView(R.id.auth_login_password_et) EditText mPassword;
    @BindView(R.id.auth_coordinator_layout) CoordinatorLayout mCoordinatorLayout;

    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();

        mSignInBtn.setOnClickListener(this);
        mRememberPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.auth_login_btn:

                signIn();
                break;

            case R.id.auth_remember_txt:
                rememberPassword();
                break;
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void rememberPassword() {
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.devintensive_forgot_pass_url)));
        startActivity(rememberIntent);
    }

    private void loginSuccess(UserModelRes userModel) {
        mDataManager.getPreferencesManager().saveAuthToken(
                userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(
                userModel.getData().getUser().getId());

        saveUserValues(userModel);
        saveUserProfileInfoFields(userModel);
        saveUserPersonalData(userModel);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void signIn() {
        if (!NetworkStatusChecker.isNetworkAvailable(this)) {
            showSnackbar("Ошибка подключения к сети, попробуте позже");
        }


        UserLoginReq loginReq = new UserLoginReq(mLogin.getText().toString(),
                mPassword.getText().toString());

        Call<UserModelRes> call = mDataManager.loginUser(loginReq);
        call.enqueue(new Callback<UserModelRes>() {
            @Override
            public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                if (response.code() == 200) {
                    loginSuccess(response.body());
                } else if (response.code() == 404) {
                    showSnackbar("неверный логин или пароль");
                } else {
                    showSnackbar("ошибка соединения");
                }
            }

            @Override
            public void onFailure(Call<UserModelRes> call, Throwable t) {
                // TODO: processing errors
            }
        });
    }

    /* Saves user git rates to shared preferences. */
    private void saveUserValues(UserModelRes userModel) {
        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRaiting(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects(),
        };

        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }

    /* Saves name of user, user photo and avatar. */
    private void saveUserPersonalData(UserModelRes userModelRes) {
        String firstName = userModelRes.getData().getUser().getFirstName();
        String secondName = userModelRes.getData().getUser().getSecondName();

        mDataManager.getPreferencesManager().saveUserNames(firstName, secondName);

        UserModelRes.PublicInfo publicInfo = userModelRes.getData().getUser().getPublicInfo();
        if (publicInfo != null) {
            String photo = publicInfo.getPhoto();
            String avatar = publicInfo.getAvatar();
            if (photo != null) {
                mDataManager.getPreferencesManager().saveUserPhoto(Uri.parse(photo));
            }

            if (avatar != null) {
                mDataManager.getPreferencesManager().saveUserAvatar(Uri.parse(avatar));
            }
        }
    }

    /* Returns value string if it's not null and returns clear string in other case. */
    private String getValueOrClear(String str) {
        if (str != null) return str;

        return "";
    }

    /* Saves phone number, email, vk and git addresses,
       about user info to shared preferences. */
    private void saveUserProfileInfoFields(UserModelRes userModelRes) {
        ArrayList<String> userProfileData = new ArrayList<>();

        String tempVal = null;
        UserModelRes.Contacts contacts = userModelRes.getData().getUser().getContacts();
        if (contacts != null) {
            // Get phone number
            tempVal = contacts.getPhone();
            userProfileData.add(getValueOrClear(tempVal));
            // Get email
            tempVal = contacts.getEmail();
            userProfileData.add(getValueOrClear(tempVal));
            // Get vk
            tempVal = contacts.getVk();
            userProfileData.add(getValueOrClear(tempVal));
        } else {
            for (int i = 0; i < 3; i++) {
                userProfileData.add("");
            }
        }

        // Get git address
        tempVal = null;
        UserModelRes.Repositories reps = userModelRes.getData().getUser().getRepositories();
        if (reps != null) {
            UserModelRes.Repo repo = reps.getRepositoriesList().get(0);
            if (repo != null) tempVal = repo.getGit();
        }
        userProfileData.add(getValueOrClear(tempVal));

        // Get bio
        tempVal = null;
        UserModelRes.PublicInfo publicInfo = userModelRes.getData().getUser().getPublicInfo();
        if (publicInfo != null) {
            tempVal = publicInfo.getBio();
        }
        userProfileData.add(getValueOrClear(tempVal));

        mDataManager.getPreferencesManager().saveUserProfileData(userProfileData);
    }
}
