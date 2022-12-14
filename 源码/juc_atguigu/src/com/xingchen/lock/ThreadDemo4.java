package com.xingchen.lock;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * list集合线程不安全
 * @author xing'chen
 */
public class ThreadDemo4 {
    public static void main(String[] args) {
        //创建ArrayList集合
//        List<String> list = new ArrayList<>();
        // Vector解决
//        List<String> list = new Vector<>();

        //Collections解决
//        List<String> list = Collections.synchronizedList(new ArrayList<>());

        // CopyOnWriteArrayList解决
//        List<String> list = new CopyOnWriteArrayList<>();
//        for (int i = 0; i <30; i++) {
//            new Thread(()->{
//                //向集合添加内容
//                list.add(UUID.randomUUID().toString().substring(0,8));
//                //从集合获取内容
//                System.out.println(list);
//            },String.valueOf(i)).start();
//        }

        //演示Hashset
//        Set<String> set = new HashSet<>();

//        Set<String> set = new CopyOnWriteArraySet<>();
//        for (int i = 0; i <30; i++) {
//            new Thread(()->{
//                //向集合添加内容
//                set.add(UUID.randomUUID().toString().substring(0,8));
//                //从集合获取内容
//                System.out.println(set);
//            },String.valueOf(i)).start();
//        }

        //演示HashMap
//        Map<String,String> map = new HashMap<>();

//        Map<String,String> map = new ConcurrentHashMap<>();
//        for (int i = 0; i <30; i++) {
//            String key = String.valueOf(i);
//            new Thread(()->{
//                //向集合添加内容
//                map.put(key,UUID.randomUUID().toString().substring(0,8));
//                //从集合获取内容
//                System.out.println(map);
//            },String.valueOf(i)).start();
//        }
    }
}
