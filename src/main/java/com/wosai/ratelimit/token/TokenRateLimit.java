package com.wosai.ratelimit.token;

import com.wosai.ratelimit.AbstractRateLimit;
import com.wosai.ratelimit.RateLimit;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Herry Jiang
 * @date 2021/3/9
 */
public class TokenRateLimit extends AbstractRateLimit implements RateLimit {

    private volatile long lasTime = System.currentTimeMillis();
    //每隔多久放一个token，时间单位为微秒
    private long tokenIntervalMicroseconds;

    public static final long CONSTANT_1000 = 1000;
    //当前QPS
    private AtomicLong tokenBucket = new AtomicLong(0);

    public TokenRateLimit(int maxQps) {
        super(maxQps);
        tokenIntervalMicroseconds = 1000 * 1000 / maxQps;
    }

    public boolean acquire() {
        // 放token
        synchronized (mutex()){
            long now = System.currentTimeMillis();
            long interval = now - lasTime;
            if (interval > CONSTANT_1000){
                lasTime = now;
                interval = 1000;
            }
            long putTokenNum = interval * CONSTANT_1000 / tokenIntervalMicroseconds;
            System.out.println("开始>当前令牌桶token数："+tokenBucket.get()+", putTokenNum="+putTokenNum);
            if (putTokenNum != 0){
                lasTime = now;
                System.out.println("当前令牌桶token数："+tokenBucket.get());
            }
            if (tokenBucket.addAndGet(putTokenNum) > maxQps){
                tokenBucket.set(maxQps);
            }
            System.out.println("结束>当前令牌桶token数："+tokenBucket.get());
            if (tokenBucket.get() <= 0 || tokenBucket.decrementAndGet() < 0){
                return false;
            }
            return true;
        }
    }
}
