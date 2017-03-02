package com.uumai.storm.bolt;

import java.util.Map;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.uumai.crawer.util.io.SerializeUtil;
import com.uumai.crawer2.CrawlerTasker;
import com.uumai.crawer2.download.Download;
import com.uumai.crawer2.download.httpdownload.HttpDownload;

public class DownloadBolt extends BaseRichBolt {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private TopologyContext context;
    private Download download;

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
        this.context=context;
        this.download=new HttpDownload();
	}

	@Override		
	  public void execute(Tuple tuple) {
	    String jobtasker = (String)tuple.getValue(0);
        System.out.println("DownloadBolt "+this.context.getThisTaskId()+" get tasker.");
        try{
            String html=dowork(jobtasker);
            System.out.println("DownloadBolt "+this.context.getThisTaskId()+" get html:"+ html);
            collector.emit(new Values(html));
            collector.ack(tuple);
        }catch(Exception ex){
            ex.printStackTrace();
            collector.fail(tuple);
        }
	  }

    private String dowork(String jobMsg) throws  Exception{
        CrawlerTasker jobtasker =(CrawlerTasker) SerializeUtil.unserialize(jobMsg);
        return download.download(jobtasker).getRawText();
//        return "mockhtml";

    }

	  @Override
	  public void declareOutputFields(OutputFieldsDeclarer ofd) {
		  ofd.declare(new Fields("html"));
	  }

}
