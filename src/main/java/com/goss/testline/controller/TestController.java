package com.goss.testline.controller;

import com.goss.testline.user.UserInfoService;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import lombok.RequiredArgsConstructor;

//@LineMessageHandler
@RequiredArgsConstructor
public class TestController {

  private final UserInfoService userInfoService;

  @EventMapping
  public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
    String userId = event.getSource().getUserId();
    String localName = event.getMessage().getText();
    var userByLineId = userInfoService.findUserByLineId(userId);
    String returnMessage = userByLineId.map(user -> userInfoService.processQuestion(user, localName))
        .orElseGet(() -> userInfoService.processFirstQuestion(userId, localName));
    return new TextMessage(returnMessage);
  }

}