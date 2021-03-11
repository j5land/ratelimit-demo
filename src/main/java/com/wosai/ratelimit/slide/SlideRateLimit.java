package com.wosai.ratelimit.slide;

import com.wosai.ratelimit.AbstractRateLimit;
import com.wosai.ratelimit.RateLimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Herry Jiang
 * @date 2021/3/8
 */
public class SlideRateLimit extends AbstractRateLimit implements RateLimit {
    //默认窗口个数：100个
    private int windowNum = 100;
    //默认窗口大小：10ms
    private int windowSize = 10;
    //计算每分片个窗口最大QPS
    private int windowMaxQPS;
    //记录整个大窗口开始时间戳
    private long windowBeginTime = System.currentTimeMillis();
    //当前QPS
    private AtomicInteger curQPS = new AtomicInteger(0);
    //记录每个小窗口对应QPS情况
    private Map<Long, AtomicInteger> windowQpsMap = new ConcurrentHashMap<Long, AtomicInteger>();
    public SlideRateLimit(int maxQps) {
        super(maxQps);
        this.windowMaxQPS = maxQps / windowNum;
    }
    public SlideRateLimit(int maxQps, int windowSize) {
        super(maxQps);
        if (windowSize <= 0){
            throw new RuntimeException("窗口大小不能小于0");
        }
        if (windowSize > 1000){
            throw new RuntimeException("窗口大小不能大于1000");
        }
        this.windowSize = windowSize;
        this.windowNum = 1000 / windowSize;
        this.windowMaxQPS = maxQps / this.windowNum;
    }

    public boolean acquire() {
        // 统计的请求数小于阈值就记录这个请求时间，并允许通过，反之拒绝
        synchronized (mutex()){
            // 获取当前时间
            long now = System.currentTimeMillis();
            // 每次统计请求时间 至 往前1秒这个时间窗口内请求数，且1秒前数据不保存
            long interval = now - windowBeginTime;
            if (interval > 1000){
                windowQpsMap.clear();
                windowBeginTime = now;
                curQPS.set(0);
                interval = 0;
            }
            if (windowMaxQPS != 0){
                // 计算当前小窗口位置
                Long windowKey = interval / windowSize + 1;
                if (windowQpsMap.containsKey(windowKey)){
                    System.out.println("窗口位:"+windowKey+", QPS:"+windowQpsMap.get(windowKey));
                    if (windowQpsMap.get(windowKey).addAndGet(1) > windowMaxQPS){
                        return false;
                    }
                }else {
                    System.out.println("窗口位:"+windowKey+", QPS:"+windowQpsMap.get(windowKey));
                    windowQpsMap.put(windowKey, new AtomicInteger(1));
                }
            }
            if (curQPS.addAndGet(1) > maxQps){
                return false;
            }
            return true;
        }
    }
}
