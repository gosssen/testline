package com.goss.testline.cctv;

import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ImageCarouselColumn;
import com.linecorp.bot.model.message.template.ImageCarouselTemplate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CctvServiceImpl implements CctvService {
    private final CctvRepository cctvRepository;

    @Override
    @Transactional
    public void saveAll(List<Cctv> cctvList) {
        if (CollectionUtils.isEmpty(cctvList)) {
            return;
        }
        cctvRepository.saveAllAndFlush(cctvList.stream()
                .distinct()
                .collect(Collectors.toList()));
        for (Cctv cctv : cctvList) {
            if (cctv.hasNext()) {
                saveAll(cctv.getCctvList());
            }
        }
    }
    @Override
    public boolean hasData() {
        return cctvRepository.count() > 0;
    }

    @Override
    public Message processQuestion(String keyword) {
        List<Cctv> cctvList = cctvRepository.findByLabelContaining(keyword);
        if (cctvList.isEmpty()) {
            return new TextMessage("查無相關即時影像監視器。");
        }

        List<ImageCarouselColumn> imageCarouselColumns = new ArrayList<>();
        for (Cctv cctv : cctvList) {
            imageCarouselColumns.add(this.generateImageCarouselColumn(cctv));
            if (imageCarouselColumns.size() > 9) {
                break;
            }
        }
        return new TemplateMessage("即時影像監視器查詢結果。", new ImageCarouselTemplate(imageCarouselColumns));
    }


    private ImageCarouselColumn generateImageCarouselColumn(Cctv cctv) {

        try {
            return new ImageCarouselColumn(
                    new URI(cctv.getImageUrl()),
                    new URIAction(
                            StringUtils.abbreviate(cctv.getLabel(), 12),
                            new URI(cctv.getUri()),
                            new URIAction.AltUri(new URI(cctv.getUri()))
                    )
            );
        } catch (URISyntaxException e) {
            log.info("Error generating ImageCarouselColumn", e);
            throw new RuntimeException(e);
        }
    }

}
