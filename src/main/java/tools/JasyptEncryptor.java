package tools;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class JasyptEncryptor {

    public static void main(String[] args) {
        StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();
        enc.setAlgorithm("PBEWithMD5AndDES");
        enc.setPassword("MASTER_KEY");

        System.out.println(enc.encrypt("bs://4a9fd49cebb2149eeb1c5c662c5a9402566e14f9"));

        // U9VYZWspVTsnZGoWjzZa
        // bs://4a9fd49cebb2149eeb1c5c662c5a9402566e14f9

    }
}
