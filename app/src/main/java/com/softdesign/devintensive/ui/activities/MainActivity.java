package com.softdesign.devintensive.ui.activities;


import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.ProfileDataValidator;
import com.softdesign.devintensive.utils.RoundedAvatarDrawable;
import com.softdesign.devintensive.utils.TextValidator;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + " Main Activity";

    private DataManager mDataManager;
    /** Edit mode. It's 1 if data is modifying and 0 in other case. */
    private int mCurrentEditMode = 0;

    /** The main group of components. */
    @BindView(R.id.main_coordinator_container) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.navigation_drawer) DrawerLayout mNavigationDrawer;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.profile_placeholder) RelativeLayout mProfilePlaceholder;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.appbar_layout) AppBarLayout mAppBarLayout;
    @BindView(R.id.user_photo_img) ImageView mProfileImage;

    private ImageView mUserAvatar;
    @BindView(R.id.navigation_view) NavigationView mNavigationView;

    /** Icons in the items on the main activity for calling intents. */
    @BindView(R.id.call_phone_icon) ImageView mCallPhoneIcon;
    @BindView(R.id.send_mail_msg_icon) ImageView mSendMailMessageIcon;
    @BindView(R.id.view_vk_profile_icon) ImageView mViewVkProfileIcon;
    @BindView(R.id.view_github_profile_icon) ImageView mViewGithubProfileIcon;

    /** EditText components for input / show profile user information. */
    @BindViews({R.id.phone_et, R.id.mail_et, R.id.vk_et, R.id.github_et, R.id.about_et})
    List<EditText> mUserInfoViews;

    private AppBarLayout.LayoutParams mAppBarParams;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();

        View headerLayout = mNavigationView.getHeaderView(0);
        mUserAvatar = (ImageView)headerLayout.findViewById(R.id.drawer_header_avatar);

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);
        mCallPhoneIcon.setOnClickListener(this);
        mSendMailMessageIcon.setOnClickListener(this);
        mViewVkProfileIcon.setOnClickListener(this);
        mViewGithubProfileIcon.setOnClickListener(this);

        setupToolbar();
        setupDrawer();
        roundAvatar();
        loadUserInfoValue();
        insertProfileImage(mDataManager.getPreferencesManager().loadUserPhoto(), false);


        if (savedInstanceState == null) {
            // the first start of this activity
        } else {
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                if (mCurrentEditMode == 0) {
                    changeEditMode(1);
                    mCurrentEditMode = 1;
                } else {
                    changeEditMode(0);
                    mCurrentEditMode = 0;
                }
                break;

            case R.id.profile_placeholder:
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO_DIALOG);
                break;
            case R.id.view_vk_profile_icon:
                viewVkProfile();
                break;
            case R.id.view_github_profile_icon:
                viewGithubProfile();
                break;
            case R.id.call_phone_icon:
                callPhoneNumber();
                break;
            case R.id.send_mail_msg_icon:
                sendMailMessage();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }


    @Override
    public void onBackPressed() {

        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();
                    
                    insertProfileImage(mSelectedImage, true);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    insertProfileImage(mSelectedImage, true);
                }
        }
    }

    /** Makes avatar on NavigationView like rounded. */
    protected void roundAvatar() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = false;
        Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.user_photo, options);
        RoundedAvatarDrawable roundedAvatarDrawable = new RoundedAvatarDrawable(avatar);
        mUserAvatar.setImageDrawable(roundedAvatarDrawable);
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        showSnackbar(item.getTitle().toString());
                        item.setChecked(true);
                        mNavigationDrawer.closeDrawer(GravityCompat.START);
                        return false;
                    }
                });
    }

    private void changeEditMode(int mode) {
        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);

                showProfilePlaceholder();
                lockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
            }
            mUserInfoViews.get(0).requestFocus();
        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);

                hideProfilePlaceholder();
                unlockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
            }
            saveUserInfoValue();
        }
    }

    private void loadUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    private void saveUserInfoValue() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void loadPhotoFromGallery() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent,
                getString(R.string.user_profile_image_choose_msg)),
                ConstantManager.REQUEST_GALLERY_PICTURE);
    }

    private void loadPhotoFromCamera() {

        boolean permissions = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (permissions) {

            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                showSnackbar("Не удалось загрузить фото.");
            }

            if (mPhotoFile != null) {
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            tryRequestPermissions(new String[] {
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            }, ConstantManager.CAMERA_REQUEST_PERMISSIONS_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSIONS_CODE &&
                grantResults.length == 2) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO: process permission errors here
            }

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO: process permission errors here
            }
        }
    }

    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    private void unlockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    private Dialog createLoadProfilePhotoDialog() {
        String[] selectItems = {
                getString(R.string.user_profile_dialog_gallery),
                getString(R.string.user_profile_dialog_camera),
                getString(R.string.user_profile_dialog_cancel)
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.user_profile_dialog_title);
        builder.setItems(selectItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int choiceItem) {
                switch (choiceItem) {
                    case 0:
                        loadPhotoFromGallery();
                        break;
                    case 1:
                        loadPhotoFromCamera();
                        break;
                    case 2:
                        dialogInterface.cancel();
                        break;
                }
            }
        });
        return builder.create();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO_DIALOG:
                return createLoadProfilePhotoDialog();
            default:
                return null;

        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return image;
    }

    private void insertProfileImage(Uri selectedImage, boolean isSaveToPreferences) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);

        if (isSaveToPreferences) {
            mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
        }
    }

    private void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);

    }

    private void tryRequestPermissions(String[] requiredPermissions, int requestCode) {
        ActivityCompat.requestPermissions(this, requiredPermissions, requestCode);
        Snackbar.make(mCoordinatorLayout,
                getString(R.string.user_profile_app_permessions_request),
                Snackbar.LENGTH_LONG).setAction("Разрешить", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openApplicationSettings();
            }
        }).show();
    }

    private void viewVkProfile() {
        String link = mDataManager.getPreferencesManager().getVkProfileAddress();
        Uri address = Uri.parse("https://" + link);
        Intent openVkProfileIntent = new Intent(Intent.ACTION_VIEW, address);
        startActivity(openVkProfileIntent);
    }

    private void viewGithubProfile() {
        String link = mDataManager.getPreferencesManager().getGitProfileAddress();
        Uri address = Uri.parse("https://" + link);
        Intent openGitProfileIntent = new Intent(Intent.ACTION_VIEW, address);
        startActivity(openGitProfileIntent);
    }

    private void callPhoneNumber() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) ==
                PackageManager.PERMISSION_GRANTED) {
            String phoneNumber = mDataManager.getPreferencesManager().getUserPnoneNumber();
            Uri callUri = Uri.parse("tel:" + phoneNumber);
            Intent callPhoneIntent = new Intent(Intent.ACTION_CALL, callUri);
            startActivity(callPhoneIntent);
        } else {
            tryRequestPermissions(new String[] {Manifest.permission.CALL_PHONE},
                    ConstantManager.CALL_PHONE_PERMISSIONS_CODE);
        }
    }

    private void sendMailMessage() {
        String mailAddress = mDataManager.getPreferencesManager().getMailAddress();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, mailAddress);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Content");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (ActivityNotFoundException exc) {
            showSnackbar("Приложений для работы с email почтой не найдено");
        }
    }

}
