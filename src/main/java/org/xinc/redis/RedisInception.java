package org.xinc.redis;


import org.xinc.function.Inception;
import org.xinc.function.InceptionException;

public class RedisInception  implements Inception {
    @Override
    public void checkRule(Object source) throws InceptionException {
        throw new InceptionException("redis 规则检查异常 key分区不存在");
    }
}
