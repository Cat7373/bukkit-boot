package org.cat73.demoplugin.beans;

import org.cat73.catbase.annotation.Bean;
import org.cat73.catbase.annotation.Inject;
import org.cat73.catbase.annotation.PostConstruct;
import org.cat73.catbase.util.Logger;

@Bean
public class Bean2 {
    @Inject
    private Bean3 bean;

    @PostConstruct
    public void init() {
        Logger.debug("Bean2.init(): bean: %s", bean);
    }
}
