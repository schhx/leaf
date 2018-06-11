package org.schhx.leaf.leaf.impl.db;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.schhx.leaf.leaf.Leaf;
import org.schhx.leaf.leaf.impl.db.dao.LeafConfigMapper;
import org.schhx.leaf.leaf.impl.db.module.LeafConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 利用数据库产生趋势递增ID
 *
 * @author shanchao
 * @date 2018-05-31
 */
@Component
@Slf4j
public class DBLeaf implements Leaf {

    @Autowired
    private LeafConfigMapper leafMapper;

    private static Map<String, IdPool> idPoolMap = new HashMap<>();

    @Override
    public long nextId(String bizTag) {
        IdPool idPool = idPoolMap.get(bizTag);
        if (idPool == null) {
            idPool = initIdPool(bizTag);
        }
        return idPool.nextId();
    }

    private synchronized IdPool initIdPool(String bizTag) {
        IdPool idPool = idPoolMap.get(bizTag);
        if (idPool == null) {
            idPool = new IdPool(leafMapper.selectByBizTag(bizTag));
            idPoolMap.put(bizTag, idPool);

        }
        return idPool;
    }

    @Transactional(rollbackFor = Exception.class)
    protected Segment getNextSegment(String bizTag) {
        leafMapper.increase(bizTag);
        LeafConfig leaf = leafMapper.selectByBizTag(bizTag);
        return new Segment(leaf);
    }

    private class IdPool {
        private String bizTag;

        private final float DEFAULT_LOAD_FACTOR = 0.2f;

        private volatile Segment current;

        private volatile Segment next;

        private final int threshold;

        private AtomicInteger count;

        private Lock idPoolLock = new ReentrantLock();

        public long nextId() {
            if (count.incrementAndGet() >= threshold && next == null && idPoolLock.tryLock()) {
                try {
                    next = getNextSegment(bizTag);
                } finally {
                    idPoolLock.unlock();
                }
            }


            long id = current.nextId();
            if(!current.isValid(id)) {
                idPoolLock.lock();
                try{
                    id = current.nextId();
                    if(!current.isValid(id)) {
                        current = next;
                        next = null;
                        count.set(0);
                        id = current.nextId();
                    }
                } finally {
                    idPoolLock.unlock();
                }
            }
            return id;
        }

        public IdPool(LeafConfig leaf) {
            current = getNextSegment(leaf.getBizTag());
            this.bizTag = leaf.getBizTag();
            this.threshold = (int) (leaf.getStep() * DEFAULT_LOAD_FACTOR);
            count = new AtomicInteger(0);
        }
    }


    @Data
    private class Segment {
        private long min;

        private long max;

        private AtomicLong atomicLong;

        public Segment(LeafConfig leaf) {
            this.min = leaf.getMaxId() - leaf.getStep();
            this.max = leaf.getMaxId() - 1;
            atomicLong = new AtomicLong(min);
        }

        public long nextId() {
            return atomicLong.getAndIncrement();
        }

        public boolean isValid(long id) {
            return id <= max;
        }


    }
}
