package com.jibingkun.zookeeper.zk.example.client;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 异步API更新数据
 * @author junjin4838
 *
 */
public class ZookeeperAsyncSetData implements Watcher{
	
	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	
	private static ZooKeeper zk = null;
	
	public static void main(String[] args) throws Exception{
		
		String path = "/zk-book-4";

		zk = new ZooKeeper("localhost:2181", 6000,new ZookeeperAsyncSetData());
		countDownLatch.await();
		
		zk.create(path, "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		
		zk.setData(path, "456".getBytes(), -1, new IStatCallback(), null);
		
		Thread.sleep(Integer.MAX_VALUE);
	}

	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected == event.getState()){
			if(EventType.None == event.getType() && null == event.getPath()){
				countDownLatch.countDown();
			}
		}
	}

}

class IStatCallback implements StatCallback{
	public void processResult(int rc, String path, Object ctx, Stat stat) {
		if(rc == 0){
			System.out.println("SUCCCESS");
		}
	}
	
}
