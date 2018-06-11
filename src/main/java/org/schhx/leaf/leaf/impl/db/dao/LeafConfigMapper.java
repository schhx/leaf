package org.schhx.leaf.leaf.impl.db.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.schhx.leaf.leaf.impl.db.module.LeafConfig;

/**
 * @author shanchao
 * @date 2018-05-30
 */
@Mapper
public interface LeafConfigMapper {

    @Select("select * from leaf_config where biz_tag = #{bizTag}")
    LeafConfig selectByBizTag(String bizTag);

    @Update("update leaf_config set max_id = max_id + step where biz_tag = #{bizTag}")
    int increase(String bizTag);

}