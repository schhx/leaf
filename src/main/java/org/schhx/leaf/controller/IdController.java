package org.schhx.leaf.controller;

import lombok.extern.slf4j.Slf4j;
import org.schhx.leaf.leaf.impl.db.DBLeaf;
import org.schhx.leaf.leaf.impl.sf.SnowFlakeLeaf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shanchao
 * @date 2018-05-31
 */
@RestController
@Slf4j
public class IdController {

    @Autowired
    private DBLeaf dbLeaf;

    @Autowired
    private SnowFlakeLeaf snowFlakeLeaf;

    @GetMapping("/db")
    public long db() {
        long id =  dbLeaf.nextId("order");
        log.info("bd {}", id);
        return id;
    }

    @GetMapping("/sf")
    public long sf() {
        long id =  snowFlakeLeaf.nextId();
        log.info("sf {}", id);
        return id;
    }
}
