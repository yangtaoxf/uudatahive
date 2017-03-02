package com.uumai.crawer.quartz.gupiao.qq;

import com.uumai.crawer.quartz.QuartzCrawlerTasker;
import com.uumai.crawer.quartz.local.QuartzLocalAppMaster;

public class Category extends QuartzLocalAppMaster {

	@Override
	public void dobusiness() throws Exception {

		dotask("http://stockapp.finance.qq.com/mstats/?mod=all");

	}

	private void dotask(String url) throws Exception {
		QuartzCrawlerTasker tasker = new QuartzCrawlerTasker();
		tasker.setUrl(url);

		for (int i = 0; i < 10; i++) {
			tasker.addXpath("category" + i, "//div[@id='alllist']/div[2]/div["
					+ (i * 2 + 1) + "]/allText()");

			tasker.addXpath_all("name" + i + "_",
					"//div[@id='alllist']/div[2]/ul[" + (i + 1)
							+ "]/li/a/text()");
			tasker.addXpath_all("link" + i + "_",
					"//div[@id='alllist']/div[2]/ul[" + (i + 1)
							+ "]/li/a/@href");

		}

		putDistributeTask(tasker);
	}

	public static void main(String[] args) throws Exception {
		Category master = new Category();
		master.init();
		master.start();

	}

}
