package com.streampublisher.recorder;

public interface PublishListener {
    void onPublishConnected();
    void onPublishReconnecting();
    void onPublishReconnected();
    void onPublishError();
}
