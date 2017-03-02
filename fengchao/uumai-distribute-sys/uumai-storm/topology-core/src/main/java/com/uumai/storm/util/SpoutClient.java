package com.uumai.storm.util;

import com.uumai.crawer.util.io.SerializeUtil;
import com.uumai.crawer2.CrawlerTasker;
import com.uumai.redis.RedisDao;

import redis.clients.jedis.Jedis;

public class SpoutClient {
	 String rediskey;
	public SpoutClient(String rediskey){
		this.rediskey=rediskey;
		
	}
	
	public void sendTask(String message){
		try {
			RedisDao dao=new RedisDao();
 			Jedis redis=dao.getRedis();
			redis.lpush(rediskey,  message);
			dao.returnResource(redis);
			dao.destroy();
		 } catch (Exception e) {
	            e.printStackTrace();
	        }
	}

    public void sendTask(CrawlerTasker tasker){
        sendTask(SerializeUtil.serialize(tasker));
    }

	public static void main(String[] args) {
		String topologyName = "uumaiTopology";
	    if (args.length >= 1) {
	      topologyName = args[0];
	    }
		SpoutClient client=new SpoutClient(topologyName);
//		for(int i=0;i<30;i++){
//			client.sendTask("http://amazon.com/"+i);
//		}
//        AmazonCrawlerTasker producttasker=new AmazonCrawlerTasker();
//        producttasker.
//                setUrl("http://www.amazon.com/Capezio-Little-Flutter-Sleeve-Leotard-4-6/dp/B0038M1XT4");
//        producttasker.setCrawler_type("product");
//        client.sendTask(producttasker);

		System.out.println("sended successfully");

	}

}
