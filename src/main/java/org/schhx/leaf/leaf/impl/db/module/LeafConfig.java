package org.schhx.leaf.leaf.impl.db.module;

import lombok.Data;

import java.util.Date;

/**
 * @author shanchao
 * @date 2018-05-30
 */
@Data
public class LeafConfig {
    private String bizTag;

    private Long maxId;

    private Integer step;

    private String desc;

    private Date updateTime;
}