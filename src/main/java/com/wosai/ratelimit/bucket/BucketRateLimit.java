package com.wosai.ratelimit.bucket;

import com.wosai.ratelimit.AbstractRateLimit;
import com.wosai.ratelimit.RateLimit;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Herry Jiang
 * @date 2021/3/10
 */
public class BucketRateLimit extends AbstractRateLimit implements RateLimit {

    private volatile long lasTime = System.currentTimeMillis();

    //多少时间流出1滴水，流出水的时间，时间精确到微秒
    private long intervalMicroseconds;

    private AtomicLong bucketCapacity = new AtomicLong(0);;

    public BucketRateLimit(int maxQps) {
        super(maxQps);
        intervalMicroseconds = 1000 * 1000 / maxQps;
    }

    public boolean acquire() {
        synchronized (mutex()){
            long now = System.currentTimeMillis();
            //漏桶流走的水
            long runOutWater = (now - lasTime) * 1000 / intervalMicroseconds;
            if (runOutWater != 0){
                lasTime = now;
            }
            if (bucketCapacity.addAndGet( - runOutWater ) < 0){
                bucketCapacity.set(0);
            }
            if (bucketCapacity.get() + 1 < maxQps){
                bucketCapacity.incrementAndGet();
                return true;
            }
            return false;
        }
    }
}
