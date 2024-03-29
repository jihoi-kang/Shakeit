package com.example.kjh.shakeit.login.contract;

import com.example.kjh.shakeit.api.ResultCallback;

public interface EmailLoginContract {

    interface View {

        String getInputEmail();
        void showMessageForIncorrectEmail();
        String getInputPassword();
        void showMessageForIncorrectPassword();
        void hideSoftKeyboard();
        void showLoadingDialog();
        void hideLoadingDialog();
        void showMessageForSuccessLoginAndFinishActivity(String body);
        void showMessageForFailureLogin(String errorMsg);

    }

    interface Presenter {

        void onClickEmailLogin();

    }

    interface Model {

        void login(String email, String password, ResultCallback callback);

    }

}
