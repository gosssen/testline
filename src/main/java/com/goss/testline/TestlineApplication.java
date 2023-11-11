package com.goss.testline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TestlineApplication {
	//todo: 初始化目前只做一次,手動啟用 後續在新增判斷
	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(TestlineApplication.class, args);
//		InitData bean = run.getBean(InitData.class);
//		bean.recursiveCrawl("https://tw.live/");
	}

}
