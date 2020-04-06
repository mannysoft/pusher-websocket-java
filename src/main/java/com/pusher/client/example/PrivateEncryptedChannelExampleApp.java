package com.pusher.client.example;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateEncryptedChannel;
import com.pusher.client.channel.PrivateEncryptedChannelEventListener;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

public class PrivateEncryptedChannelExampleApp implements
        ConnectionEventListener, PrivateEncryptedChannelEventListener {

    private String apiKey = "FILL_ME_IN"; // "key" at https://dashboard.pusher.com
    private String channelName = "private-encrypted-channel";
    private String eventName = "my-event";
    private String cluster = "eu";

    private PrivateEncryptedChannel channel;

    public static void main(final String[] args) {
        new PrivateEncryptedChannelExampleApp(args);
    }

    private PrivateEncryptedChannelExampleApp(final String[] args) {
        switch (args.length) {
            case 4: cluster = args[3];
            case 3: eventName = args[2];
            case 2: channelName = args[1];
            case 1: apiKey = args[0];
        }

        final HttpAuthorizer authorizer = new HttpAuthorizer(
                "http://localhost:3030/pusher/auth");
        final PusherOptions options = new PusherOptions().setAuthorizer(authorizer).setEncrypted(true);
        options.setCluster(cluster);

        Pusher pusher = new Pusher(apiKey, options);
        pusher.connect(this);

        channel = pusher.subscribePrivateEncrypted(channelName, this, eventName);

        // Keep main thread asleep while we watch for events or application will terminate
        while (true) {
            try {
                Thread.sleep(1000);
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAuthenticationFailure(String message, Exception e) {
        System.out.println(String.format(
                "Authentication failure due to [%s], exception was [%s]", message, e));
    }

    @Override
    public void onSubscriptionSucceeded(String channelName) {
        System.out.println(String.format(
                "Subscription to channel [%s] succeeded", channel.getName()));
    }

    @Override
    public void onEvent(PusherEvent event) {
        System.out.println(String.format(
                "Received event [%s]", event.toString()));
    }

    @Override
    public void onConnectionStateChange(ConnectionStateChange change) {
        System.out.println(String.format(
                "Connection state changed from [%s] to [%s]",
                change.getPreviousState(),
                change.getCurrentState()));
    }

    @Override
    public void onError(String message, String code, Exception e) {
        System.out.println(String.format(
                "An error was received with message [%s], code [%s], exception [%s]",
                message,
                code,
                e));
    }

    @Override
    public void onDecryptionFailure(Exception e) {
        System.out.println(String.format(
                "An error was received decrypting message - exception: [%s]", e));
    }
}
