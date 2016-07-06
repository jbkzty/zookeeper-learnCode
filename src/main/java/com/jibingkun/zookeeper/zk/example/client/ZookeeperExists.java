package com.jibingkun.zookeeper.zk.example.client;

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
 * 检测节点是否存在
 *    public Stat exists(final String path,Watcher watcher)
 *    public Stat exists(final String path,boolean watch)
 *    public void exists(final String path,Watcher watcher,StatCallback cb,Object ctx)
 *    public void exists(String path,boolean watch,StatCallback cb,Object ctx)
 *    
 *    Watcher watcher :
 *       注册得Watcher,用于监听以下三类事件
 *           节点被创建
 *           节点被删除
 *           节点被更新
 * 
 * @author junjin4838
 *
 */
public class ZookeeperExists implements Watcher {
	
	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	
	private static ZooKeeper zk = null;
	
	public static void main(String[] args) throws Exception{
		String path = "/zk-book-4";

		zk = new ZooKeeper("localhost:2181", 6000,new ZookeeperExists());
		countDownLatch.await();
		
		//用过exists接口来检测是否存在指定节点，同时注册一个Watcher
		zk.exists(path, true);
		
		//创建节点，此时服务器会马上向客户端发送一个事件通知：NodeCreate
		//客户端在收到该事件通知之后，再次调用exists接口，同时注册Watcher
		zk.create(path, "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		
		zk.setData(path, "456".getBytes(), -1);
		
		zk.delete(path, -1);
	
		Thread.sleep(Integer.MAX_VALUE);
		
	}

	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected == event.getState()){
			if(EventType.None == event.getType()){
				countDownLatch.countDown();
			}else if(EventType.NodeCreated == event.getType()){
				System.out.println("Node("+event.getPath()+")Create");
				try {
					zk.exists(event.getPath(), true);
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else if(EventType.NodeDataChanged == event.getType()){
				System.out.println("Node("+event.getPath()+")Changed");
				try {
					zk.exists(event.getPath(), true);
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else if(EventType.NodeDeleted == event.getType()){
				System.out.println("Node("+event.getPath()+")deleted");
				try {
					zk.exists(event.getPath(), true);
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
