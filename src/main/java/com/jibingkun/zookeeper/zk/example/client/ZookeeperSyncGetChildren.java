package com.jibingkun.zookeeper.zk.example.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * 获取一个节点的所有子节点
 * @author junjin4838
 *
 */
public class ZookeeperSyncGetChildren implements Watcher{
	
	private static CountDownLatch  countDownLatch = new CountDownLatch(1);
	
	private static ZooKeeper  zk = null;
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		String path = "/zk-book-1";
		
		zk = new ZooKeeper("localhost:2181",6000,new ZookeeperSyncGetChildren());
		countDownLatch.await();
		
		zk.create(path, "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		zk.create(path+"/c1", "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		
		List<String> childrenList = zk.getChildren(path, true);
		System.out.println("获得到的子节点： "+ childrenList);
		
		zk.create(path+"/c2", "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		
		Thread.sleep(Integer.MAX_VALUE);
		
	}

	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected == event.getState()){
			if(EventType.None == event.getType() && null == event.getPath()){
				countDownLatch.countDown();
			}else if(event.getType() == EventType.NodeChildrenChanged){
				try {
					List<String> childrenList = zk.getChildren(event.getPath(), true);
					System.out.println("变更之后的子节点：" + childrenList);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}
	}
	

}
