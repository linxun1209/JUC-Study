# **7 JUC 三大辅助类 **
JUC 中提供了三种常用的辅助类，通过这些辅助类可以很好的解决线程数量过 
多时 Lock 锁的频繁操作。这三种辅助类为： 

- • CountDownLatch: 减少计数 
- • CyclicBarrier: 循环栅栏 
- • Semaphore: 信号灯 

下面我们分别进行详细的介绍和学习 
## **7.1 减少计数 CountDownLatch **
CountDownLatch 类可以设置一个计数器，然后通过 countDown 方法来进行 
减 1 的操作，使用 await 方法等待计数器不大于 0，然后继续执行 await 方法 
之后的语句。 
• CountDownLatch 主要有两个方法，当一个或多个线程调用 await 方法时，这 
些线程会阻塞 
• 其它线程调用 countDown 方法会将计数器减 1(调用 countDown 方法的线程 
不会阻塞) 
• 当计数器的值变为 0 时，因 await 方法阻塞的线程会被唤醒，继续执行 
### **场景: 6 个同学陆续离开教室后值班同学才可以关门。 **
CountDownLatchDemopackage com.at
```
package com.xingchen.juc;

import java.util.concurrent.CountDownLatch;

//演示 CountDownLatch

/**
 * @author xing'chen
 */
public class CountDownLatchDemo {
    //6个同学陆续离开教室之后，班长锁门
    
    public static void main(String[] args) throws InterruptedException {

        //创建CountDownLatch对象，设置初始值
        CountDownLatch countDownLatch = new CountDownLatch(6);

        //6个同学陆续离开教室之后
        for (int i = 1; i <=6; i++) {
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+" 号同学离开了教室");

                //计数  -1
                countDownLatch.countDown();

            },String.valueOf(i)).start();
        }

        //等待
        countDownLatch.await();

        System.out.println(Thread.currentThread().getName()+" 班长锁门走人了");
    }
}

```
## **7.2 循环栅栏 CyclicBarrier **
CyclicBarrier 看英文单词可以看出大概就是循环阻塞的意思，在使用中 
CyclicBarrier 的构造方法第一个参数是目标障碍数，每次执行 CyclicBarrier 一 
次障碍数会加一，如果达到了目标障碍数，才会执行 cyclicBarrier.await()之后 
的语句。可以将 CyclicBarrier 理解为加 1 操作 
### **场景: 集齐 7 颗龙珠就可以召唤神龙 **
CyclicBarrierDemo
```
package com.xingchen.juc;

import java.util.concurrent.CyclicBarrier;

//集齐7颗龙珠就可以召唤神龙
/**
 * @author xing'chen
 */
public class CyclicBarrierDemo {

    //创建固定值
    private static final int NUMBER = 7;

    public static void main(String[] args) {
        //创建CyclicBarrier
        CyclicBarrier cyclicBarrier =
                new CyclicBarrier(NUMBER,()->{
                    System.out.println("*****集齐7颗龙珠就可以召唤神龙");
                });

        //集齐七颗龙珠过程
        for (int i = 1; i <=7; i++) {
            new Thread(()->{
                try {
                    System.out.println(Thread.currentThread().getName()+" 星龙被收集到了");
                    //等待
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            },String.valueOf(i)).start();
        }
    }
}

```
## **7.3 信号灯 Semaphore **
Semaphore 的构造方法中传入的第一个参数是最大信号量（可以看成最大线 
程池），每个信号量初始化为一个最多只能分发一个许可证。使用 acquire 方 
法获得许可证，release 方法释放许可 
场景: 抢车位, 6 部汽车 3 个停车位 
SemaphoreDemo
```
package com.xingchen.juc;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author xing'chen
 */ //6辆汽车，停3个车位
public class SemaphoreDemo {
    public static void main(String[] args) {
        //创建Semaphore，设置许可数量
        Semaphore semaphore = new Semaphore(3);

        //模拟6辆汽车
        for (int i = 1; i <=6; i++) {
            new Thread(()->{
                try {
                    //抢占
                    semaphore.acquire();

                    System.out.println(Thread.currentThread().getName()+" 抢到了车位");

                    //设置随机停车时间
                    TimeUnit.SECONDS.sleep(new Random().nextInt(5));

                    System.out.println(Thread.currentThread().getName()+" ------离开了车位");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //释放
                    semaphore.release();
                }
            },String.valueOf(i)).start();
        }
    }
}

```
# **8 读写锁 **
![image.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1671005516450-344e788e-6e22-4aaa-bdca-76347255eaab.png#averageHue=%23fefbf8&clientId=u45e337c5-2a6e-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=270&id=u943bec9c&margin=%5Bobject%20Object%5D&name=image.png&originHeight=486&originWidth=1190&originalType=binary&ratio=1&rotation=0&showTitle=false&size=18086&status=done&style=none&taskId=uba1509e3-3d58-4feb-8633-40a767c7702&title=&width=661.1111286245751)
## **8.1 读写锁介绍 **
现实中有这样一种场景：对共享资源有读和写的操作，且写操作没有读操作那 
么频繁。在没有写操作的时候，多个线程同时读一个资源没有任何问题，所以 
应该允许多个线程同时读取共享资源；但是如果一个线程想去写这些共享资源， 
就不应该允许其他线程对该资源进行读和写的操作了。 
针对这种场景，**JAVA 的并发包提供了读写锁 ReentrantReadWriteLock， **
**它表示两个锁，一个是读操作相关的锁，称为共享锁；一个是写相关的锁，称 **
**为排他锁 **
### 1. 线程进入读锁的前提条件： 
• 没有其他线程的写锁 
• 没有写请求, 或者==有写请求，但调用线程和持有锁的线程是同一个(可重入锁)。== 
### 2. 线程进入写锁的前提条件： 
• 没有其他线程的读锁 
• 没有其他线程的写锁 
### 而读写锁有以下三个重要的特性： 
（1）公平选择性：支持非公平（默认）和公平的锁获取方式，吞吐量还是非公 
平优于公平。 
（2）重进入：读锁和写锁都支持线程重进入。 
（3）锁降级：遵循获取写锁、获取读锁再释放写锁的次序，写锁能够降级成为 
读锁。
## **8.2 ReentrantReadWriteLock **
ReentrantReadWriteLock 类的整体结构
```
public class ReentrantReadWriteLock implements ReadWriteLock,
        java.io.Serializable {
    /** 读锁 */
    private final ReentrantReadWriteLock.ReadLock readerLock;
    /** 写锁 */
    private final ReentrantReadWriteLock.WriteLock writerLock;
    final Sync sync;

    /** 使用默认（非公平）的排序属性创建一个新的
     ReentrantReadWriteLock */
    public ReentrantReadWriteLock() {
        this(false);
    }
    /** 使用给定的公平策略创建一个新的 ReentrantReadWriteLock */
    public ReentrantReadWriteLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
        readerLock = new ReadLock(this);
        writerLock = new WriteLock(this);
    }
    /** 返回用于写入操作的锁 */
    public ReentrantReadWriteLock.WriteLock writeLock() { return
            writerLock; }

    /** 返回用于读取操作的锁 */
    public ReentrantReadWriteLock.ReadLock readLock() { return
            readerLock; }
    abstract static class Sync extends AbstractQueuedSynchronizer {}
    static final class NonfairSync extends Sync {}
    static final class FairSync extends Sync {}
    public static class ReadLock implements Lock, java.io.Serializable {}
    public static class WriteLock implements Lock, java.io.Serializable {}
}
```
可以看到，ReentrantReadWriteLock 实现了 ReadWriteLock 接口， 
ReadWriteLock 接口定义了获取读锁和写锁的规范，具体需要实现类去实现； 
同时其还实现了 Serializable 接口，表示可以进行序列化，在源代码中可以看 
到 ReentrantReadWriteLock 实现了自己的序列化逻辑。
## **8.3 入门案例 **
**场景: 使用 ReentrantReadWriteLock 对一个 hashmap 进行读和写操作 **
### **8.3.1 实现案例**
```
package com.xingchen.readwrite;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//资源类
class MyCache {
    //创建map集合

    private volatile Map<String,Object> map = new HashMap<>();

    //创建读写锁对象
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    //放数据
    public void put(String key,Object value) {
        //添加写锁
        rwLock.writeLock().lock();

        try {
            System.out.println(Thread.currentThread().getName()+" 正在写操作"+key);
            //暂停一会
            TimeUnit.MICROSECONDS.sleep(300);
            //放数据
            map.put(key,value);
            System.out.println(Thread.currentThread().getName()+" 写完了"+key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放写锁
            rwLock.writeLock().unlock();
        }
    }

    //取数据
    public Object get(String key) {
        //添加读锁
        rwLock.readLock().lock();
        Object result = null;
        try {
            System.out.println(Thread.currentThread().getName()+" 正在读取操作"+key);
            //暂停一会
            TimeUnit.MICROSECONDS.sleep(300);
            result = map.get(key);
            System.out.println(Thread.currentThread().getName()+" 取完了"+key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放读锁
            rwLock.readLock().unlock();
        }
        return result;
    }
}

/**
 * @author xing'chen
 */
public class ReadWriteLockDemo {
    public static void main(String[] args) throws InterruptedException {
        MyCache myCache = new MyCache();
        //创建线程放数据
        for (int i = 1; i <=5; i++) {
            final int num = i;
            new Thread(()->{
                myCache.put(num+"",num+"");
            },String.valueOf(i)).start();
        }

        TimeUnit.MICROSECONDS.sleep(300);

        //创建线程取数据
        for (int i = 1; i <=5; i++) {
            final int num = i;
            new Thread(()->{
                myCache.get(num+"");
            },String.valueOf(i)).start();
        }
    }
}

```
## **8.4 小结(重要) **
• 在线程持有读锁的情况下，该线程不能取得写锁(因为获取写锁的时候，如果发 
现当前的读锁被占用，就马上获取失败，不管读锁是不是被当前线程持有)。 
• 在线程持有写锁的情况下，该线程可以继续获取读锁（获取读锁时如果发现写 
锁被占用，只有写锁没有被当前线程占用的情况才会获取失败）。 
原因: 当线程获取读锁的时候，可能有其他线程同时也在持有读锁，因此不能把 
获取读锁的线程“升级”为写锁；而对于获得写锁的线程，它一定独占了读写 
锁，因此可以继续让它获取读锁，当它同时获取了写锁和读锁后，还可以先释 
放写锁继续持有读锁，这样一个写锁就“降级”为了读锁。
