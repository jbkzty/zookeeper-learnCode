package com.jibingkun.zookeeper.zk.example.client;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 更新数据
 * 基于version参数可以很好的控制zookeeper上节点数据的原子性操作
 * @author junjin4838
 *
 */
public class ZookeeperSyncSetData implements Watcher {
	
	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	
	private static ZooKeeper zk = null;
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		
		String path = "/zk-book-3";
		
		zk = new ZooKeeper("localhost:2181",6000,new ZookeeperSyncSetData());
		countDownLatch.await();
		
		zk.create(path, "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		zk.getData(path, true, null);
		
	    //参数“-1” --> 基于数据最新版本经常操作
		Stat stat = zk.setData(path, "456".getBytes(), -1);
		System.out.println("第一次更新：" + stat.getCzxid() + "," + stat.getMzxid() + "," + stat.getVersion());
		
		Stat Stat2 = zk.setData(path, "456".getBytes(), stat.getVersion());
		System.out.println("第二次更新：" + Stat2.getCzxid() + "," + Stat2.getMzxid() + "," +Stat2.getVersion());
		
		try {
			zk.setData(path, "456".getBytes(), stat.getVersion());
		} catch (KeeperException e) {
			System.out.println("Error :" + e.getCode() + e.getMessage());
		}
		
		Thread.sleep(Integer.MAX_VALUE);
	}

	public void process(WatchedEvent event) {
	   if(KeeperState.SyncConnected == event.getState()){
		   countDownLatch.countDown();
	   }
	}
	
	

}
