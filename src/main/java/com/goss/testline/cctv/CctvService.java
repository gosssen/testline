package com.goss.testline.cctv;

import com.linecorp.bot.model.message.Message;

import java.util.List;

public interface CctvService {

    void saveAll(List<Cctv> cctvList);

    boolean hasData();

    Message processQuestion(String keyword);
}

