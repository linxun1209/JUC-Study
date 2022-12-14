# **5 多线程锁 **
## **5.1 锁的八个问题演示 **
```
package com.xingchen.sync;

import java.util.concurrent.TimeUnit;

class Phone {

    public static synchronized void sendSMS() throws Exception {
        //停留4秒
        TimeUnit.SECONDS.sleep(4);
        System.out.println("------sendSMS");
    }

    public synchronized void sendEmail() throws Exception {
        System.out.println("------sendEmail");
    }

    public void getHello() {
        System.out.println("------getHello");
    }
}

/**
 * @author xing'chen
 * @Description: 8锁
 *
1 标准访问，先打印短信还是邮件
------sendSMS
------sendEmail

2 停4秒在短信方法内，先打印短信还是邮件
------sendSMS
------sendEmail

3 新增普通的hello方法，是先打短信还是hello
------getHello
------sendSMS

4 现在有两部手机，先打印短信还是邮件
------sendEmail
------sendSMS

5 两个静态同步方法，1部手机，先打印短信还是邮件
------sendSMS
------sendEmail

6 两个静态同步方法，2部手机，先打印短信还是邮件
------sendSMS
------sendEmail

7 1个静态同步方法,1个普通同步方法，1部手机，先打印短信还是邮件
------sendEmail
------sendSMS

8 1个静态同步方法,1个普通同步方法，2部手机，先打印短信还是邮件
------sendEmail
------sendSMS

 */

public class Lock_8 {
    public static void main(String[] args) throws Exception {

        Phone phone = new Phone();
        Phone phone2 = new Phone();

        new Thread(() -> {
            try {
                phone.sendSMS();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "AA").start();

        Thread.sleep(100);

        new Thread(() -> {
            try {
               // phone.sendEmail();
               // phone.getHello();
                phone2.sendEmail();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "BB").start();
    }
}

```
## **结论: **
一个对象里面如果有多个 synchronized 方法，某一个时刻内，只要一个线程去调用其中的 
一个 synchronized 方法了， 
其它的线程都只能等待，换句话说，某一个时刻内，只能有唯一一个线程去访问这些 
synchronized 方法 
锁的是当前对象 this，被锁定后，其它的线程都不能进入到当前对象的其它的 
synchronized 方法 
加个普通方法后发现和同步锁无关 
换成两个对象后，不是同一把锁了，情况立刻变化。 
synchronized 实现同步的基础：Java 中的每一个对象都可以作为锁。 
## **具体表现为以下 3 种形式。 **

- **对于普通同步方法，锁是当前实例对象。 **
- **对于静态同步方法，锁是当前类的 Class 对象。 **
- **对于同步方法块，锁是 Synchonized 括号里配置的对象 **

当一个线程试图访问同步代码块时，它首先必须得到锁，退出或抛出异常时必须释放锁。 
也就是说如果一个实例对象的非静态同步方法获取锁后，该实例对象的其他非静态同步方法必须等待获取锁的方法释放锁后才能获取锁，可是别的实例对象的非静态同步方法因为跟该实例对象的非静态同步方法用的是不同的锁，所以毋须等待该实例对象已获取锁的非静态同步方法释放锁就可以获取他们自己的锁。所有的静态同步方法用的也是同一把锁——类对象本身，这两把锁是两个不同的对象，所以静态同步方法与非静态同步方法之间是不会有竞态条件的。但是一旦一个静态同步方法获取锁后，其他的静态同步方法都必须等待该方法释放锁后才能获取锁，而不管是同一个实例对象的静态同步方法之间，还是不同的实例对象的静态同步方法之间，只要它们同一个类的实例对象！
# **6 Callable&Future 接口**
## **6.1 Callable 接口 **
目前我们学习了有两种创建线程的方法-一种是通过创建 Thread 类，另一种是 
通过使用 Runnable 创建线程。但是，Runnable 缺少的一项功能是，当线程 
终止时（即 run（）完成时），我们无法使线程返回结果。为了支持此功能， 
Java 中提供了 Callable 接口。 
==**现在我们学习的是创建线程的第三种方案---Callable 接口**== 
**Callable 接口的特点如下(重点) **
• 为了实现 Runnable，需要实现不返回任何内容的 run（）方法，而对于 
Callable，需要实现在完成时返回结果的 call（）方法。 
• call（）方法可以引发异常，而 run（）则不能。 
• 为实现 Callable 而必须重写 call 方法 
• 不能直接替换 runnable,因为 Thread 类的构造方法根本没有 Callable
```
    创建新类 MyThread 实现 runnable 接口
class MyThread implements Runnable{
    @Override
    public void run() {
    }
}
    新类 MyThread2 实现 callable 接口
class MyThread2 implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        return 200;
    }
}
```
## **6.2 Future 接口 **
当 call（）方法完成时，结果必须存储在主线程已知的对象中，以便主线程可 
以知道该线程返回的结果。为此，可以使用 Future 对象。将 Future 视为保存结果的对象–它可能暂时不保存结果，但将来会保存（一旦 
Callable 返回）。Future 基本上是主线程可以跟踪进度以及其他线程的结果的 
一种方式。要实现此接口，必须重写 5 种方法，这里列出了重要的方法,如下: 
• **public boolean cancel（boolean mayInterrupt）：**用于停止任务。 
==如果尚未启动，它将停止任务。如果已启动，则仅在 mayInterrupt 为 true 
时才会中断任务。== 
• **public Object get（）抛出 InterruptedException，ExecutionException： **
用于获取任务的结果。 
==如果任务完成，它将立即返回结果，否则将等待任务完成，然后返回结果。 
== 
• **public boolean isDone（）：**如果任务完成，则返回 true，否则返回 false 
可以看到 Callable 和 Future 做两件事-Callable 与 Runnable 类似，因为它封 
装了要在另一个线程上运行的任务，而 Future 用于存储从另一个线程获得的结 
果。实际上，future 也可以与 Runnable 一起使用。 
要创建线程，需要 Runnable。为了获得结果，需要 future。 
## **6.3 FutureTask **
Java 库具有具体的 FutureTask 类型，该类型实现 Runnable 和 Future，并方 
便地将两种功能组合在一起。 可以通过为其构造函数提供 Callable 来创建 
FutureTask。然后，将 FutureTask 对象提供给 Thread 的构造函数以创建 
Thread 对象。因此，间接地使用 Callable 创建线程。 
**核心原理:(重点) **
在主线程中需要执行比较耗时的操作时，但又不想阻塞主线程时，可以把这些 
作业交给 Future 对象在后台完成 
• 当主线程将来需要时，就可以通过 Future 对象获得后台作业的计算结果或者执 
行状态• 一般 FutureTask 多用于耗时的计算，主线程可以在完成自己的任务后，再去 
获取结果。 
• 仅在计算完成时才能检索结果；如果计算尚未完成，则阻塞 get 方法 
• 一旦计算完成，就不能再重新开始或取消计算 
• get 方法而获取结果只有在计算完成时获取，否则会一直阻塞直到任务转入完 
成状态，然后会返回结果或者抛出异常 
• get 只计算一次,因此 get 方法放到最后 
**demo 案例 **
## **6.4 使用 Callable 和 Future **
CallableDemo 案例
```
/**
 * CallableDemo 案列
 */
public class CallableDemo {
    /**
     * 实现 runnable 接口
     */
    static class MyThread1 implements Runnable{
        /**
         * run 方法
         */
        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + "线程进入了 run
                        方法");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /**
     * 实现 callable 接口
     */
    static class MyThread2 implements Callable{
        /**
         * call 方法
         * @return
         * @throws Exception
         */
        @Override
        public Long call() throws Exception {
            try {
                System.out.println(Thread.currentThread().getName() + "线程进入了 call
                        方法,开始准备睡觉");
                        Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + "睡醒了");
            }catch (Exception e){
                e.printStackTrace();
            }
            return System.currentTimeMillis();
        }
    }
    public static void main(String[] args) throws Exception{
        //声明 runable
        Runnable runable = new MyThread1();
        //声明 callable
        Callable callable = new MyThread2();
        //future-callable
        FutureTask<Long> futureTask2 = new FutureTask(callable);
        //线程二
        new Thread(futureTask2, "线程二").start();
        for (int i = 0; i < 10; i++) {
            Long result1 = futureTask2.get();
            System.out.println(result1);
        }
        //线程一
        new Thread(runable,"线程一").start();
    }
}
```
## **6.5 小结(重点) **
• 在主线程中需要执行比较耗时的操作时，但又不想阻塞主线程时，可以把这些 
作业交给 Future 对象在后台完成, 当主线程将来需要时，就可以通过 Future 
对象获得后台作业的计算结果或者执行状态• 一般 FutureTask 多用于耗时的计算，主线程可以在完成自己的任务后，再去 
获取结果 
• 仅在计算完成时才能检索结果；如果计算尚未完成，则阻塞 get 方法。一旦计 
算完成，就不能再重新开始或取消计算。get 方法而获取结果只有在计算完成 
时获取，否则会一直阻塞直到任务转入完成状态，然后会返回结果或者抛出异 
常。 
• 只计算一次









# 文件的所有图片

![01-进程和线程概念.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936570879-83a177da-db10-481c-92e5-c5bff98b26c4.png#averageHue=%23fdfdfd&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=368&id=uc61c4589&margin=%5Bobject%20Object%5D&name=01-%E8%BF%9B%E7%A8%8B%E5%92%8C%E7%BA%BF%E7%A8%8B%E6%A6%82%E5%BF%B5.png&originHeight=662&originWidth=1350&originalType=binary&ratio=1&rotation=0&showTitle=false&size=16944&status=done&style=none&taskId=udd048482-f81e-435d-a367-048f53d2e0b&title=&width=750.0000198682154)![02-创建线程方式.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936570878-b0c731a4-8e79-46de-851a-10842c4edea1.png#averageHue=%23fdfdfd&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=319&id=ubc0fcbde&margin=%5Bobject%20Object%5D&name=02-%E5%88%9B%E5%BB%BA%E7%BA%BF%E7%A8%8B%E6%96%B9%E5%BC%8F.png&originHeight=574&originWidth=1351&originalType=binary&ratio=1&rotation=0&showTitle=false&size=11883&status=done&style=none&taskId=u98f97390-1e83-4e7a-99b6-93866b96fa4&title=&width=750.5555754384882)![03-多线程编程步骤.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936570882-bd7e4127-cd5e-4d13-a1b5-eb917bc54698.png#averageHue=%23fdfdfd&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=319&id=u7699647e&margin=%5Bobject%20Object%5D&name=03-%E5%A4%9A%E7%BA%BF%E7%A8%8B%E7%BC%96%E7%A8%8B%E6%AD%A5%E9%AA%A4.png&originHeight=574&originWidth=1351&originalType=binary&ratio=1&rotation=0&showTitle=false&size=18506&status=done&style=none&taskId=ua5dbe731-f14d-4735-ac8d-7fc8d1d77a9&title=&width=750.5555754384882)![04-线程定制化通信.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936570904-9aeb12c0-70aa-43ef-91e1-0707636164e0.png#averageHue=%23faf9f8&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=319&id=u75e04104&margin=%5Bobject%20Object%5D&name=04-%E7%BA%BF%E7%A8%8B%E5%AE%9A%E5%88%B6%E5%8C%96%E9%80%9A%E4%BF%A1.png&originHeight=574&originWidth=1351&originalType=binary&ratio=1&rotation=0&showTitle=false&size=18242&status=done&style=none&taskId=ue2953bce-22e6-42b0-9c69-ef1fa462a51&title=&width=750.5555754384882)![05-乐观锁和悲观锁.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936571012-1e296555-1832-46d5-acfa-093523ff39a6.png#averageHue=%23faf5f0&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=319&id=u36fac18b&margin=%5Bobject%20Object%5D&name=05-%E4%B9%90%E8%A7%82%E9%94%81%E5%92%8C%E6%82%B2%E8%A7%82%E9%94%81.png&originHeight=574&originWidth=1351&originalType=binary&ratio=1&rotation=0&showTitle=false&size=191988&status=done&style=none&taskId=ub19f44e5-bf9d-46bc-800a-156e649ceee&title=&width=750.5555754384882)![06-futureTask.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936571598-1162bcf3-06e8-4f3a-9c4b-d6901470baaa.png#averageHue=%23fdfdfd&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=319&id=u526426af&margin=%5Bobject%20Object%5D&name=06-futureTask.png&originHeight=574&originWidth=1351&originalType=binary&ratio=1&rotation=0&showTitle=false&size=14282&status=done&style=none&taskId=ue811f4d1-c531-46d4-bb43-f54cbcfa0a2&title=&width=750.5555754384882)![07-阻塞队列.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936571796-03f1989e-f82f-4047-914e-31be1ef136aa.png#averageHue=%23fefcfa&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=368&id=u77b208ee&margin=%5Bobject%20Object%5D&name=07-%E9%98%BB%E5%A1%9E%E9%98%9F%E5%88%97.png&originHeight=662&originWidth=1350&originalType=binary&ratio=1&rotation=0&showTitle=false&size=76890&status=done&style=none&taskId=u0294415f-9310-43c3-a8f6-ac37cba9601&title=&width=750.0000198682154)![08-读写锁.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936571793-1889480e-6907-4ef3-a4f4-a8d87ed6796b.png#averageHue=%23fefcfb&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=368&id=u088cde34&margin=%5Bobject%20Object%5D&name=08-%E8%AF%BB%E5%86%99%E9%94%81.png&originHeight=662&originWidth=1350&originalType=binary&ratio=1&rotation=0&showTitle=false&size=21474&status=done&style=none&taskId=u4a70feaa-dff2-45f0-b017-4c42c567d4c&title=&width=750.0000198682154)![09-线程池七个参数.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936571813-5f056797-a491-4a82-8b92-af8050e15824.png#averageHue=%23fcfbfb&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=368&id=uc9b9b7e4&margin=%5Bobject%20Object%5D&name=09-%E7%BA%BF%E7%A8%8B%E6%B1%A0%E4%B8%83%E4%B8%AA%E5%8F%82%E6%95%B0.png&originHeight=662&originWidth=1350&originalType=binary&ratio=1&rotation=0&showTitle=false&size=13128&status=done&style=none&taskId=uc8a5f41e-53a0-40f3-839b-a5f12cebec5&title=&width=750.0000198682154)![10-线程池底层工作流程.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936571902-090584b5-7a2c-4972-b3a3-ffaa7915a2ca.png#averageHue=%23fefdfc&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=368&id=u1d506b75&margin=%5Bobject%20Object%5D&name=10-%E7%BA%BF%E7%A8%8B%E6%B1%A0%E5%BA%95%E5%B1%82%E5%B7%A5%E4%BD%9C%E6%B5%81%E7%A8%8B.png&originHeight=662&originWidth=1350&originalType=binary&ratio=1&rotation=0&showTitle=false&size=97727&status=done&style=none&taskId=u5efd18eb-b4f7-486f-ab54-e809b9c3a3a&title=&width=750.0000198682154)![11-分支合并框架.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936572611-f7464b0e-e53a-4c1e-8060-107eb47378a1.png#averageHue=%23fefefe&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=368&id=u20595db3&margin=%5Bobject%20Object%5D&name=11-%E5%88%86%E6%94%AF%E5%90%88%E5%B9%B6%E6%A1%86%E6%9E%B6.png&originHeight=662&originWidth=1350&originalType=binary&ratio=1&rotation=0&showTitle=false&size=12565&status=done&style=none&taskId=u73826846-d3ba-48dc-a8e1-8783afe21fd&title=&width=750.0000198682154)![12-写时复制技术.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936572749-5283027d-8f64-4dec-9abb-0d8b7a1fab3b.png#averageHue=%23fcfcfc&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=319&id=uc671d8f8&margin=%5Bobject%20Object%5D&name=12-%E5%86%99%E6%97%B6%E5%A4%8D%E5%88%B6%E6%8A%80%E6%9C%AF.png&originHeight=574&originWidth=1351&originalType=binary&ratio=1&rotation=0&showTitle=false&size=16905&status=done&style=none&taskId=ub68988fe-5488-46af-9ca7-ad9dd587a72&title=&width=750.5555754384882)![13-可重入锁.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936572791-4dfdd463-0c3c-486e-9aac-0034fc8ac4e6.png#averageHue=%23fdfdfd&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=368&id=ucfc87b9e&margin=%5Bobject%20Object%5D&name=13-%E5%8F%AF%E9%87%8D%E5%85%A5%E9%94%81.png&originHeight=662&originWidth=1350&originalType=binary&ratio=1&rotation=0&showTitle=false&size=12451&status=done&style=none&taskId=udf2e10b8-7477-42b0-90d1-e9f855bf984&title=&width=750.0000198682154)![14-读写锁演变.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936572800-520434c5-d48a-4d74-9d9e-d2e1e6c5b921.png#averageHue=%23fefcfa&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=368&id=u54a0d322&margin=%5Bobject%20Object%5D&name=14-%E8%AF%BB%E5%86%99%E9%94%81%E6%BC%94%E5%8F%98.png&originHeight=662&originWidth=1350&originalType=binary&ratio=1&rotation=0&showTitle=false&size=23847&status=done&style=none&taskId=u779f0275-7a30-43b3-bf92-c0fdf980a89&title=&width=750.0000198682154)![15-读写锁降级.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936572872-dcec92d6-601d-451e-90e0-bbe82c2001aa.png#averageHue=%23fdfdfd&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=368&id=u369ad460&margin=%5Bobject%20Object%5D&name=15-%E8%AF%BB%E5%86%99%E9%94%81%E9%99%8D%E7%BA%A7.png&originHeight=662&originWidth=1350&originalType=binary&ratio=1&rotation=0&showTitle=false&size=14984&status=done&style=none&taskId=ue32ab06e-8bb9-4662-b9b3-2fb544f0ec6&title=&width=750.0000198682154)![16-死锁.png](https://cdn.nlark.com/yuque/0/2022/png/33764834/1670936573461-99fdfcfe-0973-4dd1-984f-600f0fa1beed.png#averageHue=%23fafafa&clientId=u8035a014-9ebc-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=368&id=ud5769e5b&margin=%5Bobject%20Object%5D&name=16-%E6%AD%BB%E9%94%81.png&originHeight=662&originWidth=1350&originalType=binary&ratio=1&rotation=0&showTitle=false&size=26759&status=done&style=none&taskId=u9ae71453-2daa-48df-9173-0805e94852b&title=&width=750.0000198682154)
