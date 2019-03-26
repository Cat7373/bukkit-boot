package org.cat73.demoplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.cat73.catbase.annotation.CatPlugin;
import org.cat73.demoplugin.beans.Bean1;
import org.cat73.demoplugin.beans.Bean2;
import org.cat73.demoplugin.beans.Bean3;

/**
 * 插件主类
 */
@CatPlugin(classes = {
        Bean1.class,
        Bean2.class,
        Bean3.class
})
public final class CatBaseDemo extends JavaPlugin {}
