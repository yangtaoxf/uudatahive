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
 * Created by rock on 5/14/15.
 */
public class UumaiWorkerDaemonBolt extends BaseRichBolt {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private OutputCollector collector;
    private TopologyContext context;
    private static ListenerThread listener;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.context=context;

//            System.out.println("cd k:"+context.getCodeDir() + "/uumai.properties");
//            UumaiProperties.init(context.getCodeDir() + "/uumai.properties");
            //listener= new ListenerThread();
            System.out.println("register uumaiSlave listener thread...");
             //the spount may restart by nimbus,so I guess we can't set it daemon
             //listener.setDaemon(true);
             //listener.start();
    }

    @Override
    public void execute(Tuple tuple) {
        Utils.sleep(1000*60);
        String message = (String)tuple.getValue(0);
        System.out.println("get a new message..."+message);
        synchronized (this){
            if(listener==null){
                System.out.println("uumaiSlave listener thread didn't started, re-register...");
                listener= new ListenerThread();
                //the spount may restart by nimbus,so I guess we can't set it daemon
                //listener.setDaemon(true);
                listener.start();
            }
        }
        collector.ack(tuple);
    }



    @Override
    public void declareOutputFields(OutputFieldsDeclarer ofd) {
     }

    class ListenerThread extends Thread {
        private SlavesRuner runer;

        public void run() {
            runer=new SlavesRuner();
            runer.startserver();
            System.out.println("uumai SlavesRuner start running...");
        }
    }

}
