package org.cat73.demoplugin.beans;

import org.cat73.bukkitboot.annotation.Bean;
import org.cat73.bukkitboot.annotation.Inject;
import org.cat73.bukkitboot.annotation.PostConstruct;
import org.cat73.bukkitboot.util.Logger;

@Bean
public class Bean3 {
    @Inject
    private Bean1 bean;

    @PostConstruct
    public void init() {
        Logger.debug("Bean3.init(): bean: %s", bean);
    }
}
