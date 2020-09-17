package com.example.services.chatSocket;

public interface ConnectionListener {
    public void onConnected();

    public void onDisconnected();

    public void onConnectError();
}