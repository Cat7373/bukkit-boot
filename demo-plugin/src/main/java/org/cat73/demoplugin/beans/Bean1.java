package org.cat73.demoplugin.beans;

import org.cat73.bukkitboot.annotation.Bean;
import org.cat73.bukkitboot.annotation.Inject;
import org.cat73.bukkitboot.annotation.PostConstruct;
import org.cat73.bukkitboot.util.Logger;

@Bean
public class Bean1 {
    @Inject
    private Bean2 bean;

    @PostConstruct
    public void init() {
        Logger.debug("Bean1.init(): bean: %s", bean);
    }
}
