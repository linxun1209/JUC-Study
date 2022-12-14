# **10 ThreadPool 线程池 **
## **10.1 线程池简介 **
线程池（英语：thread pool）：一种线程使用模式。线程过多会带来调度开销， 
进而影响缓存局部性和整体性能。而线程池维护着多个线程，等待着监督管理 
者分配可并发执行的任务。这避免了在处理短时间任务时创建与销毁线程的代 
价。线程池不仅能够保证内核的充分利用，还能防止过分调度。 
例子： 10 年前单核 CPU 电脑，假的多线程，像马戏团小丑玩多个球，CPU 需 
要来回切换。 现在是多核电脑，多个线程各自跑在独立的 CPU 上，不用切换 
效率高。 
#### **线程池的优势： **
线程池做的工作只要是控制运行的线程数量，处理过程中将任 
务放入队列，然后在线程创建后启动这些任务，如果线程数量超过了最大数量， 
超出数量的线程排队等候，等其他线程执行完毕，再从队列中取出任务来执行。 
#### **它的主要特点为： **
• 降低资源消耗: 通过重复利用已创建的线程降低线程创建和销毁造成的销耗。 
• 提高响应速度: 当任务到达时，任务可以不需要等待线程创建就能立即执行。 
• 提高线程的可管理性: 线程是稀缺资源，如果无限制的创建，不仅会销耗系统资 
源，还会降低系统的稳定性，使用线程池可以进行统一的分配，调优和监控。 
• **Java 中的线程池是通过 Executor 框架实现的，该框架中用到了 Executor，Executors， **
**ExecutorService，ThreadPoolExecutor 这几个类**
![image.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1671010306105-8107b84f-2828-4daf-b8e9-3dfb38e58faa.png#averageHue=%23fdfcfb&clientId=ub55697b5-e089-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=394&id=u18cd6f3b&margin=%5Bobject%20Object%5D&name=image.png&originHeight=710&originWidth=1160&originalType=binary&ratio=1&rotation=0&showTitle=false&size=92244&status=done&style=none&taskId=u4379ab89-75ba-4568-87fe-68c4e1cc8c4&title=&width=644.4444615163925)
## **10.2 线程池参数说明 **
本次介绍 5 种类型的线程池 
### **10.2.1 常用参数(重点) **
• corePoolSize 线程池的核心线程数 
• maximumPoolSize 能容纳的最大线程数 
• keepAliveTime 空闲线程存活时间 
• unit 存活的时间单位 
• workQueue 存放提交但未执行任务的队列 
• threadFactory 创建线程的工厂类 
• handler 等待队列满后的拒绝策略 
线程池中，有三个重要的参数，决定影响了拒绝策略：corePoolSize - 核心线 
程数，也即最小的线程数。workQueue - 阻塞队列 。 maximumPoolSize - 
最大线程数 
当提交任务数大于 corePoolSize 的时候，会优先将任务放到 workQueue 阻 
塞队列中。当阻塞队列饱和后，会扩充线程池中线程数，直到达到maximumPoolSize 最大线程数配置。此时，再多余的任务，则会触发线程池 
的拒绝策略了。 
总结起来，也就是一句话，**当提交的任务数大于（workQueue.size() + **
**maximumPoolSize ），就会触发线程池的拒绝策略**。 
### **10.2.2 拒绝策略(重点) **
**CallerRunsPolicy**: 当触发拒绝策略，只要线程池没有关闭的话，则使用调用 
线程直接运行任务。一般并发比较小，性能要求不高，不允许失败。但是，由 
于调用者自己运行任务，如果任务提交速度过快，可能导致程序阻塞，性能效 
率上必然的损失较大 
**AbortPolicy**: 丢弃任务，并抛出拒绝执行 RejectedExecutionException 异常 
信息。线程池默认的拒绝策略。必须处理好抛出的异常，否则会打断当前的执 
行流程，影响后续的任务执行。 
**DiscardPolicy**: 直接丢弃，其他啥都没有 
**DiscardOldestPolicy**: 当触发拒绝策略，只要线程池没有关闭的话，丢弃阻塞 
队列 workQueue 中最老的一个任务，并将新任务加入 
## **10.3 线程池的种类与创建 **
### **10.3.1 newCachedThreadPool(常用) **
#### **作用**：
创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空 
闲线程，若无可回收，则新建线程. 
#### **特点**: 
• 线程池中数量没有固定，可达到最大值（Interger. MAX_VALUE） 
• 线程池中的线程可进行缓存重复利用和回收（回收默认时间为 1 分钟） 
• 当线程池中，没有可用线程，会重新创建一个线程 
#### **创建方式：**
```
    /**
     * 可缓存线程池
     * @return
     */
    public static ExecutorService newCachedThreadPool(){
/**
 * corePoolSize 线程池的核心线程数
 * maximumPoolSize 能容纳的最大线程数
 * keepAliveTime 空闲线程存活时间
 * unit 存活的时间单位
 * workQueue 存放提交但未执行任务的队列
 * threadFactory 创建线程的工厂类:可以省略
 * handler 等待队列满后的拒绝策略:可以省略
 */
        return new ThreadPoolExecutor(0,
                Integer.MAX_VALUE,
                60L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
```
#### **场景:**
** **适用于创建一个可无限扩大的线程池，服务器负载压力较轻，执行时间较 
短，任务多的场景 
### **10.3.2 newFixedThreadPool(常用) **
#### **作用**：
创建一个可重用固定线程数的线程池，以共享的无界队列方式来运行这 
些线程。在任意点，在大多数线程会处于处理任务的活动状态。如果在所有线 
程处于活动状态时提交附加任务，则在有可用线程之前，附加任务将在队列中 
等待。如果在关闭前的执行期间由于失败而导致任何线程终止，那么一个新线 
程将代替它执行后续的任务（如果需要）。在某个线程被显式地关闭之前，池 
中的线程将一直存在。**特征： **
• 线程池中的线程处于一定的量，可以很好的控制线程的并发量 
• 线程可以重复被使用，在显示关闭之前，都将一直存在 
• 超出一定量的线程被提交时候需在队列中等待 
#### **创建方式**：
```
    /**
     * 固定长度线程池
     * @return
     */
    public static ExecutorService newFixedThreadPool(){
/**
 * corePoolSize 线程池的核心线程数
 * maximumPoolSize 能容纳的最大线程数
 * keepAliveTime 空闲线程存活时间
 * unit 存活的时间单位
 * workQueue 存放提交但未执行任务的队列
 * threadFactory 创建线程的工厂类:可以省略
 * handler 等待队列满后的拒绝策略:可以省略
 */
        return new ThreadPoolExecutor(10,
                10,
                0L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
```
#### **场景: **
适用于可以预测线程数量的业务中，或者服务器负载较重，对线程数有严 
格限制的场景 
### **10.3.3 newSingleThreadExecutor(常用)**
#### **作用**：
创建一个使用单个 worker 线程的 Executor，以无界队列方式来运行该 
线程。（注意，如果因为在关闭前的执行期间出现失败而终止了此单个线程， 
那么如果需要，一个新线程将代替它执行后续的任务）。可保证顺序地执行各 
个任务，并且在任意给定的时间不会有多个线程是活动的。与其他等效的 
newFixedThreadPool 不同，可保证无需重新配置此方法所返回的执行程序即 
可使用其他的线程。 
#### **特征： **
线程池中最多执行 1 个线程，之后提交的线程活动将会排在队列中以此 
执行 
#### **创建方式：**
```
    /**
     * 单一线程池
     * @return
     */
    public static ExecutorService newSingleThreadExecutor(){
/**
 * corePoolSize 线程池的核心线程数
 * maximumPoolSize 能容纳的最大线程数
 * keepAliveTime 空闲线程存活时间
 * unit 存活的时间单位
 * workQueue 存放提交但未执行任务的队列
 * threadFactory 创建线程的工厂类:可以省略
 * handler 等待队列满后的拒绝策略:可以省略
 */
        return new ThreadPoolExecutor(1,
                1,
                0L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
```
#### **场景: **
适用于需要保证顺序执行各个任务，并且在任意时间点，不会同时有多个 
线程的场景 
### **10.3.4 newScheduleThreadPool(了解) **
#### **作用: **
线程池支持定时以及周期性执行任务，创建一个 corePoolSize 为传入参 
数，最大线程数为整形的最大数的线程池** 
#### **特征: **
> （1）线程池中具有指定数量的线程，即便是空线程也将保留 
> （2）可定时或者延迟执行线程活动 

#### **创建方式:**
```
   public static ScheduledExecutorService newScheduledThreadPool(int
                                                                          corePoolSize,
                                                                  ThreadFactory threadFactory) {
        return new ScheduledThreadPoolExecutor(corePoolSize,
                threadFactory);
    }
```
#### **场景: **
适用于需要多个后台线程执行周期任务的场景 
### **10.3.5 newWorkStealingPool **
jdk1.8 提供的线程池，底层使用的是 ForkJoinPool 实现，创建一个拥有多个 
任务队列的线程池，可以减少连接数，创建当前可用 cpu 核数的线程来并行执 
行任务 
#### **创建方式:**
```
    public static ExecutorService newWorkStealingPool(int parallelism) {
/**
 * parallelism：并行级别，通常默认为 JVM 可用的处理器个数
 * factory：用于创建 ForkJoinPool 中使用的线程。
 * handler：用于处理工作线程未处理的异常，默认为 null
 * asyncMode：用于控制 WorkQueue 的工作模式:队列---反队列
 */
        return new ForkJoinPool(parallelism,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                null,
          
```
#### **场景: **
适用于大耗时，可并行执行的场景 
## **10.4 线程池入门案例 **
> #### **场景: 火车站 3 个售票口, 10 个用户买票**

```
package com.xingchen.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xing'chen
 */ //演示线程池三种常用分类
public class ThreadPoolDemo1 {
    public static void main(String[] args) {
        //一池五线程
        ExecutorService threadPool1 = Executors.newFixedThreadPool(5); //5个窗口

        //一池一线程
        ExecutorService threadPool2 = Executors.newSingleThreadExecutor(); //一个窗口

        //一池可扩容线程
        ExecutorService threadPool3 = Executors.newCachedThreadPool();
        //10个顾客请求
        try {
            for (int i = 1; i <=10; i++) {
                //执行
                threadPool3.execute(()->{
                    System.out.println(Thread.currentThread().getName()+" 办理业务");
                });
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关闭
            threadPool3.shutdown();
        }

    }

}

```
## **10.5 线程池底层工作原理(重要)**
![image.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1671010792790-0118e617-8b7f-4127-95dd-9c3612a92d80.png#averageHue=%23eff1ee&clientId=ub55697b5-e089-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=432&id=ueb71a30d&margin=%5Bobject%20Object%5D&name=image.png&originHeight=777&originWidth=1171&originalType=binary&ratio=1&rotation=0&showTitle=false&size=317391&status=done&style=none&taskId=u224c089d-09eb-45a7-8dfb-16564158d3f&title=&width=650.5555727893928)

1. 在创建了线程池后，线程池中的线程数为零
2. 当调用 execute()方法添加一个请求任务时，线程池会做出如下判断： 2.1 如
果正在运行的线程数量小于 corePoolSize，那么马上创建线程运行这个任务；
2.2 如果正在运行的线程数量大于或等于 corePoolSize，那么将这个任务放入
队列； 2.3 如果这个时候队列满了且正在运行的线程数量还小于
maximumPoolSize，那么还是要创建非核心线程立刻运行这个任务； 2.4 如
果队列满了且正在运行的线程数量大于或等于 maximumPoolSize，那么线程
池会启动饱和拒绝策略来执行。
3. 当一个线程完成任务时，它会从队列中取下一个任务来执行
4. 当一个线程无事可做超过一定的时间（keepAliveTime）时，线程会判断：
4.1 如果当前运行的线程数大于 corePoolSize，那么这个线程就被停掉。 4.2
所以线程池的所有任务完成后，它最终会收缩到 corePoolSize 的大小。
5. ![image.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1671010816427-62e89c77-03f6-406f-a39c-b3416ec1a04e.png#averageHue=%23eeeeee&clientId=ub55697b5-e089-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=186&id=u816f3679&margin=%5Bobject%20Object%5D&name=image.png&originHeight=334&originWidth=1126&originalType=binary&ratio=1&rotation=0&showTitle=false&size=122677&status=done&style=none&taskId=u58df7bcb-3f94-4a2b-a82f-45dd611cf0e&title=&width=625.5555721271189)
## 10.6 注意事项(重要)

1. 项目中创建多线程时，使用常见的三种线程池创建方式，单一、可变、定长都
有一定问题，原因是 FixedThreadPool 和 SingleThreadExecutor 底层都是用
LinkedBlockingQueue 实现的，这个队列最大长度为 Integer.MAX_VALUE，
容易导致 OOM。所以实际生产一般自己通过 ThreadPoolExecutor 的 7 个参
数，自定义线程池
2. 创建线程池推荐适用 ThreadPoolExecutor 及其 7 个参数手动创建
o corePoolSize 线程池的核心线程数
o maximumPoolSize 能容纳的最大线程数
o keepAliveTime 空闲线程存活时间
o unit 存活的时间单位
o workQueue 存放提交但未执行任务的队列
o threadFactory 创建线程的工厂类
o handler 等待队列满后的拒绝策略
3. 为什么不允许适用不允许 Executors.的方式手动创建线程池,如下图

![image.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1671010861950-aaa779c5-ec79-4499-87de-aab6fc40256f.png#averageHue=%23e7e6e1&clientId=ub55697b5-e089-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=227&id=u74bfd4e8&margin=%5Bobject%20Object%5D&name=image.png&originHeight=408&originWidth=1186&originalType=binary&ratio=1&rotation=0&showTitle=false&size=375316&status=done&style=none&taskId=u462c040e-64b9-4653-b7e1-b1f2d0fb1d5&title=&width=658.8889063434841)

