package tools;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class JasyptEncryptor {

    public static void main(String[] args) {
        StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();
        enc.setAlgorithm("PBEWithMD5AndDES");
        enc.setPassword("MASTER_KEY");

        System.out.println(enc.encrypt("sachirockzz_QmeLlx"));

       // sachirockzz_QmeLlx // uwtnNqF2LqBdnbXEBtp1pTt/XPabYeag2TSZxTssQk=
        // dqGwajoFfKuNCvRmW99q // 3a3a9iEirjmy4/+oxu5JojQXeu80YPTRsZkMCRMfI+I=
        // bs://12dea302b5de097443be949803a6e20e58a9e6bc //LeymNuUcCMKZIpuP974l9+GxixvS7E1O034uUm7VTo/SNrehy+a/Bw5jJqxRp+Phd1BtNDxa/dA=
        // onthego.in@brillio.com // kPYn/6RXoaWKD9SW9i4ck+U5hF5Ig6/y/4iBDgJpm48=
        // Justcomeandtest@123456789 // SlkwtdzgUxR4+29EK/xPNNQ53M2CurD4Q7Az0W5Joe1gXeNVwutO4Q==


    }
}
