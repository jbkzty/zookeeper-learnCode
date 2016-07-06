package com.jibingkun.zookeeper.zk.example.client;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * 异步创建节点
 * @author junjin4838
 *
 */
public class ZookeeperSynCreateNode implements Watcher{
	
	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	
	public static void main(String[] args) throws IOException {
		
		ZooKeeper zookeeper = new ZooKeeper("localhost:2181",6000, new ZookeeperSynCreateNode());
		try {
			countDownLatch.await();
			
			zookeeper.create("/zk-test-ephemeral-", "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,new IStringCallback(), "i am context");
			
			zookeeper.create("/zk-test-ephemeral-", "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,new IStringCallback(), "i am context");
			
			zookeeper.create("/zk-test-ephemeral-", "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,new IStringCallback(), "i am context");
			
			Thread.sleep(Integer.MAX_VALUE);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected == event.getState()){
			countDownLatch.countDown();
			System.out.println("线程数量减少");
		}
	}

}

/**
 * 回调函数
 * 当服务端节点创建完毕之后，zookeeper客户端就会自动调用这个方法，可以处理相应的业务处理
 * @param rc : Result Code 服务端响应码--客户端可以从这个相应编码中识别出API调用的结果
 *             0 （ok） ： 接口调用成功
 *             -4（ConnectionLoss）： 客户端和服务端连接已经断开
 *             -110 （NodeExists） ： 指定节点已经存在
 *             -112 （sessionExpired） ： 会话已经过期
 * @param path 节点路径
 * @param ctx  上下文参数值（一般用于对象的传输）
 * @param name
 * 
 * @author junjin4838
 *
 */
class IStringCallback implements StringCallback{

	public void processResult(int rc, String path, Object ctx, String name) {
		System.out.println("create path result : [" + rc + "," + path + "," + ctx + "," + "real path name :" + name + " ]");
		
	}
	
}
