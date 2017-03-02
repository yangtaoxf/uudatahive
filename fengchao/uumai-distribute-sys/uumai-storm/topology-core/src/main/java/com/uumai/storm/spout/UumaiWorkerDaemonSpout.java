package com.uumai.storm.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import com.uumai.crawer.util.UumaiProperties;
import com.uumai.crawer2.SlavesRuner;

import java.util.Map;

/**
 * Created by rock on 8/26/15.
 */
public class UumaiWorkerDaemonSpout extends BaseRichSpout {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    SpoutOutputCollector _collector;
    static int i=0;

    private static ListenerThread listener;

    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        _collector = collector;
//        System.out.println("cd k:"+context.getCodeDir() + "/uumai.properties");
//        UumaiProperties.init(context.getCodeDir() + "/uumai.properties");
        //listener= new ListenerThread();
        System.out.println("register uumaiSlave listener thread...");
    }


    public void nextTuple() {
        Utils.sleep(1000*60);
        _collector.emit(new Values("message"+i));
        //System.out.println("send a new message..."+i);
        i=i+1;
        synchronized (this){
            if(listener==null){
                System.out.println("uumaiSlave listener thread didn't started, re-register...");
                listener= new ListenerThread();
                //the spount may restart by nimbus,so I guess we can't set it daemon
                listener.setDaemon(true);
                listener.start();
            }
        }
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

    class ListenerThread extends Thread {
        private SlavesRuner runer;

        public void run() {
            runer=new SlavesRuner();
            runer.startserver();
            System.out.println("uumai SlavesRuner start running...");
        }
    }
}
