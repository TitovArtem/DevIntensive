package com.softdesign.devintensive.data.managers;


import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserModelRes;

import retrofit2.Call;

public class DataManager {
    private static DataManager INSTANCE = null;

    private PreferencesManager mPreferencesManager;
    private RestService mRestService = ServiceGenerator.createService(RestService.class);

    public DataManager() {
        mPreferencesManager = new PreferencesManager();
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    public void setPreferencesManager(PreferencesManager preferencesManager) {
        mPreferencesManager = preferencesManager;
    }

    //region ============= Network =============

    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq) {
        return mRestService.loginUser(userLoginReq);
    }

    //endregion


    //region ============= Database =============
    //endregion
}
