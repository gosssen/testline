package com.goss.testline;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;

public class TestGetTWLiveLinks {
    public static void main(String[] args) {
        // 爬取的網頁URL
        String url = "https://tw.live/";
        // 訪問過的連結
        HashSet<String> visitedLinks = new HashSet<>();
        recursiveCrawl(url, visitedLinks);
        visitedLinks.clear();
    }

    public static void recursiveCrawl(String url, HashSet<String> visitedLinks) {
        try {
            Document doc = Jsoup.connect(url).get();
            // 提取連結
            Elements links = doc.select("body a:not(nav a, header a)");

            String parentLink;
            String parentLinkText;

            for (Element link : links) {
                String linkHref = link.attr("href");

                // 訪問過的連結直接離開
                if (visitedLinks.contains(linkHref)) {
                    break;
                }

                if (!link.text().isEmpty()) {
                    parentLink = linkHref;
                    parentLinkText = link.text();

                    // 國道的連結文字都是即時影像，另外判斷取得<a>前一個元素<small>的文字
                    if ("即時影像".equals(parentLinkText)) {
                        parentLinkText = link.previousElementSibling() == null ? "" : Objects.requireNonNull(link.previousElementSibling()).text();
                    }

                    System.out.println("連結文字: " + parentLinkText);
                    System.out.println("連結URL: " + parentLink);
                }

                // 連結文字大部份在<a>下一個元素的文字
                String nextElement = link.nextElementSibling() == null ? "" : Objects.requireNonNull(link.nextElementSibling()).text();

                // 檢查連結URL是否包含 "https://tw.live/cam/?id="
                if (linkHref.startsWith("https://tw.live/cam/?id=")) {
                    System.out.println("符合條件的連結文字: " + nextElement);
                    System.out.println("符合條件的連結: " + linkHref);
                } else if (linkHref.startsWith("https://tw.live/") && linkHref.endsWith("/")) {
                    // 否則遞歸訪問連結
                    recursiveCrawl(linkHref, visitedLinks);
                }

                // 將訪問過的連結放入，避免重覆查詢
                visitedLinks.add(linkHref);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
