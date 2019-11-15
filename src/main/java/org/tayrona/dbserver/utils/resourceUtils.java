package org.tayrona.dbserver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.tayrona.dbserver.Constants;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public abstract class resourceUtils {
    private static final Logger LOG = LoggerFactory.getLogger(resourceUtils.class.getName());

    public static String loadText(final String name) throws IOException {
        try (Reader reader = new InputStreamReader(ResourceUtils.getURL(name).openStream())) {
            return FileCopyUtils.copyToString(reader);
        }
    }
    public static String loadTextResource(final String name) throws IOException {
        return resourceUtils.loadText(Constants.RESOURCE_PREFIX + name);
    }

    public static String loadTextOrResource(final String name) throws IOException {
        try {
            return resourceUtils.loadText(Constants.RESOURCE_PREFIX + name);
        } catch (IOException e) {
            return resourceUtils.loadTextResource(name);
        }
    }

    public static String loadTextSafe(final String name) {
        try {
            return resourceUtils.loadText(name);
        } catch (IOException e) {
            LOG.error("{}: '{}'", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    public static String loadTextResourceSafe(final String name) {
        try {
            return resourceUtils.loadTextResource(name);
        } catch (IOException e) {
            LOG.error("{}: '{}'", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    public static String loadTextOrResourceSafe(final String name) {
        try {
            return resourceUtils.loadTextOrResource(name);
        } catch (IOException e) {
            LOG.error("{}: '{}'", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }
}
