package com.learnRedis.stringexample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.BitSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/bitmap")
public class BitmapCaseAction {
    //存储的key前缀
    private static final String ONLINE_KEY_PREFIX = "online:";
    //天数
    private static final int DAY_NUM = 30;
    //用户数量
    private static final int PEOPLE_NUM = 10000;

    private RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成模拟数据
     */
    public void createData() {
        //用来保证线程执行完在进行后面的操作
        CountDownLatch countDownLatch = new CountDownLatch(DAY_NUM);

        int poolSize = Runtime.getRuntime().availableProcessors() * 2;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 60, TimeUnit.SECONDS, new ArrayBlockingQueue(DAY_NUM-poolSize));
        //DAY_NUM天
        for (int i = 1; i <= DAY_NUM; i++) {
            int finalI = i;
            executor.execute(() -> {
                //假设有PEOPLE_NUM个用户
                for (int j = 1; j <= PEOPLE_NUM; j++) {
                    redisTemplate.opsForValue().setBit(ONLINE_KEY_PREFIX + finalI, j, Math.random() > 0.1);
                }
                countDownLatch.countDown();
            });
        }

        //等待线程全部执行完成
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据传入的天数统计
     *
     * @param day
     */
    public void calActive(int day) {
        if (day < 0 || day > DAY_NUM){
            throw new IllegalArgumentException("传入的天数不能小于0或者大于30天!");
        }

        long calStart = System.currentTimeMillis();
        BitSet active = new BitSet();
        active.set(0, PEOPLE_NUM);
        for (int i = 1; i <= day; i++) {

//            BitSet bitSet = BitSet.valueOf(redisTemplate.opsForValue().get(ONLINE_KEY_PREFIX + i, 0, -1));
//            BitSet bitSet = BitSet.valueOf(redisTemplate.opsForValue().get((ONLINE_KEY_PREFIX + i)));
//            BitSet bitSet = BitSet.valueOf(jedis.get((ONLINE_KEY_PREFIX + i).getBytes()));
//            active.and(bitSet);
        }
        long calEnd = System.currentTimeMillis();
        System.out.println(day + "天的上线用户" + active.cardinality() + ",花费时长:" + (calEnd - calStart));
    }
}
