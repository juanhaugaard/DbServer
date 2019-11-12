package org.tayrona.dbserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public abstract class Utils {
    private static final Logger LOG = LoggerFactory.getLogger(Utils.class.getName());

    private static final String RESOURCE_PREFIX = "classpath:";

    public static String loadText(final String name) throws IOException {
        try (Reader reader = new InputStreamReader(ResourceUtils.getURL(name).openStream())) {
            return FileCopyUtils.copyToString(reader);
        }
    }
    public static String loadTextResource(final String name) throws IOException {
        return Utils.loadText(RESOURCE_PREFIX + name);
    }

    public static String loadTextOrResource(final String name) throws IOException {
        try {
            return Utils.loadText(RESOURCE_PREFIX + name);
        } catch (IOException e) {
            return Utils.loadTextResource(name);
        }
    }

    public static String loadTextSafe(final String name) {
        try {
            return Utils.loadText(name);
        } catch (IOException e) {
            LOG.error("{}: '{}'", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    public static String loadTextResourceSafe(final String name) {
        try {
            return Utils.loadTextResource(name);
        } catch (IOException e) {
            LOG.error("{}: '{}'", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    public static String loadTextOrResourceSafe(final String name) {
        try {
            return Utils.loadTextOrResource(name);
        } catch (IOException e) {
            LOG.error("{}: '{}'", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }
}
