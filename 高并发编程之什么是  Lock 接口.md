# **2 Lock 接口 **
## **2.1 Synchronized **
### **2.1.1 Synchronized 关键字回顾 **
**synchronized** 是 Java 中的关键字，是一种同步锁。它修饰的对象有以下几种： 
**1. 修饰一个代码块**，
被修饰的代码块称为同步语句块，其作用的范围是大括号{} 
括起来的代码，作用的对象是调用这个代码块的对象； 
**2. 修饰一个方法**，
被修饰的方法称为同步方法，其作用的范围是整个方法，作用 
的对象是调用这个方法的对象； o 虽然可以使用 synchronized 来定义方法，但 synchronized 并不属于方法定 义的一部分，因此，synchronized 关键字不能被继承。如果在父类中的某个方 法使用了 synchronized 关键字，而在子类中覆盖了这个方法，在子类中的这 个方法默认情况下并不是同步的，而必须显式地在子类的这个方法中加上 synchronized 关键字才可以。当然，还可以在子类方法中调用父类中相应的方 法，这样虽然子类中的方法不是同步的，但子类调用了父类的同步方法，因此， 子类的方法也就相当于同步了。
**3. 修改一个静态的方法**，其作用的范围是整个静态方法，作用的对象是这个类的 所有对象； 
**4. 修改一个类，**
其作用的范围是 synchronized 后面括号括起来的部分，作用主 
的对象是这个类的所有对象。 
### **2.1.2 售票案例 **
```
    class Ticket {
        //票数 
        private int number = 30;
        //操作方法：卖票 
        public synchronized void sale() {
				//判断：是否有票 
            if(number > 0) {
                System.out.println(Thread.currentThread().getName()+" : 
                        "+(number--)+"
                        "+number); 
            }
        }
    }
```
如果一个代码块被 synchronized 修饰了，当一个线程获取了对应的锁，并执 
行该代码块时，其他线程便只能一直等待，等待获取锁的线程释放锁，而这里 
获取锁的线程释放锁只会有两种情况： 

-  1）获取锁的线程执行完了该代码块，然后线程释放对锁的占有； 
-  2）线程执行发生异常，此时 JVM 会让线程自动释放锁。 

 那么如果这个获取锁的线程由于要等待 IO 或者其他原因（比如调用 sleep 
方法）被阻塞了，但是又没有释放锁，其他线程便只能干巴巴地等待，试想一 
下，这多么影响程序执行效率。 因此就需要有一种机制可以不让等待的线程一直无期限地等待下去（比如只等 待一定的时间或者能够响应中断），通过 Lock 就可以办到。 
## **2.2 什么是 Lock **
Lock 锁实现提供了比使用同步方法和语句可以获得的更广泛的锁操作。它们允 
许更灵活的结构，可能具有非常不同的属性，并且可能支持多个关联的条件对 象。Lock 提供了比 synchronized 更多的功能。
####  Lock 与的 Synchronized 区别

- • Lock 不是 Java 语言内置的，synchronized 是 Java 语言的关键字，因此是内 置特性。Lock 是一个类，通过这个类可以实现同步访问； 
- • Lock 和 synchronized 有一点非常大的不同，采用 synchronized 不需要用户 去手动释放锁，当 synchronized 方法或者 synchronized 代码块执行完之后， 系统会自动让线程释放对锁的占用；而 Lock 则必须要用户去手动释放锁，如 果没有主动释放锁，就有可能导致出现死锁现象。 
### **2.2.1 Lock 接口 **
```
  public interface Lock {
        void lock();
        void lockInterruptibly() throws InterruptedException;
        boolean tryLock();
        boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
        void unlock();
        Condition newCondition();
    }
```
下面来逐个讲述 Lock 接口中每个方法的使用 
### **2.2.2 lock **
lock()方法是平常使用得最多的一个方法，就是用来获取锁。如果锁已被其他 
线程获取，则进行等待。 
采用 Lock，必须主动去释放锁，并且在发生异常时，不会自动释放锁。因此一 
般来说，使用 Lock 必须在 try{}catch{}块中进行，并且将释放锁的操作放在 
finally 块中进行，以保证锁一定被被释放，防止死锁的发生。通常使用 Lock 
来进行同步的话，是以下面这种形式去使用的： 
```
Lock lock = ...; 
lock.lock(); 
try{
//处理任务 
    }catch(Exception ex){
    }finally{lock.unlock(); //释放锁 
    }  
```
### **2.2.3 newCondition **
关键字 synchronized 与 wait()/notify()这两个方法一起使用可以实现等待/通 
知模式， Lock 锁的 newContition()方法返回 Condition 对象，Condition 类 
也可以实现等待/通知模式。 
用 notify()通知时，JVM 会随机唤醒某个等待的线程， 使用 Condition 类可以 
进行选择性通知， Condition 比较常用的两个方法： 
• await()会使当前线程等待,同时会释放锁,当其他线程调用 signal()时,线程会重 
新获得锁并继续执行。 
• signal()用于唤醒一个等待的线程。 
==注意：在调用 Condition 的 await()/signal()方法前，也需要线程持有相关 
的 Lock 锁，调用 await()后线程会释放这个锁，在 singal()调用后会从当前 
Condition 对象的等待队列中，唤醒 一个线程，唤醒的线程尝试获得锁， 一旦 
获得锁成功就继续执行。== 
## **2.3 ReentrantLock **
ReentrantLock，意思是“可重入锁”，关于可重入锁的概念将在后面讲述。 
ReentrantLock 是唯一实现了 Lock 接口的类，并且 ReentrantLock 提供了更 
多的方法。下面通过一些实例看具体看一下如何使用。 
```
public class Test {
    private ArrayList<Integer> arrayList = new ArrayList<Integer>();
    public static void main(String[] args) {
        final Test test = new Test();
        new Thread(){
            public void run() {
                test.insert(Thread.currentThread());
            };}.start();
        new Thread(){
            public void run() {
                test.insert(Thread.currentThread());
            };
        }.start();
    }
    public void insert(Thread thread) {
        Lock lock = new ReentrantLock(); //注意这个地方 
        lock.lock();
        try {
            System.out.println(thread.getName()+"得到了锁");
            for(int i=0;i<5;i++) {
                arrayList.add(i);
            }
        } catch (Exception e) {
// TODO: handle exception 
        }finally {
            System.out.println(thread.getName()+"释放了锁");
            lock.unlock();
        }
    }
} 
```
## **2.4 ReadWriteLock **
ReadWriteLock 也是一个接口，在它里面只定义了两个方法： 
```
public interface ReadWriteLock {
    /**
     * Returns the lock used for reading. 
     *
     * @return the lock used for reading. 
     */ Lock readLock();
    /**
     * Returns the lock used for writing. 
     *
     * @return the lock used for writing. 
     */
    Lock writeLock();
} 
```
一个用来获取读锁，一个用来获取写锁。也就是说将文件的读写操作分开，分 
成 2 个锁来分配给线程，从而使得多个线程可以同时进行读操作。下面的 
**ReentrantReadWriteLock **实现了 ReadWriteLock 接口。 
ReentrantReadWriteLock 里面提供了很多丰富的方法，不过最主要的有两个 
方法：readLock()和 writeLock()用来获取读锁和写锁。 
下面通过几个例子来看一下 ReentrantReadWriteLock 具体用法。 
假如有多个线程要同时进行读操作的话，先看一下 synchronized 达到的效果： 
```
public class Test {
    private ReentrantReadWriteLock rwl = new
            ReentrantReadWriteLock();
    public static void main(String[] args) {
        final Test test = new Test();
        new Thread(){
            public void run() { test.get(Thread.currentThread());
            };
        }.start();
        new Thread(){
            public void run() {
                test.get(Thread.currentThread());
            };
        }.start();
    }
    public synchronized void get(Thread thread) {
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis() - start <= 1) {
            System.out.println(thread.getName()+"正在进行读操作");
        }
        System.out.println(thread.getName()+"读操作完毕");
    }
}
```
而改成用读写锁的话： 
```
public class Test { private ReentrantReadWriteLock rwl = new
        ReentrantReadWriteLock();
    public static void main(String[] args) {
        final Test test = new Test();
        new Thread(){
            public void run() {
                test.get(Thread.currentThread());
            };
        }.start();
        new Thread(){
            public void run() {
                test.get(Thread.currentThread());
            };
        }.start();
    }
    public void get(Thread thread) {
        rwl.readLock().lock();
        try {
            long start = System.currentTimeMillis();
            while(System.currentTimeMillis() - start <= 1) {
                System.out.println(thread.getName()+"正在进行读操作");
            }
            System.out.println(thread.getName()+"读操作完毕");
        } finally {
            rwl.readLock().unlock();
        }
    }
} 
```
说明 thread1 和 thread2 在同时进行读操作。这样就大大提升了读操作的效率。 
==**注意:**== 
• 如果有一个线程已经占用了读锁，则此时其他线程如果要申请写锁，则申请写 
锁的线程会一直等待释放读锁。 
• 如果有一个线程已经占用了写锁，则此时其他线程如果申请写锁或者读锁，则 
申请的线程会一直等待释放写锁。 
## **2.5 小结(重点) **
### Lock 和 synchronized 有以下几点不同： 
1. Lock 是一个接口，而 synchronized 是 Java 中的关键字，synchronized 是内置的语言实现； 
2. synchronized 在发生异常时，会自动释放线程占有的锁，因此不会导致死锁现象发生；而 Lock 在发生异常时，如果没有主动通过 unLock()去释放锁，则很可能造成死锁现象，因此使用 Lock 时需要在 finally 块中释放锁； 
3. Lock 可以让等待锁的线程响应中断，而 synchronized 却不行，使用 synchronized 时，等待的线程会一直等待下去，不能够响应中断；4. 通过 Lock 可以知道有没有成功获取锁，而 synchronized 却无法办到。 
5. Lock 可以提高多个线程进行读操作的效率。在性能上来说，如果竞争资源不激烈，两者的性能是差不多的，而当竞争资源非常激烈时（即有大量线程同时竞争），此时 Lock 的性能要远远优于 synchronized
