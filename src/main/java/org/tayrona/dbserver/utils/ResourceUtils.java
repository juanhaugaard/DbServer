package org.tayrona.dbserver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.tayrona.dbserver.Constants;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public interface ResourceUtils {
    Logger LOG = LoggerFactory.getLogger(ResourceUtils.class.getName());

    static String loadText(final String name) throws IOException {
        try (Reader reader = new InputStreamReader(org.springframework.util.ResourceUtils.getURL(name).openStream())) {
            return FileCopyUtils.copyToString(reader);
        }
    }
    static String loadTextResource(final String name) throws IOException {
        return loadText(Constants.RESOURCE_PREFIX + name);
    }

    static String loadTextOrResource(final String name) throws IOException {
        try {
            return loadText(Constants.RESOURCE_PREFIX + name);
        } catch (IOException e) {
            return loadTextResource(name);
        }
    }

    static String loadTextSafe(final String name) {
        try {
            return loadText(name);
        } catch (IOException e) {
            LOG.error("{}: '{}'", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    static String loadTextResourceSafe(final String name) {
        try {
            return loadTextResource(name);
        } catch (IOException e) {
            LOG.error("{}: '{}'", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    static String loadTextOrResourceSafe(final String name) {
        try {
            return loadTextOrResource(name);
        } catch (IOException e) {
            LOG.error("{}: '{}'", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }
}
