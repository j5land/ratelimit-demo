package com.wosai.ratelimit;

/**
 * @author Herry Jiang
 * @date 2021/3/8
 */
public interface RateLimit {

    boolean acquire();

}
