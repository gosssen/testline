package com.goss.testline.config;

import com.goss.testline.cctv.Cctv;
import com.goss.testline.cctv.CctvService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoadData {

    private static final String CCTV_MENU_SELECTOR = ".container .cctv-menu ul li a";
    private static final String CCTV_CONTAINER_SELECTOR_1 = "#cctv-container .cctv-stack";
    private static final String CCTV_CONTAINER_SELECTOR_2 = ".container .row .col-6.col-md-4.mb-3.text-center";

    private final CctvService cctvService;

    public void checkAndRun(String url) {
        if (!cctvService.hasData()) {
            run(url);
        }
    }

    public void run(String url) {
        if (url == null || url.isEmpty()) {
            log.warn("URL is null or empty.");
            return;
        }

        log.info("Starting data scraping from the web...");

//        String url = "https://tw.live/";
        Document doc = this.getDocument(url);
        Elements links = doc.select(CCTV_MENU_SELECTOR);
        List<Cctv> cctvList = new ArrayList<>();

        links.forEach(link -> {
            Cctv cctv = new Cctv();
            cctv.setUri(link.attr("href"));
            cctv.setLabel(link.text());
            cctv.setCctvList(new ArrayList<>());
            cctvList.add(cctv);
        });

        this.recursiveCrawl(cctvList);
        cctvService.saveAll(cctvList);
        log.info("Data loading complete.");
    }

    private void recursiveCrawl(List<Cctv> cctvList) {
        Document doc;
        String uri;
        String label;
        String imageUrl;
        for (Cctv cctv : cctvList) {
            doc = this.getDocument(cctv.getUri());
            Elements select = doc.select(CCTV_CONTAINER_SELECTOR_1);

            if (!select.isEmpty()) {
                for (Element element : select) {
                    uri = element.select("a").get(0).attr("href");
                    label = element.select("p").get(0).text();
                    imageUrl = element.select("img").get(0).attr("data-src");
                    addSubTwLive(cctv, uri, label, imageUrl);
                }
            }

            select = doc.select(CCTV_CONTAINER_SELECTOR_2);
            if (!select.isEmpty()) {
                for (Element element : select) {
                    uri = element.select("a").attr("href");
                    label = element.select("h2").text() + " " + element.select("small").text();
                    addSubTwLive(cctv, uri, label, null);
                }
                recursiveCrawl(cctv.getCctvList());
            }

        }
    }

    private void addSubTwLive(Cctv cctv, String url, String name, String imgUrl) {
        Cctv subCctv = new Cctv();
        subCctv.setUri(url);
        subCctv.setLabel(name);
        subCctv.setImageUrl(imgUrl);
        subCctv.setParentUri(cctv.getUri());
        subCctv.setParentLabel(cctv.getLabel());
        subCctv.setCctvList(new ArrayList<>());
        cctv.getCctvList().add(subCctv);
    }

    private Document getDocument(String uri) {
        try {
            return Jsoup.connect(uri).get();
        } catch (IOException e) {
            log.error("Failed to fetch document from URI: {}", uri, e);
            throw new RuntimeException("Failed to fetch document", e);
        }
    }

}
