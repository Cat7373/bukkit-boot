package org.cat73.bukkitboot.util.reflect;

import org.cat73.bukkitboot.util.Lang;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.annotation.Nonnull;

/**
 * 包扫描工具类
 */
public final class Scans {
    private Scans() {
        throw new UnsupportedOperationException();
    }

    /**
     * 从一个 Class 对象作为引子，扫描其所在的包内的所有 Class 并返回
     * <p>只支持 jar 包</p>
     *
     * @param clazz 作为引子的 Class 对象
     * @return 扫描到的所有 Class 对象
     * @throws IOException 如果扫描过程中出现了 IO 异常
     */
    @Nonnull
    public static List<Class<?>> scanClass(@Nonnull Class<?> clazz) throws IOException {
        // 获取 Class 所在的 jar 包
        URL url = clazz.getProtectionDomain().getCodeSource().getLocation();

        // 去掉 URL 中的前缀
        String location = url.toString();
        if (location.endsWith(".jar")) {
            return scanClass(location);
        } else if (location.contains("jar!")) {
            if (location.startsWith("jar:file:")) {
                location = location.substring("jar:file:".length());
            }

            return scanClass(location.substring(0, location.lastIndexOf("jar!") + 3));
        } else {
            throw new IllegalStateException(String.format("不支持的包类型: %s", location));
        }
    }

    /**
     * 扫描一个包中所有的 Class
     * @param location jar 包的 URL
     * @return 扫描到的 Class
     * @throws IOException 如果扫描过程中出现了 IO 异常
     */
    @Nonnull
    private static List<Class<?>> scanClass(@Nonnull String location) throws IOException {
        // 获得包的绝对路径
        String absolutePath = getAbsolutePath(location);

        // 获取包里所有的文件名
        List<String> names = new ArrayList<>();
        try (ZipInputStream zis = makeZipInputStream(absolutePath)) {
            ZipEntry ens;
            while (null != (ens = zis.getNextEntry())) {
                if (ens.isDirectory()) {
                    continue;
                }
                names.add(ens.getName());
            }
        }

        // 扫描所有的 Class 的全类名
        List<String> classNames = new ArrayList<>();
        for (String name : names) {
            String ensName = name;
            if (name.contains("/")) {
                name = name.substring(name.lastIndexOf('/') + 1);
            }

            if (!name.toLowerCase().endsWith(".class")) {
                continue;
            }

            String className = ensName;
            if (className.startsWith("/")) {
                className = className.substring(1);
            }
            className = className.replace("/", ".");
            // 去掉 .class(6 个字符)
            className = className.substring(0, className.length() - 6);

            // 添加到结果列表中
            classNames.add(className);
        }

        // 将类名加载为 Class
        List<Class<?>> classList = new ArrayList<>();
        for (String className : classNames) {
            try {
                classList.add(Class.forName(className));
            } catch (Throwable e) {
                // quiet
            }
        }

        // 返回结果
        return classList;
    }

    /**
     * 将一个 zip 文件路径转换为 ZIP 文件输入流
     * @param filePath 文件路径
     * @return ZipInputStream 输入流
     * @throws IOException 如果转换过程中出现了 IO 异常
     */
    @Nonnull
    private static ZipInputStream makeZipInputStream(@Nonnull String filePath) throws IOException {
        ZipInputStream zis;
        try {
            zis = new ZipInputStream(new FileInputStream(filePath));
        } catch (IOException e) {
            zis = new ZipInputStream(new URL(filePath).openStream());
        }
        return zis;
    }

    /**
     * 根据一个 URL 路径获取包的绝对路径
     * @param location URL 路径
     * @return 包的绝对路径
     */
    @Nonnull
    private static String getAbsolutePath(@Nonnull String location) {
        String path = location;
        if (path.startsWith("zip:")) {
            path = path.substring(4);
        }
        if (path.startsWith("file:/")) {
            path = path.substring(6);
            if (!new File(path).exists() && !path.startsWith("/")) {
                path = "/" + path;
            }
        }
        try {
            return new File(path).getAbsoluteFile().getCanonicalPath();
        } catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }
}
