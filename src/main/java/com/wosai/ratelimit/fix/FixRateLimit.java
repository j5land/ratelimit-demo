package com.wosai.ratelimit.fix;

import com.wosai.ratelimit.AbstractRateLimit;
import com.wosai.ratelimit.RateLimit;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * https://segmentfault.com/a/1190000023552181
 * @author Herry Jiang
 * @date 2021/3/8
 */
public class FixRateLimit extends AbstractRateLimit implements RateLimit {

    private long lasTime = System.currentTimeMillis();
    // 当前QPS
    private AtomicInteger curQPS = new AtomicInteger(0);

    public FixRateLimit(int maxQps) {
        super(maxQps);
    }

    public boolean acquire() {
        synchronized (mutex()){
            //获取当前时间
            long now = System.currentTimeMillis();
            if (now - lasTime > 1000){
                lasTime = now;
                curQPS.set(0);
            }
            if (curQPS.addAndGet(1) > maxQps){
                return false;
            }
            return true;
        }
    }
}
