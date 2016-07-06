package com.jibingkun.zookeeper.zk.example.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.Children2Callback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 使用异步API获取子节点
 * 
 * 异步接口通常会应用在这样的场景中：应用启动的时候，会获取一些配置信息，例如“机器列表”
 * 这些配置通常比较大，并且不希望配置的信息的获取影响到应用的主流程
 * 
 * @author junjin4838
 *
 */
public class ZookeeperAsyncGetChildren implements Watcher {

	private static CountDownLatch countDownLatch = new CountDownLatch(1);

	private static ZooKeeper zk = null;

	public static void main(String[] args) throws IOException,
			InterruptedException, KeeperException {

		String path = "/zk-book-2";

		zk = new ZooKeeper("localhost:2181", 6000,new ZookeeperAsyncGetChildren());
		countDownLatch.await();

		zk.create(path, "123".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);
		zk.create(path + "/c1", "123".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL);

		zk.getChildren(path, true, new IChildren2Callback(), null);
		
		zk.create(path + "/c2", "123".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL);
		
		Thread.sleep(Integer.MAX_VALUE);
		
	}

	public void process(WatchedEvent event) {
		if (KeeperState.SyncConnected == event.getState()) {
			if (EventType.None == event.getType() && null == event.getPath()) {
				countDownLatch.countDown();
			} else if (event.getType() == EventType.NodeChildrenChanged) {
				try {
					List<String> list = zk.getChildren(event.getPath(), true);
					System.out.println("节点修改过的信息：" + list);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}

/**
 * 回调函数
 * 
 * @author junjin4838
 *
 */
class IChildren2Callback implements Children2Callback {

	public void processResult(int rc, String path, Object ctx,
			List<String> children, Stat stat) {
		System.out.println("异步回调-状态码： " + rc);
		System.out.println("异步回调-路径： " + path);
		System.out.println("异步回调-上下文： " + ctx);
		System.out.println("异步回调-结果集： " + children);
		System.out.println("异步回调-节点状态信息： " + stat);
	}

}
