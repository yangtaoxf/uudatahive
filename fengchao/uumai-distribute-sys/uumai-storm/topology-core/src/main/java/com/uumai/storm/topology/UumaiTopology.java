package com.uumai.storm.topology;

import backtype.storm.Config;
import backtype.storm.topology.TopologyBuilder;

import com.uumai.crawer.util.license.LicenseInfo;
import com.uumai.crawer.util.license.LicenseValidateHelper;
import com.uumai.storm.bolt.BaseBolt;
import com.uumai.storm.bolt.DownloadBolt;
import com.uumai.storm.bolt.ParseBolt;
import com.uumai.storm.bolt.UumaiWorkerDaemonBolt;
import com.uumai.storm.spout.BaseRedisSpout;
import com.uumai.storm.spout.BaseSpout;
import com.uumai.storm.spout.UumaiWorkerDaemonSpout;
import com.uumai.storm.util.StormRunner;

public class UumaiTopology {
	  private static final int DEFAULT_RUNTIME_IN_SECONDS = 60;

	  private final TopologyBuilder builder;
	  private final String topologyName;
	  private final Config topologyConfig;
	  private final int runtimeInSeconds;
      private int picount=1;
      private int license_limit_picount=1;


    public UumaiTopology(String topologyName) throws InterruptedException {
        this(topologyName,1);
    }

        public UumaiTopology(String topologyName,int picount) throws InterruptedException {
	    builder = new TopologyBuilder();
	    this.topologyName = topologyName;
            this.picount=picount;
	    topologyConfig = createTopologyConfiguration();
	    runtimeInSeconds = DEFAULT_RUNTIME_IN_SECONDS;
	    wireTopology();
	  }

	  private Config createTopologyConfiguration() {
	    Config conf = new Config();
	    //conf.setDebug(true);
	    conf.setNumWorkers(picount);
          conf.setNumAckers(0);
	    return conf;
	  }

	  public void runLocally() throws InterruptedException {
	    StormRunner.runTopologyLocally(builder.createTopology(), topologyName, topologyConfig, runtimeInSeconds);
	  }

    private void wireTopology() throws InterruptedException {
        String spoutId = "taskerGenerator";
        String download = "download";
        String parse = "parse";
//        builder.setSpout(spoutId, new BaseRedisSpout(topologyName,3,3), 1);
//        builder.setBolt(download, new DownloadBolt(),2).shuffleGrouping(spoutId);
//        builder.setBolt(parse, new ParseBolt(topologyName),3).shuffleGrouping(download);

//   	    builder.setSpout(spoutId, new BaseSpout(), this.maxPicount);
//        builder.setBolt(download, new UumaiWorkerDaemonBolt(),this.maxPicount).localOrShuffleGrouping(spoutId);

//        builder.setSpout(spoutId, new BaseSpout(),picount);
//        builder.setBolt(download, new UumaiWorkerDaemonBolt(),picount).localOrShuffleGrouping(spoutId);

        builder.setSpout(spoutId, new UumaiWorkerDaemonSpout(),picount);
        builder.setBolt(download, new BaseBolt(),picount).localOrShuffleGrouping(spoutId);

    }

	  public void runRemotely() throws Exception {
	    StormRunner.runTopologyRemotely(builder.createTopology(), topologyName, topologyConfig);
	  }

    private boolean checkLicense() {
        try {
            LicenseValidateHelper licenseValidateHelper=new LicenseValidateHelper();
            LicenseInfo licenseInfo=licenseValidateHelper.validate();
            this.license_limit_picount=new Integer(licenseInfo.getDistribute_pi_count());
            System.out.println("license check passed,could run pi count:"+this.license_limit_picount);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("license check failed!");
        return false;
    }
	  
	  public static void main(String[] args) throws Exception {

	    String topologyName = "uumaiTopology";
	    if (args.length >= 1) {
	      topologyName = args[0];
	    }
	    boolean runLocally = true;
	    if (args.length >= 2 && args[1].equalsIgnoreCase("remote")) {
	      runLocally = false;
	    }
          int picount=1;
          if (args.length >= 3) {
              picount = new Integer(args[2]);
          }

	    System.out.println("Topology name: " + topologyName);
	    
	    UumaiTopology rtw = new UumaiTopology(topologyName,picount);

	    if (runLocally) {
	    	System.out.println("Running in local mode");
	      rtw.runLocally();
	    }
	    else {
	    	System.out.println("Running in remote (cluster) mode");
            if(rtw.checkLicense())
                rtw.runRemotely();
	    }
	  }
}
