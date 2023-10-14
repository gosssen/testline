package com.goss.testline;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.messaging.model.Message;
import com.linecorp.bot.messaging.model.TextMessage;
import com.linecorp.bot.model.PushMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageUtil {
    @Value("${line.bot.channelToken}")
    String channelToken;
    public void pushMessage(String notifyId, String content) {
        Message message = new TextMessage(content);
//        PushMessage pushMessage = new PushMessage(notifyId, message);
        LineMessagingClient build = LineMessagingClient.builder(channelToken).build();
//        build.pushMessage(pushMessage);
    }
}