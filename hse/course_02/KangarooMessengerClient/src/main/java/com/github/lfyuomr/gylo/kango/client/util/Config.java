package com.github.lfyuomr.gylo.kango.client.util;

import com.github.lfyuomr.gylo.kango.client.KangoApp;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Config {
    private final static Preferences preferences = Preferences.userNodeForPackage(KangoApp.class);

    public static void
    clearPreferences(long account_id) throws BackingStoreException {
        Preferences prefs = getAccountNode(account_id);
        System.out.println(prefs.keys().length);
        for (String key : prefs.keys()) {
            prefs.remove(key);
        }
    }

    private static Preferences
    getAccountNode(long account_id) {
        return Config.preferences.node(String.valueOf(account_id));
    }

    public static File
    getHistoryFile(long user_id) throws IOException {
        return new File(getHistoryPath().toString() + "/" + user_id + ".conf.xml");
    }

    private static Path
    getHistoryPath() throws IOException {
        String path = preferences.get(Keys.HISTORY_DIRECTORY.getKey(), null);
        path = (path != null) ? path : System.getProperty("user.home") + "/.KangarooMessenger";

        System.out.println(path);

        Path result = Paths.get(path);
        if (!Files.exists(result))
            Files.createDirectory(result);

        preferences.put(Keys.HISTORY_DIRECTORY.getKey(), path);

        return result;
    }

    public static BigInteger
    getConversationCryptoKey(long account_id, long conversation_id) {
        final Preferences preferences = getAccountNode(account_id);
        final byte[] result = preferences.getByteArray(String.valueOf(account_id), null);
        if (result == null)
            throw new IllegalArgumentException("Nonexistent conversation id: " + conversation_id);

        return new BigInteger(result);
    }

    public static Dictionary<String, BigInteger>
    getAllConversationsCryptoKeys(long account_id) {
        final Preferences preferences = getAccountNode(account_id);
        String[] conversations;
        try {
            conversations = getAllConversationIds(account_id);
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }

        Dictionary<String, BigInteger> result = new Hashtable<>(conversations.length);

        for (String conv : conversations) {
            byte[] key = preferences.getByteArray(conv, null);
            if (key == null)
                throw new RuntimeException(new BackingStoreException("Error when reading from preferences"));

            result.put(conv, new BigInteger(key));
        }


        return result;
    }

    private static String[]
    getAllConversationIds(long account_id) throws BackingStoreException {
        final Preferences preferences = getAccountNode(account_id);
        return preferences.keys();
    }

    public static void
    saveCryptoKeys(long account_id, Dictionary<String, BigInteger> cryptoKeys) {
        final Preferences preferences = getAccountNode(account_id);
        final Enumeration<String> ids = cryptoKeys.keys();
        while (ids.hasMoreElements()) {
            final String conv = ids.nextElement();
            preferences.putByteArray(conv, cryptoKeys.get(conv).toByteArray());
        }
    }

    private enum Keys {
        HISTORY_DIRECTORY("history directory");

        private final String key;

        Keys(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}
