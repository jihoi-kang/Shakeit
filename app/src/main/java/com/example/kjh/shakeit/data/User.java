package com.example.kjh.shakeit.data;

import java.io.Serializable;

/**
 * 사용자의 정보를 담는 데이터 클래스
 * @author 강지회
 * @version 1.0.0
 * @since 2019. 4. 26. PM 5:47
 **/
public class User implements Serializable {

    private int _id;
    private String email;
    private String login_type;
    private String name;
    private String image_url;
    private String status_message;
    private int cash;
    private String device_token;

    /** Setter */
    public void set_id(int _id) {
        this._id = _id;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }
    public void setCash(int cash) {
        this.cash = cash;
    }
    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    /** Getter */
    public int get_id() {
        return _id;
    }
    public String getEmail() {
        return email;
    }
    public String getLogin_type() {
        return login_type;
    }
    public String getName() {
        return name;
    }
    public String getImage_url() {
        return image_url;
    }
    public String getStatus_message() {
        return status_message;
    }
    public int getCash() {
        return cash;
    }
    public String getDevice_token() {
        return device_token;
    }


}
