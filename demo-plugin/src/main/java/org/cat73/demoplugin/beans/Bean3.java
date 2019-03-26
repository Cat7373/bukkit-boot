package org.cat73.demoplugin.beans;

import org.cat73.catbase.annotation.Inject;
import org.cat73.catbase.annotation.PostConstruct;
import org.cat73.catbase.util.Logger;

public class Bean3 {
    @Inject
    private Bean1 bean;

    @PostConstruct
    public void init() {
        Logger.debug("Bean3.init(): bean: %s", bean);
    }
}
