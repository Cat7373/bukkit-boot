package org.cat73.demoplugin.nms;

import org.cat73.bukkitboot.annotation.core.Bean;
import org.cat73.bukkitboot.annotation.core.Inject;
import org.cat73.bukkitboot.annotation.core.PostConstruct;
import org.cat73.bukkitboot.util.Logger;

@Bean
public class NMSDemoBean {
    @Inject
    private INMSBean bean;

    @PostConstruct
    public void init() {
        Logger.debug("NMSDemoBean.init(): bean: %s, name: %s", this.bean.getClass().getSimpleName(), this.bean.getName());
    }
}
