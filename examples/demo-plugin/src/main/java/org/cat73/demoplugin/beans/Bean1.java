package org.cat73.demoplugin.beans;

import org.cat73.bukkitboot.annotation.core.Bean;
import org.cat73.bukkitboot.annotation.core.Inject;
import org.cat73.bukkitboot.annotation.core.PostConstruct;
import org.cat73.bukkitboot.annotation.core.PreDestroy;
import org.cat73.bukkitboot.util.Logger;

@Bean
public class Bean1 {
    @Inject(name = "bean2")
    private Object bean;

    @PostConstruct
    public void init(Bean3 parameterBean) {
        Logger.debug("Bean1.init(): bean: %s, parameterBean: %s", this.bean.getClass().getSimpleName(), parameterBean.getClass().getSimpleName());
    }

    @PreDestroy
    public void preDestroy(Bean3 parameterBean) {
        Logger.debug("Bean1.preDestroy(): bean: %s, parameterBean: %s", this.bean.getClass().getSimpleName(), parameterBean.getClass().getSimpleName());
    }
}
