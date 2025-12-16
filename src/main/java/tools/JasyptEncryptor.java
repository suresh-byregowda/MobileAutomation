package tools;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class JasyptEncryptor {

    public static void main(String[] args) {
        StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();
        enc.setAlgorithm("PBEWithMD5AndDES");
        enc.setPassword("MASTER_KEY");

        System.out.println(enc.encrypt("AndamanNicobar@456"));

    }
}
