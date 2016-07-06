package com.jibingkun.zookeeper.zk.example.client;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * 删除节点的权限控制
 * @author junjin4838
 *
 */
public class ZookeeperAuthDelete {

	final static String PATH = "/zk-book-auth_test";
	
	final static String PATH2 = "/zk-book-auth_test/child";
	
	public static void main(String[] args) throws Exception {
		
		ZooKeeper zk1 = new ZooKeeper("localhost:2181",6000, null);
		zk1.addAuthInfo("digest", "foo:true".getBytes());
		zk1.create(PATH, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
		zk1.create(PATH2, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
		
		//没有删除权限
		//删除节点失败： KeeperErrorCode = NoAuth for /zk-book-auth_test/child
		try {
			ZooKeeper zk2 = new ZooKeeper("localhost:2181",6000, null);
			zk2.delete(PATH2, -1);
		} catch (Exception e) {
			System.out.println("删除节点失败： " + e.getMessage());
		}
		
		//有删除权限
		//删除节点成功： /zk-book-auth_test/child
		ZooKeeper zk3 = new ZooKeeper("localhost:2181",6000, null);
		zk3.addAuthInfo("digest", "foo:true".getBytes());
		zk3.delete(PATH2, -1);
		System.out.println("删除节点成功： " + PATH2);
		
		//没有权限的客户端也可以删除节点成功
		//删除节点成功： /zk-book-auth_test
		/**
		 * zk4是没有包含权限信息的客户端会话，但最终可以成功删除数据节点
		 * 删除节点接口的权限比较特殊：当客户端对一个数据节点添加权限信息之后，对于删除操作而言，其作用范围是其子节点
		 * 也就是说：
		 *    当我们队一个数据节点添加权限信息之后，依然可以自由的删除这个节点，但是对于这个节点的子节点，就必须使用相应的权限信息才可以
		 */
		ZooKeeper zk4 = new ZooKeeper("localhost:2181",6000, null);
		zk4.delete(PATH, -1);
		System.out.println("删除节点成功： " + PATH);
		
	}
}
