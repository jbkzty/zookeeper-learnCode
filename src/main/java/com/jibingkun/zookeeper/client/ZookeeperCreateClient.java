package com.jibingkun.zookeeper.client;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

/**
 * 创建会话
 * 四种构造方法：
 * ZooKeeper(String connectString,int sessionTimeout,Watcher watcher)
 * ZooKeeper(String connectString,int sessionTimeout,Watcher watcher,boolean canBeReadOnly)
 * ZooKeeper(String connectString,int sessionTimeout,Watcher watcher,long sessionId,byte[] sessionPasswd)
 * ZooKeeper(String connectString,int sessionTimeout,Watcher watcher,long sessionId,byte[] sessionPasswd,boolean canBeReadOnly)
 * 
 * connectString  ： 服务器列表，由逗号分开
 * sessionTimeout ： 会话超时时间,单􏰽是毫秒􏰹􏰱在􏰙个时间内没有收到心跳检测,会话就会失效
 * watcher        ： 事件通知处理器
 * canBeReadOnly  ： 用于标识􏰱前会话是否支持”read-only”模式􏰹,􏰱zk集群中的某个机器􏰀集群中过半以􏰍的机器网络端口􏰝,􏰣机器将􏰕会接􏰿客户端的任何读写请求,但是,有时候希望继续提供读请求,
 *                  因􏰣􏰛置􏰣参数􏰼true, 即􏰣客户端􏱁􏰷以从􏰀集群中半数以􏰍节点网络􏰕通的机器节点中读􏱂数据
 * sessionId和 sessionPasswd  ： 这两个参数能够唯一确定一个会话，同时客户端使用这两个参数可以实现客户端会话的复用，从而达到恢复会话的效果。
 *                              使用方法：第一次连接上zookeeper服务器时，通过调用zookeeper对象实例的两个方法：
 *                                 -- long getSessionId()
 *                                 -- byte[] getSessionPasswd()
 *                              然后构造参数便可以
 *                              
 * zookeeper客户端会话的建立是一个异步的过程。
 *     构造方法会在处理完客户端初始化工作后立即返回，在多数情况下，此时并没有真正建立好一个可用的会话，在会话的生命周期中处于“connecting”的状态
 * 当会话真正创建完毕之后，zookeeper服务端会向会话对应的客户端发送一个事件通知，以告知客户端，客户端只有在获取这个通知之后，才算真正建立了会话。 
 * 
 *     构造方法内部实现了与ZooKeeper服务器之间的TCP连接的创建
 *     
 * @author junjin4838
 *
 */
public class ZookeeperCreateClient implements Watcher {
	
	//设置等待线程锁
	private static CountDownLatch countDownLatch = new CountDownLatch(1);

	public static void main(String[] args) throws IOException {
		ZooKeeper zookeeper = new ZooKeeper("localhost:2181",6000, new ZookeeperCreateClient());
		System.out.println("begin state=" + zookeeper.getState());
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Zookeeper session established.");
		}
		System.out.println("end state="+zookeeper.getState());	
	}

	/**
	 * 实现Watcher接口，这个方法是负责处理来自zookeeper服务端的watcher通知，在收到服务端发来的SynConnected事件之后，doSomething
	 */
	public void process(WatchedEvent event) {
		System.out.println("receive watched event :" + event);
        if(KeeperState.SyncConnected == event.getState()){
        	countDownLatch.countDown();
        }
	}

}
