package com.lc.project.utils;

import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisUtils {

    @Resource
    private RedisTemplate redisTemplate;

    public  Boolean isCurrentChat(String sendId,String acceptId){
        HashMap userCurrent = (HashMap) redisTemplate.opsForHash().entries("userCurrent");
        System.out.println(userCurrent);
        String o = (String)userCurrent.get(sendId);
        Object o1 = userCurrent.get(acceptId);
        if(o1 == null){
            return Boolean.FALSE;
        }
        log.info("当前用户聊天对象",o);
        String[] split = o.split(":");
        String[] split2 = o1.toString().split(":");
        log.info("当前对方聊天对象",o1);
        if(split[0].equals(split2[1]) && split2[0].equals(split[1])){
           return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public void setCurrent(String sendId,String chat){
        redisTemplate.opsForHash().put("userCurrent",sendId,chat);
    }

    public void removeCurrent(String currentId) {
        if(hasCurrent(currentId)){
            redisTemplate.opsForHash().delete("userCurrent",currentId);
        }
    }

    public boolean hasCurrent(String currentId){
      return  redisTemplate.opsForHash().hasKey("userCurrent",currentId);
    }

    public Set<String> keys(String keys) {
        try {
            return redisTemplate.keys(keys);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (io.lettuce.core.RedisCommandTimeoutException e) {
            e.printStackTrace();
            throw new RedisException("Command timed out after 10 second(s)");
        } catch (io.lettuce.core.RedisConnectionException e) {
            e.printStackTrace();
            throw new RedisException("Unable to connect to Redis");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RedisException("Cache Error");
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     * @return
     */
    @SuppressWarnings("unchecked")
    public Long del(String... key) {
        Long result = 0L;
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                if (redisTemplate.delete(key[0])) {
                    result = 1L;
                }
            } else {
                result = redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
        return result;
    }

    /**
     * 删除缓存
     *
     * @param keys String集合
     */
    public Long del(Collection<String> keys) {
        Long result = 0L;
        if (keys != null && keys.size() > 0) {
            result = redisTemplate.delete(keys);
        }
        return result;
    }

    // =================================String================================

    /**
     * 普通缓存获取，当redis没有值时，返回null，不会报错
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    // =================================hash================================

    /**
     * 获取所有哈希表中的字段
     *
     * @param key 键 不能为null
     * @return 所有哈希表中的字段
     */
    public Set<Object> hkeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取哈希表中字段的数量
     *
     * @param key 键 不能为null
     * @return 所有哈希表中的字段
     */
    public Long hlen(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     * @return 删除的记录数
     */
    public Long hdel(String key, Object... item) {
        return redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    // =================================set================================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<String> sGetString(String key) {
        try {
            Set<String> strings = new HashSet<String>();
            Set<Object> objects = redisTemplate.opsForSet().members(key);
            Iterator<Object> iterable = objects.iterator();
            while (iterable.hasNext()) {
                strings.add(iterable.next().toString());
            }
            return strings;
        } catch (io.lettuce.core.RedisCommandTimeoutException e) {
            e.printStackTrace();
            throw new RedisException("Command timed out after 10 second(s)");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RedisException("Cache Error");
        }

    }

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将Set类型数据放入set缓存
     *
     * @param key 键
     * @param set 值 Set类型
     * @return 成功个数
     */
    public long sSetBySet(String key, Set<?> set) {
        try {
            Object[] objects = set.toArray(new Object[set.size()]);
            return redisTemplate.opsForSet().add(key, objects);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将Set类型数据放入set缓存
     *
     * @param key  键
     * @param set  值 Set类型
     * @param time 时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return 成功个数
     */
    public long sSetBySet(String key, Set<?> set, long time) {
        try {
            Object[] objects = set.toArray(new Object[set.size()]);
            long result = redisTemplate.opsForSet().add(key, objects);
            if (time > 0) {
                expire(key, time);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0)
                expire(key, time);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取2个变量的合集,并将合集转为String类型
     *
     * @param key
     * @param otherKey
     * @return
     */
    public Set<String> sUnionString(String key, String otherKey) {
        try {
            Set<String> strings = new HashSet<String>();
            Set<Object> objects = redisTemplate.opsForSet().union(key, otherKey);
            // 遍历集合，将Object转为String
            Iterator<Object> iterable = objects.iterator();
            while (iterable.hasNext()) {
                strings.add(iterable.next().toString());
            }
            return strings;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取多个变量的合集,并将合集转为String类型
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<String> sUnionString(String key, Collection<String> otherKeys) {
        try {
            Set<String> strings = new HashSet<String>();
            Set<Object> objects = redisTemplate.opsForSet().union(key, otherKeys);
            // 遍历集合，将Object转为String
            Iterator<Object> iterable = objects.iterator();
            while (iterable.hasNext()) {
                strings.add(iterable.next().toString());
            }
            return strings;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取2个变量的合集
     *
     * @param key
     * @param otherKey
     * @return
     */
    public Set<Object> sUnion(String key, String otherKey) {
        try {
            return redisTemplate.opsForSet().union(key, otherKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取多个变量的合集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<Object> sUnion(String key, Collection<String> otherKeys) {
        try {
            return redisTemplate.opsForSet().union(key, otherKeys);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取2个变量合集后保存到最后一个参数上
     *
     * @param key
     * @param otherKey
     * @return
     */
    public Set<Object> sUnionAndStore(String key, String otherKey, String destKey) {
        try {
            redisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
            return redisTemplate.opsForSet().members(destKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取多个变量的合集并保存到最后一个参数上
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<Object> sUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
        try {
            redisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
            return redisTemplate.opsForSet().members(destKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean sIsMember(String key, Object object) {
        try {
            Boolean isMember = redisTemplate.opsForSet().isMember(key, object);
            return isMember;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0)
                expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0)
                expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
