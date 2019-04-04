package org.cat73.demoplugin.beans;

import org.cat73.bukkitboot.annotation.Bean;
import org.cat73.bukkitboot.annotation.Inject;
import org.cat73.bukkitboot.annotation.PostConstruct;
import org.cat73.bukkitboot.util.Logger;

@Bean
public class Bean3 {
    @Inject(name = "bean1")
    private Bean1 bean;

    @PostConstruct
    public void init(Bean2 parameterBean) {
        Logger.debug("Bean3.init(): bean: %s, parameterBean: %s", this.bean.getClass().getSimpleName(), parameterBean.getClass().getSimpleName());
    }
}
