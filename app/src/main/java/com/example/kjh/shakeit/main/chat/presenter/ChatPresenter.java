package com.example.kjh.shakeit.main.chat.presenter;

import android.Manifest;
import android.os.Message;

import com.example.kjh.shakeit.R;
import com.example.kjh.shakeit.api.ResultCallback;
import com.example.kjh.shakeit.data.ChatHolder;
import com.example.kjh.shakeit.data.ChatRoom;
import com.example.kjh.shakeit.data.MessageHolder;
import com.example.kjh.shakeit.data.ReadHolder;
import com.example.kjh.shakeit.data.User;
import com.example.kjh.shakeit.main.chat.contract.ChatContract;
import com.example.kjh.shakeit.otto.BusProvider;
import com.example.kjh.shakeit.otto.Events;
import com.example.kjh.shakeit.utils.Serializer;
import com.example.kjh.shakeit.utils.StrUtil;
import com.example.kjh.shakeit.utils.TimeManager;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.kjh.shakeit.main.chat.ChatActivity.chatActHandler;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.CONN_WEBRTC;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.DELIVERY;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.MESSAGE;
import static com.example.kjh.shakeit.netty.protocol.ProtocolHeader.UPDATE_UNREAD;

public class ChatPresenter implements ChatContract.Presenter {

    private final String TAG = ChatPresenter.class.getSimpleName();

    private ChatContract.View view;
    private ChatContract.Model model;

    private ArrayList<ChatHolder> chats;

    public ChatPresenter(ChatContract.View view, ChatContract.Model model) {
        this.view = view;
        this.model = model;

        chats = new ArrayList<>();
    }

    /**------------------------------------------------------------------
     생명주기 ==> onCreate()
     ------------------------------------------------------------------*/
    @Override
    public void onCreate() {
        BusProvider.getInstance().register(this);

        User user = view.getUser();
        ChatRoom room = view.getChatRoom();

        /** 채팅 목록 */
        getChatList();

        /** 읽지않은 메시지 읽었다고 알림 */
        model.updateUnreadChat(user.getUserId(), room);
    }

    /**------------------------------------------------------------------
     생명주기 ==> onDestroy()
     ------------------------------------------------------------------*/
    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
    }

    /**------------------------------------------------------------------
     메서드 ==> 텍스트 메시지 전송 로직
     ------------------------------------------------------------------*/
    @Override
    public void onClickSend() {
        String content = view.getInputContent();
        User user = view.getUser();
        ChatRoom room = view.getChatRoom();
        String time = TimeManager.nowTime();

        if(StrUtil.isBlank(content))
            return;

        /** 참가자들의 아이디들 => JSONArray */
        JSONArray unreadUsers = new JSONArray();
        for(int index = 0; index < room.getParticipants().size(); index++)
            unreadUsers.put(room.getParticipants().get(index).getUserId());

        room.setChatHolder(
                new ChatHolder(0, room.getRoomId(), user.getUserId(), "text", content, time, unreadUsers.toString(), true)
        );
        String body = Serializer.serialize(room);

        model.sendMessage(body);
        view.clearInputContent();
    }

    /**------------------------------------------------------------------
     메서드 ==> 채팅 목록
     ------------------------------------------------------------------*/
    private void getChatList() {
        ChatRoom room = view.getChatRoom();
        chats.clear();

        model.getChatList(room.getRoomId(), new ResultCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    JSONArray jsonArray = new JSONArray(body);
                    for(int index = 0; index < jsonArray.length(); index++) {
                        ChatHolder holder = Serializer.deserialize(jsonArray.getString(index), ChatHolder.class);
                        chats.add(holder);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                view.showChatList(chats);
            }
            @Override
            public void onFailure(String errorMsg) {}
        });
    }

    /**------------------------------------------------------------------
     메서드 ==> 텍스트 외 메시지를 보내려고 add 눌렀을 때
     ------------------------------------------------------------------*/
    @Override
    public void onClickAttach() {
        view.showSelectType();
    }

    /**------------------------------------------------------------------
     메서드 ==> 영상통화 연결
     ------------------------------------------------------------------*/
    @Override
    public void toCall(String roomID) {
        TedPermission.with(view.getContext())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        String type = "receiver";
                        String finalRoomID = roomID;
                        if(StrUtil.isBlank(finalRoomID)) {
                            int random = new Random().nextInt(99999999);
                            finalRoomID = "shake" + random;
                            type = "sender";
                        }
                        view.connectToRoom(
                                finalRoomID,
                                false,
                                false,
                                false,
                                0,
                                type
                        );
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                    }
                })
                .setDeniedTitle(R.string.permission_denied_title)
                .setDeniedMessage(R.string.permission_denied_message)
                .setGotoSettingButtonText(R.string.tedpermission_setting)
                .setPermissions(Manifest.permission.INTERNET, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                .check();
    }

    /**------------------------------------------------------------------
     구독이벤트 ==> Netty에서 이벤트 왔을 때 ==> 메시지 받거나 콜백
     ------------------------------------------------------------------*/
    @Subscribe
    public void nettyEvent (Events.nettyEvent event) {
        MessageHolder holder = event.getMessageHolder();

        if(holder.getType() == MESSAGE) {
            ChatRoom room = Serializer.deserialize(holder.getBody(), ChatRoom.class);

            /** 채팅방 처음 생성되고 채팅 메시지 보냈을 때 */
            if(view.getChatRoom().getRoomId() == 0)
                view.setChatRoomId(room.getRoomId());

            if(view.getChatRoom().getRoomId() != room.getRoomId())
                return;

            if(holder.getSign() == DELIVERY)
                model.updateUnreadChat(view.getUser().getUserId(), view.getChatRoom());

            chats.add(room.getChatHolder());
        } else if(holder.getType() == UPDATE_UNREAD) {
            ReadHolder readHolder = Serializer.deserialize(holder.getBody(), ReadHolder.class);

            /** 읽은 사용자들을 UnreadArray에서 제거 */
            for(int index = 0; index < readHolder.getChatIds().size(); index++) {
                for(int chatsIdx = 0; chatsIdx < chats.size(); chatsIdx++) {
                    if(chats.get(chatsIdx).getChatId() == readHolder.getChatIds().get(index)) {
                        ChatHolder chatHolder = chats.get(chatsIdx);

                        JSONArray unreadUsers = null;
                        try {
                            unreadUsers = new JSONArray(chatHolder.getUnreadUsers());
                            for(int unreadIdx = 0; unreadIdx < unreadUsers.length(); unreadIdx++) {
                                if(unreadUsers.getInt(unreadIdx) == readHolder.getUserId())
                                    unreadUsers.remove(unreadIdx);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        chatHolder.setUnreadUsers(unreadUsers.toString());
                        chats.set(chatsIdx, chatHolder);
                    }
                }
            }
        } else if(holder.getType() == CONN_WEBRTC) {
            String roomID = null;
            try {
                JSONObject jsonObject = new JSONObject(holder.getBody());
                roomID = jsonObject.getString("roomID");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            view.goCallWaitActivity(roomID);
            return;
        }

        Message msg = chatActHandler.obtainMessage();
        msg.obj = chats;
        chatActHandler.sendMessage(msg);
    }
}
