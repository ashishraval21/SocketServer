package com.niki.server;

import java.io.Serializable;

/**
 * Created by songline on 16/12/16.
 */
public class ClientMessage implements Serializable{
private static final long serialVersionUID = 5950169519310163575L;
    private String id;
    private String message;
    private String type;
    private String senderId;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String toString() {
        return "Id = " + getId() + " ; Name = " + getMessage();
    }


}


