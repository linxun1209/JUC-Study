> **课程内容概览 **
> • 1、什么是 JUC 
> • 2、Lock 接口 
> • 3、线程间通信 
> • 4、集合的线程安全 
> • 5、多线程锁 
> • 6、Callable 接口 
> • 7、JUC 三大辅助类: CountDownLatch CyclicBarrier Semaphore 
> • 8、读写锁: ReentrantReadWriteLock 
> • 9、阻塞队列 
> • 10、ThreadPool 线程池 
> • 11、Fork/Join 框架 
> • 12、CompletableFuture\

# juc
## **1.1 JUC 简介 **
在 Java 中，线程部分是一个重点，本篇文章说的 JUC 也是关于线程的。JUC 
就是 java.util .concurrent 工具包的简称。这是一个处理线程的工具包，JDK 
1.5 开始出现的。
![image.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670917975455-1f209c8e-c9c8-4e5b-a2be-675c2c30cf4b.png#averageHue=%23e0ba89&clientId=u8dd6fbe7-1d4b-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=319&id=udf4e0322&margin=%5Bobject%20Object%5D&name=image.png&originHeight=574&originWidth=1144&originalType=binary&ratio=1&rotation=0&showTitle=false&size=207263&status=done&style=none&taskId=uaa164e75-b9db-4185-8a47-65af5389600&title=&width=635.5555723920285)
## **1.2 进程与线程 **
### **进程（Process） **
是计算机中的程序关于某数据集合上的一次运行活动，是系 
统进行资源分配和调度的基本单位，是操作系统结构的基础。 在当代面向线程 
设计的计算机结构中，进程是线程的容器。程序是指令、数据及其组织形式的 
描述，进程是程序的实体。是计算机中的程序关于某数据集合上的一次运行活 
动，是系统进行资源分配和调度的基本单位，是操作系统结构的基础。程序是 
指令、数据及其组织形式的描述，进程是程序的实体。
### **线程（thread） **
是操作系统能够进行运算调度的最小单位。它被包含在进程之 
中，是进程中的实际运作单位。一条线程指的是进程中一个单一顺序的控制流， 
一个进程中可以并发多个线程，每条线程并行执行不同的任务。
### **总结来说: **
进程：指在系统中正在运行的一个应用程序；程序一旦运行就是进程；进程— 
—资源分配的最小单位。 
线程：系统分配处理器时间资源的基本单元，或者说进程之内独立执行的一个 
单元执行流。线程——程序执行的最小单位。
## **1.3 线程的状态 **
### **1.3.1 线程状态枚举类 **
**Thread.State**
```
public enum State {
/**
* Thread state for a thread which has not yet started.
*/
NEW,(新建)
/**
* Thread state for a runnable thread. A thread in the runnable
* state is executing in the Java virtual machine but it may
* be waiting for other resources from the operating system
* such as processor.
*/
RUNNABLE,（准备就绪）
/**
* Thread state for a thread blocked waiting for a monitor lock.
* A thread in the blocked state is waiting for a monitor lock
* to enter a synchronized block/method or
* reenter a synchronized block/method after calling
* {@link Object#wait() Object.wait}.
*/
BLOCKED,（阻塞）
/**
* Thread state for a waiting thread.
* A thread is in the waiting state due to calling one of the
* following methods:
* <ul>
* <li>{@link Object#wait() Object.wait} with no timeout</li>
* <li>{@link #join() Thread.join} with no timeout</li>
* <li>{@link LockSupport#park() LockSupport.park}</li>
* </ul>
*
* <p>A thread in the waiting state is waiting for another thread to
* perform a particular action.
*
* For example, a thread that has called <tt>Object.wait()</tt>
* on an object is waiting for another thread to call
* <tt>Object.notify()</tt> or <tt>Object.notifyAll()</tt> on
* that object. A thread that has called <tt>Thread.join()</tt>
* is waiting for a specified thread to terminate.
*/
WAITING,（不见不散）
/**
* Thread state for a waiting thread with a specified waiting time.
* A thread is in the timed waiting state due to calling one of
* the following methods with a specified positive waiting time:
* <ul>
* <li>{@link #sleep Thread.sleep}</li>
* <li>{@link Object#wait(long) Object.wait} with timeout</li>
* <li>{@link #join(long) Thread.join} with timeout</li>
* <li>{@link LockSupport#parkNanos LockSupport.parkNanos}</li>
* <li>{@link LockSupport#parkUntil LockSupport.parkUntil}</li>
* </ul>
*/
TIMED_WAITING,（过时不候）
/**
* Thread state for a terminated thread.
* The thread has completed execution.
*/
TERMINATED;(终结
```
### **1.3.2 wait/sleep 的区别 **
（1）sleep 是 Thread 的静态方法，wait 是 Object 的方法，任何对象实例都 
能调用。 
（2）sleep 不会释放锁，它也不需要占用锁。wait 会释放锁，但调用它的前提 
是当前线程占有锁(即代码要在 synchronized 中)。 
（3）它们都可以被 interrupted 方法中断。 
## **1.4 并发与并行 **
### **1.4.1 串行模式 **
串行表示所有任务都一一按先后顺序进行。串行意味着必须先装完一车柴才能 
运送这车柴，只有运送到了，才能卸下这车柴，并且只有完成了这整个三个步 
骤，才能进行下一个步骤。 
**串行是一次只能取得一个任务，并执行这个任务**。 
### **1.4.2 并行模式 **
并行意味着可以同时取得多个任务，并同时去执行所取得的这些任务。并行模 
式相当于将长长的一条队列，划分成了多条短队列，所以并行缩短了任务队列的长度。并行的效率从代码层次上强依赖于多进程/多线程代码，从硬件角度上 
则依赖于多核 CPU。 
### **1.4.3 并发 **
**并发(concurrent)指的是多个程序可以同时运行的现象，更细化的是多进程可 **
**以同时运行或者多指令可以同时运行**。但这不是重点，在描述并发的时候也不 
会去扣这种字眼是否精确，==并发的重点在于它是一种现象==, ==并发描述 
的是多进程同时运行的现象==。但实际上，对于单核心 CPU 来说，同一时刻 
只能运行一个线程。所以，这里的"同时运行"表示的不是真的同一时刻有多个 
线程运行的现象，这是并行的概念，而是提供一种功能让用户看来多个程序同 
时运行起来了，但实际上这些程序中的进程不是一直霸占 CPU 的，而是执行一 
会停一会。 
**要解决大并发问题，通常是将大任务分解成多个小任务**, 由于操作系统对进程的 
调度是随机的，所以切分成多个小任务后，可能会从任一小任务处执行。这可 
能会出现一些现象： 
• 可能出现一个小任务执行了多次，还没开始下个任务的情况。这时一般会采用 
队列或类似的数据结构来存放各个小任务的成果 
• 可能出现还没准备好第一步就执行第二步的可能。这时，一般采用多路复用或 
异步的方式，比如只有准备好产生了事件通知才执行某个任务。 
• 可以多进程/多线程的方式并行执行这些小任务。也可以单进程/单线程执行这 
些小任务，这时很可能要配合多路复用才能达到较高的效率 
### **1.4.4 小结(重点) **
**并发：**同一时刻多个线程在访问同一个资源，多个线程对一个点 
 例子：春运抢票 电商秒杀... 
**并行：**多项工作一起执行，之后再汇总 
 例子：泡方便面，电水壶烧水，一边撕调料倒入桶中 
### **1.5 管程 **
管程(monitor)是保证了同一时刻只有一个进程在管程内活动,即管程内定义的操作在同 
一时刻只被一个进程调用(由编译器实现).但是这样并不能保证进程以设计的顺序执行 
JVM 中同步是基于进入和退出管程(monitor)对象实现的，每个对象都会有一个管程 
(monitor)对象，管程(monitor)会随着 java 对象一同创建和销毁 
执行线程首先要持有管程对象，然后才能执行方法，当方法完成之后会释放管程，方 
法在执行时候会持有管程，其他线程无法再获取同一个管程 
### **1.6 用户线程和守护线程 **
**用户线程:**平时用到的普通线程,自定义线程 
**守护线程:**运行在后台,是一种特殊的线程,比如垃圾回收 
**当主线程结束后,用户线程还在运行,JVM 存活 **
**如果没有用户线程,都是守护线程,JVM 结束**
