package com.goss.testline.config;

import com.goss.testline.group.Group;
import com.goss.testline.group.GroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author Kevin  on 2023/11/9
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InitData {
  private final GroupRepository groupRepository;
  public void recursiveCrawl(String url) {
    try {
      Document doc = Jsoup.connect(url).get();
      // 提取連結
      Elements links = doc.select("body a:not(nav a, header a)");
      List<Group> listGroup = new ArrayList<>();
      Set<String> set = new HashSet<>();
      for (Element link : links) {
        String linkHref = link.attr("href");
        //南投縣有重複,第二個是空的
        if (!set.add(link.text())) {
          Group group = new Group();
          group.setUrl(linkHref);
          group.setName(link.text());
          group.setLevel(1);
          listGroup.add(group);
        }
      }
      set.clear();
      listGroup.forEach(group -> getNext(group, set));
      System.out.println(listGroup);
      saveDb(listGroup);
      System.out.println("結束");
    } catch (IOException e) {
      log.error("", e);
    }
  }

  @Transactional
  public void saveDb(List<Group> groupList) {
    if (groupList == null && groupList.isEmpty()) {
      return;
    }
    groupRepository.saveAllAndFlush(groupList);
    for (Group group : groupList) {
      if (group.hasNext()) {
        List<Group> nextGroup = group.getNextGroup();
        saveDb(nextGroup);
      }
    }
  }

  public static void getNext(Group lastGroup, Set<String> set) {
    try {
      String lastName = lastGroup.getName();
      String url = lastGroup.getUrl();
      if (set.contains(lastName)) {
        return;
      }
      set.add(lastName);

      Document doc = Jsoup.connect(url).get();
      List<Group> listGroup = new ArrayList<>();
      // cctv-container為最後一層
      Element body = doc.body();
      Element elementById = body.getElementById("cctv-container");
      if (elementById != null) {
        Elements children = elementById.children();
        for (Element link : children) {
          String nextLink = link.select("a").attr("href");
          String p = link.select("p").text();
          if (StringUtils.isNotBlank(nextLink)) {
            Group group = new Group();
            group.setUrl(nextLink);
            group.setName(p);
            group.setLevel(lastGroup.getLevel() + 1);
            group.setLastName(lastName);
            listGroup.add(group);
          }
        }
        lastGroup.setNextGroup(listGroup);
        return;
      }

      Elements menu = body.getElementsByClass("cctv-menu");
      // li代表還有下一層
      if (!menu.isEmpty()) {
        Elements ul = menu.select("li");
        for (Element link : ul) {
          String nextLink = link.select("a").attr("href");
          String p = link.select("a").text();
          if (StringUtils.isNotBlank(nextLink)) {
            Group group = new Group();
            group.setUrl(nextLink);
            group.setName(p);
            group.setLevel(lastGroup.getLevel() + 1);
            group.setLastName(lastName);
            listGroup.add(group);
          }
          lastGroup.setNextGroup(listGroup);
          listGroup.forEach(group -> getNext(group, set));
        }
      }
      //高速公路實現
      Elements elements = doc.select(".col-6.mb-3.text-center");
      if (!elements.isEmpty()) {
        // 迭代處理每個圖片元素
        for (Element link : elements) {
          // 獲取圖片的URL
          String nextLink = link.select("a").attr("href");
          String p = link.select("h2").text();
          String small = link.select("small").text();
          String name = p + small;
          if (StringUtils.isNotBlank(nextLink)) {
            Group group = new Group();
            group.setUrl(nextLink);
            group.setName(name);
            group.setLevel(lastGroup.getLevel() + 1);
            group.setLastName(lastName);
            listGroup.add(group);
          }
          lastGroup.setNextGroup(listGroup);
          listGroup.forEach(group -> getNext(group, set));
        }
      }
    } catch (Exception e) {
      log.error("", e);
    }
  }
}
