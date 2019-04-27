package com.example.kjh.shakeit.main.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kjh.shakeit.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 친구목록 탭
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 27. PM 5:33
 **/
public class TabFriendListFragment extends Fragment {

    private static TabFriendListFragment instance = null;

    private Unbinder unbinder;

    private View view;

    public static TabFriendListFragment getInstance() {
        if(instance == null)
            instance = new TabFriendListFragment();

        return instance;
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreateView()
     ------------------------------------------------------------------*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;

    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroyView()
     ------------------------------------------------------------------*/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
