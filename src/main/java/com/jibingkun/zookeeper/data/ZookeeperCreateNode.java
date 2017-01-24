package com.jibingkun.zookeeper.data;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * 使用同步创建节点
 * zookeeper.create(path, data, acl, createMode);
 *     path ： 被创建的节点路􏰐,比如􏰈/zk-book/foo
 *     data ： 创建的数据，字节数组--需要自己进行序列化（Hession,kryo）
 *     acl  ： 节点的ACL权限
 *     createMode ： 节点类型
 *           持久􏰞PERSISTENT􏰟   持久顺􏰠序􏰞PERSISTENT_SEQUENTIAL􏰟  临时􏰞EPHEMERAL􏰟  临时􏰠顺序􏰞EPHEMERAL_SEQUENTIAL􏰟
 * @author junjin4838
 * @return success create znode: /zk-test-ephemeral-  （临时节点 -- 返回的就是传入的路径）
 *         success create znode: /zk-test-ephemeral-0000000002
 *
 */
public class ZookeeperCreateNode implements Watcher {
	
	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	
	public static void main(String[] args) throws IOException, KeeperException {
		
		ZooKeeper zookeeper = new ZooKeeper("localhost:2181",6000, new ZookeeperCreateNode());
		
		try {
			
			countDownLatch.await();
			
			//创建临时节点
			String path1 = zookeeper.create("/zk-test-ephemeral-", "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			System.out.println("success create znode: "+path1);
			
			//创建临时顺序节点
			String path2 = zookeeper.create("/zk-test-ephemeral-", "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			System.out.println("success create znode: "+path2);
			
			String path3 = zookeeper.create("/locks", null, Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
			System.out.println("success create znode: "+path3);
			
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void process(WatchedEvent event) {
       if(KeeperState.SyncConnected == event.getState()){
    	   countDownLatch.countDown();
       }
	}

}
