package org.schhx.leaf.leaf;

/**
 * @author shanchao
 * @date 2018-05-31
 */
public interface Leaf {

    /**
     * 获取下一个id
     *
     * @param bizTag
     * @return
     */
    long nextId(String bizTag);
}
