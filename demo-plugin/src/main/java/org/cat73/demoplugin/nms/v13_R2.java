package org.cat73.demoplugin.nms;

import org.cat73.bukkitboot.annotation.Bean;
import org.cat73.bukkitboot.annotation.NMSVersion;
import org.cat73.bukkitboot.util.reflect.NMS;

@Bean
@NMSVersion(NMS.v1_13_R2)
@NMSVersion(NMS.v1_12_R1)
public class v13_R2 implements INMSBean {
    @Override
    public String getName() {
        return "v13_R2";
    }
}
