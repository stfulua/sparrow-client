package xyz.vprolabs.sparrow.security;

public final class StringDecryptor {
    private StringDecryptor() {
    }

    public static String decrypt(byte[] data, int key) {
        byte[] buf = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            buf[i] = (byte)(data[i] ^ key);
        }
        return new String(buf, java.nio.charset.StandardCharsets.UTF_8);
    }
}
