package com.uumai.storm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.utils.Utils;
import com.uumai.crawer.util.UumaiProperties;
import com.uumai.crawer2.SlavesRuner;

import java.util.Map;

/**
 * Created by rock on 8/26/15.
 */
public class BaseBolt extends BaseRichBolt {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private OutputCollector collector;
    private TopologyContext context;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.context=context;

    }

    @Override
    public void execute(Tuple tuple) {
        Utils.sleep(1000*60);
        String message = (String)tuple.getValue(0);
        //System.out.println("get a new message..."+message);

        collector.ack(tuple);
    }



    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
    }


}
