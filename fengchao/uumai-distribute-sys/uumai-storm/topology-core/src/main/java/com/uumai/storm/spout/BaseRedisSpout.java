package com.uumai.storm.spout;

import backtype.storm.topology.OutputFieldsDeclarer;

import java.io.File;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.util.List;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.uumai.crawer.util.UumaiProperties;
import com.uumai.redis.RedisDao;
import redis.clients.jedis.Jedis;


public class BaseRedisSpout extends BaseRichSpout {
	    /**
	 * 
	 */
	
   private static final long serialVersionUID = 1L;

		SpoutOutputCollector _collector;
		
		String rediskey;

    LinkedBlockingQueue<String> queue;
    int queueSize=10; //default value

    private AtomicInteger counter;

    LinkedBlockingQueue<Integer> runningTaskQueue;
    int runrunningTaskQueueSize=10; //default value

    public BaseRedisSpout(String rediskey,int queueSize,int runrunningTaskQueueSize){
			this.rediskey=rediskey;
			this.queueSize=queueSize;
            this.runrunningTaskQueueSize=runrunningTaskQueueSize;
		}


	    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
	    	_collector = collector;
	    	
	    	queue = new LinkedBlockingQueue<String>(this.queueSize);
            runningTaskQueue = new LinkedBlockingQueue<Integer>(this.runrunningTaskQueueSize);
            counter = new AtomicInteger();

//            System.out.println("uuami config file path:" + context.getCodeDir());
//            UumaiProperties.init(context.getCodeDir()+ "/uumai.properties");
//            System.out.println("uuami config:" + UumaiProperties.readconfig("uumai.redis.serverip", ""));

            ListenerThread listener = new ListenerThread(queue,this.rediskey);
            //the spount may restart by nimbus,so I guess we can't set it daemon
            //listener.setDaemon(true);
			listener.start();

	    }
	    
	        
	    public void nextTuple() {
//	        Utils.sleep(1000);
//	        final String word = "http://www.amazon.com/afafal.athasf.html";
//	        _collector.emit(new Values(word));

            if(runningTaskQueue.remainingCapacity()<=0){
                //running task is pull
                Utils.sleep(1000);
            }else{
                String ret = queue.poll();
                if(ret==null) {
                    Utils.sleep(1000);
                } else {
                    int msgId = this.counter.getAndIncrement();
                    runningTaskQueue.offer(msgId);
                    _collector.emit(new Values(ret),msgId);
                    System.out.println("send a new task:" + msgId);
                }
            }
	    }
	    
	    @Override
	    public void ack(Object id) {
	    	System.out.println("OK:"+id);
            runningTaskQueue.remove((Integer)id);
	    }

	    @Override
	    public void fail(Object id) {
	    	System.out.println("fail:"+id);
            runningTaskQueue.remove((Integer)id);
	    }
	    
	    public void declareOutputFields(OutputFieldsDeclarer declarer) {
	        declarer.declare(new Fields("url"));
	    }
	    
	    class ListenerThread extends Thread {
	    	LinkedBlockingQueue<String> queue;
	    	String rediskey;
	    	
	    	public ListenerThread(LinkedBlockingQueue<String> queue,String rediskey) {
				this.queue = queue;
				this.rediskey=rediskey;
			}
	    	
	    	public void run() {
                while(true){
                    try{
                        connectRedis();
                    }catch (Exception ex){
                        System.out.println(" error when connect to redis: "+ex.getMessage());
                    }
                }
	    	}

            private void connectRedis() throws Exception{
                RedisDao dao=new RedisDao();
                 Jedis redis=dao.getRedis();
                // block invoke
                while (true){
                    List<String> msgs = redis.brpop(1, rediskey);
                    if (msgs != null) {
                        String jobMsg = msgs.get(1);
                        while(true){
                            if(queue.offer(jobMsg)){
                                //System.out.println(" get new tasker: "+jobMsg);
                                break;
                            }
                                System.out.println(" pool full,sleep! ");
                                //sender.sendmsg(" no job,sleep! ");
                                Thread.sleep(1000);
                        }

//	    	                try {
//		    	                queue.put(jobMsg);  //block when pool is full
//	    	                } catch (InterruptedException e) {
//	    	                    e.printStackTrace();
//	    	                }


                    }else{
                            System.out.println(" no job,sleep! ");
                            //sender.sendmsg(" no job,sleep! ");
                            Thread.sleep(1000);
                    }
                }
            }
	    }
}
