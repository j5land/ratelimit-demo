package com.wosai.ratelimit;

import com.github.noconnor.junitperf.JUnitPerfRule;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.wosai.ratelimit.bucket.BucketRateLimit;
import com.wosai.ratelimit.fix.FixRateLimit;
import com.wosai.ratelimit.slide.SlideRateLimit;
import com.wosai.ratelimit.token.TokenRateLimit;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Herry Jiang
 * @date 2021/3/8
 */
public class RateLimitTestCase {

    @Rule
    public JUnitPerfRule jUnitPerfRule = new JUnitPerfRule();

    //private RateLimit rateLimit = new FixRateLimit(300);
    //private RateLimit rateLimit = new SlideRateLimit(300, 50);
    //private RateLimit rateLimit = new TokenRateLimit(300);
    private RateLimit rateLimit = new BucketRateLimit(5);

    /**
     * @JUnitPerfTest
     * threads：线程数
     * durationMs：执行时长
     * rampUpPeriodMs: 平稳上升时长
     * warmUpMs: 预热时长
     * maxExecutionsPerSecond: QPS限定
     */
    @Test
    @JUnitPerfTest(threads = 5, durationMs = 11000, warmUpMs = 1000, maxExecutionsPerSecond = 1000)
    public void testFixRateLimit() throws Exception {
        if (rateLimit.acquire()){
            System.out.println("请求成功");
        }else {
            System.out.println("请求失败");
            throw new Exception("请求失败");
        }
    }


    @Test
    @JUnitPerfTest(threads = 5, durationMs = 11000, warmUpMs = 1000, maxExecutionsPerSecond = 1000)
    public void testSlideRateLimit() throws Exception {
        if (rateLimit.acquire()){
            System.out.println("请求成功");
        }else {
            System.out.println("请求失败");
            throw new Exception("请求失败");
        }
    }



    @Test
    @JUnitPerfTest(threads = 5, durationMs = 11000, warmUpMs = 1000, maxExecutionsPerSecond = 1000)
    public void testTokenRateLimit() throws Exception {
        if (rateLimit.acquire()){
            System.out.println("请求成功");
        }else {
            System.out.println("请求失败");
            throw new Exception("请求失败");
        }
    }

    @Test
    @JUnitPerfTest(threads = 5, durationMs = 11000, warmUpMs = 1000, maxExecutionsPerSecond = 500)
    public void testBucketRateLimit() throws Exception {
        if (rateLimit.acquire()){
            System.out.println("请求成功");
        }else {
            System.out.println("请求失败");
            throw new Exception("请求失败");
        }
    }
}
