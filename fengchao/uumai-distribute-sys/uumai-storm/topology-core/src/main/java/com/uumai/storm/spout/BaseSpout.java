package com.uumai.storm.spout;

import backtype.storm.topology.OutputFieldsDeclarer;

import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import com.uumai.crawer.util.UumaiProperties;


public class BaseSpout extends BaseRichSpout {
	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		SpoutOutputCollector _collector;
		static int i=0;


	    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
	        _collector = collector;

	    }
	    
	        
	    public void nextTuple() {
            Utils.sleep(1000*60);
            //final String word = "http://www.amazon.com/afafal.athasf.html";send a new message
	        _collector.emit(new Values("message"+i));
	        System.out.println("send a new message..."+i);
	        i=i+1;
        }
	    
	    @Override
	    public void ack(Object id) {
	    	System.out.println("OK:"+id);
	    }

	    @Override
	    public void fail(Object id) {
	    	System.out.println("fail:"+id);
	    }
	    
	    public void declareOutputFields(OutputFieldsDeclarer declarer) {
	        declarer.declare(new Fields("url"));
	    }
}
