package com.github.lfyuomr.gylo.bostongene.task3;

import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.Assert.assertEquals;

public class APIKeyUtilTest {
    private final Path tmpKeyFile = new File(System.getProperty("user.home"), UUID.randomUUID().toString()).toPath();
    private String tmpEnvironmentVariable;

    @Before
    public void setUp() throws Exception {
        Files.copy(APIKeyUtil.FILE_LOCATION.toPath(), tmpKeyFile, REPLACE_EXISTING, NOFOLLOW_LINKS);
        setInFile(null);
        tmpEnvironmentVariable = System.getenv().get(APIKeyUtil.ENVIRONMENT_VARIABLE_NAME);
        setInEnvironment(null);
    }

    @After
    public void tearDown() throws Exception {
        Files.copy(tmpKeyFile, APIKeyUtil.FILE_LOCATION.toPath(), REPLACE_EXISTING, NOFOLLOW_LINKS);
        Files.delete(tmpKeyFile);
        setInEnvironment(tmpEnvironmentVariable);
    }

    @Test
    public void findInFile() throws Exception {
        val key = UUID.randomUUID().toString();
        setInFile(key);
        setInEnvironment(null);

        assertEquals(key, APIKeyUtil.findKey());
    }

    @Test
    public void findInEnvironment() throws Exception {
        val key = UUID.randomUUID().toString();
        setInEnvironment(key);
        setInFile(null);

        assertEquals(key, APIKeyUtil.findKey());
    }

    @Test
    public void nullIfThereIsNoKey() throws Exception {
        setInFile(null);
        setInEnvironment(null);

        assertEquals(null, APIKeyUtil.findKey());
    }

    @Test
    public void preferEnvironmentToFile() throws Exception {
        val fileKey = UUID.randomUUID().toString();
        setInFile(fileKey);

        val envKey = UUID.randomUUID().toString();
        setInEnvironment(envKey);

        assertEquals(envKey, APIKeyUtil.findKey());
    }

    private void setInEnvironment(@Nullable String key) throws Exception {
        Class<?> clazz = Class.forName("java.util.Collections$UnmodifiableMap");
        Field field = clazz.getDeclaredField("m");
        field.setAccessible(true);
        Map<String, String> map = (Map<String, String>) field.get(System.getenv());
        if (key == null) {
            map.remove(APIKeyUtil.ENVIRONMENT_VARIABLE_NAME);
        } else {
            map.put(APIKeyUtil.ENVIRONMENT_VARIABLE_NAME, key);
        }
    }

    private void setInFile(@Nullable String key) throws Exception {
        if (key == null) {
            APIKeyUtil.FILE_LOCATION.delete();
        } else {
            try (val raf = new RandomAccessFile(APIKeyUtil.FILE_LOCATION, "rw")) {
                raf.setLength(0);
                raf.writeBytes(key);
            }
        }
    }
}
