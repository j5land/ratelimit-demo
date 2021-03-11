package com.wosai.ratelimit;

/**
 * @author Herry Jiang
 * @date 2021/3/8
 */
public abstract class AbstractRateLimit implements RateLimit {

    protected int maxQps;

    private volatile Object mutexDoNotUseDirectly;

    protected Object mutex() {
        Object mutex = this.mutexDoNotUseDirectly;
        if(mutex == null) {
            synchronized(this) {
                mutex = this.mutexDoNotUseDirectly;
                if(mutex == null) {
                    this.mutexDoNotUseDirectly = mutex = new Object();
                }
            }
        }
        return mutex;
    }

    public AbstractRateLimit(int maxQps) {
        this.maxQps = maxQps;
    }
}
