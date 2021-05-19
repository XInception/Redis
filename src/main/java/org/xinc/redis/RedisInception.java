package org.xinc.redis;


import lombok.extern.slf4j.Slf4j;
import org.xinc.function.Inception;
import org.xinc.function.InceptionException;

@Slf4j
public class RedisInception implements Inception {
    @Override
    public void checkRule(Object source) throws InceptionException {
        log.info("审核xxxx");
//        throw new InceptionException("redis 规则检查异常 key分区不存在");
    }
}
