package org.cat73.demoplugin.beans;

import org.cat73.catbase.annotation.Inject;
import org.cat73.catbase.annotation.PostConstruct;
import org.cat73.catbase.util.Logger;

public class Bean1 {
    @Inject
    private Bean2 bean;

    @PostConstruct
    public void init() {
        Logger.debug("Bean1.init(): bean: %s", bean);
    }
}
