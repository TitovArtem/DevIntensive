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
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity implements View.OnClickListener {

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

    private void saveUserValues(UserModelRes userModel) {
        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRaiting(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects(),
        };

        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }
}
