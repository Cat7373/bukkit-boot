package org.cat73.demoplugin.nms;

import org.cat73.bukkitboot.annotation.core.Bean;
import org.cat73.bukkitboot.annotation.core.condition.ConditionalOnNMSVersion;
import org.cat73.bukkitboot.util.reflect.NMS;

@Bean
@ConditionalOnNMSVersion(NMS.v1_13_R1)
public class v13_R1 implements INMSBean {
    @Override
    public String getName() {
        return "v13_R1";
    }
}
