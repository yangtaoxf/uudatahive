package com.uumai.storm.bolt;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.utils.Utils;
import com.uumai.crawer2.MultiCrawlerWorker;
import com.uumai.crawer2.pool.DefaultFixThreadPool;

public class ParseBolt extends BaseRichBolt {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OutputCollector collector;
    private  TopologyContext context;
    private MultiCrawlerWorker worker;
    private static  DefaultFixThreadPool pool;
    String rediskey;

    public ParseBolt(String rediskey){
        this.rediskey=rediskey;
    }
    @Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
        this.context=context;
        if(pool==null)
            pool=new DefaultFixThreadPool(10);
        //this.worker= new AmazonCrawlerWorker(null,pool);
    }

	@Override		
	  public void execute(Tuple tuple) {
		String html = (String)tuple.getValue(0);
//	    System.out.println("ParseBolt " +  this.context.getThisTaskId() +" get html:");
//        worker.pipeline();
	    Utils.sleep(3000);
	    collector.ack(tuple);
	  }

	  @Override
	  public void declareOutputFields(OutputFieldsDeclarer ofd) {
		  //ofd.declare(new Fields("html"));
	  }

}
