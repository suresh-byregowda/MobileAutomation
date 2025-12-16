package utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public final class JasyptUtil {

    private static StandardPBEStringEncryptor encryptor;

    private JasyptUtil() {}

    private static StandardPBEStringEncryptor getEncryptor() {

        if (encryptor == null) {

            String masterPassword =
                    System.getProperty("jasypt.password", System.getenv("JASYPT_PASSWORD"));

            if (masterPassword == null || masterPassword.isBlank()) {
                throw new RuntimeException(
                        "Jasypt password not provided. Set -Djasypt.password or JASYPT_PASSWORD"
                );
            }

            StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();

            // 🔴 MUST MATCH ENCRYPTION EXACTLY
            enc.setAlgorithm("PBEWithMD5AndDES");
            enc.setPassword(masterPassword);

            encryptor = enc;
        }
        return encryptor;
    }

    public static String decryptIfNeeded(String value) {

        if (value == null) return null;

        if (value.startsWith("ENC(") && value.endsWith(")")) {
            String cipherText = value.substring(4, value.length() - 1);
            try {
                return getEncryptor().decrypt(cipherText);
            } catch (Exception e) {
                System.err.println("❌ Jasypt failed to decrypt value: " + value);
                throw e;
            }
        }

        return value;
    }

}
