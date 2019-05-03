package com.example.kjh.shakeit.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.data.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ProfileDetailActivity extends AppCompatActivity {

    private final String TAG = ProfileDetailActivity.class.getSimpleName();

    private User user;

    private Unbinder unbinder;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.profile_background_img) ImageView backgroundProfileImage;
    @BindView(R.id.email) TextView email;

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        unbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        name.setText(user.getName());
        email.setText(user.getEmail());

        if(user.getImage_url() == null || user.getImage_url().equals("")) {
            profileImage.setImageResource(R.drawable.ic_basic_profile);
            backgroundProfileImage.setImageResource(R.color.black);
        } else {
            Glide.with(this).load(user.getImage_url()).into(profileImage);
            Glide.with(this).load(user.getImage_url()).into(backgroundProfileImage);
        }

    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 1:1 채팅
     ------------------------------------------------------------------*/
    @OnClick(R.id.direct_chat)
    public void onClickDirectChat() {

    }

    /**------------------------------------------------------------------
     클릭이벤트 ==> 닫기
     ------------------------------------------------------------------*/
    @OnClick(R.id.close)
    public void onClose() {
        finish();
    }
}