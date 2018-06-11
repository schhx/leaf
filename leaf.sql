CREATE TABLE `leaf_config` (
  `biz_tag` varchar(128) NOT NULL DEFAULT '' COMMENT '业务标志',
  `max_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '最大id',
  `step` int(11) NOT NULL COMMENT '步长',
  `desc` varchar(256) DEFAULT NULL COMMENT '说明',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`biz_tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;