package com.goss.testline.user;

import com.goss.testline.group.Group;
import com.goss.testline.group.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author Kevin  on 2023/11/11
 */
@Service
@RequiredArgsConstructor
public class UserInfoService {
  private static final String NOT_FOUND_LOCAL = "查無此地區";
  private static final int FIRST_ASK_COUNT = 1;
  private final UserInfoRepository userInfoRepository;
  private final GroupRepository groupRepository;

  public Optional<UserInfo> findUserByLineId(String lineId) {
    return userInfoRepository.findById(lineId);
  }

  public String processFirstQuestion(String lineId, String localName) {
    List<Group> byNameLike = groupRepository.findByNameLikeAndLevel("%" +localName +"%",1);
    List<Group> byCount = groupRepository.findByLevel(1);
    if (byNameLike.isEmpty()) {
      return NOT_FOUND_LOCAL + "，目前可查詢地區為:\n" + byCount.stream().map(Group::getName).collect(Collectors.joining("\n"));
    } else {
      Optional<String> first = byNameLike.stream().map(Group::getName).filter(localName::equals).findFirst();
      if (first.isEmpty()) {
        return "目前無此地區,但有相近名稱:\n" + byNameLike.stream().map(Group::getName).collect(Collectors.joining("\n"));
      } else {
        UserInfo userInfo = new UserInfo();
        userInfo.setCount(FIRST_ASK_COUNT);
        userInfo.setAskLocalName(localName);
        userInfo.setLineId(lineId);
        userInfoRepository.save(userInfo);
        List<Group> byName = groupRepository.findByLastName(localName);
        return "下層列表地區為:\n" + byName.stream().map(Group::getName).collect(Collectors.joining("\n"));
      }
    }
  }

  public String processQuestion(UserInfo userInfo,String localName) {
    String lastAskLocalName = userInfo.getAskLocalName();
    Integer nextCount = userInfo.getCount() +1;
    List<Group> byNameLike = groupRepository.findByLastNameAndNameLikeAndLevel(lastAskLocalName, "%" +localName +"%",nextCount);
    if (byNameLike.isEmpty()) {
      return NOT_FOUND_LOCAL;
    } else {
      Optional<Group> first = byNameLike.stream().filter( group -> group.getName().equals(localName)).findFirst();
      boolean isNotNext = first.map(Group::getUrl)
          .map(value -> value.contains("id")).orElse(false);
      //最後一層了直接返回 並刪除過往查詢記錄
      if (isNotNext) {
        userInfoRepository.deleteById(userInfo.getLineId());
        return first.get().getUrl();
      }
      if (first.isEmpty()) {
        return "目前無此地區,但有相近名稱:\n" + byNameLike.stream().map(Group::getName).collect(Collectors.joining("\n"));
      } else {
        userInfo.setCount(nextCount);
        userInfo.setAskLocalName(localName);
        userInfoRepository.save(userInfo);
        List<Group> byName = groupRepository.findByLastName(localName);
        return "下層列表地區為:\n" + byName.stream().map(Group::getName).collect(Collectors.joining("\n"));
      }
    }
  }
}
