import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;



public class GenerateKeys 
{

    private KeyPairGenerator keyGen;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private static final String ALGORITHM = "RSA";

    
    /**
     * contructor to set the algorithm and key length 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public void generateKeys() throws NoSuchAlgorithmException, NoSuchProviderException 
    {
            this.keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            this.keyGen.initialize(1024,new SecureRandom());
            this.pair = this.keyGen.generateKeyPair();
            this.privateKey = pair.getPrivate();
            this.publicKey = pair.getPublic();
    }


    /**
     * create keys
     */
    public void createKeys() {
        this.pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }


    /**
     * get the public key
     * @param id
     * @return
     * @throws Exception
     */
    private static PublicKey getPublicKey(String id) throws Exception 
    {
        FileInputStream fi = new FileInputStream(new File(id+"-publicKey.txt"));
		ObjectInputStream oi = new ObjectInputStream(fi);
        PublicKey key = (PublicKey) oi.readObject();
        fi.close();
        oi.close();
        return key; 
    } 

    /**
     * get the private key
     * @param id
     * @return
     * @throws Exception
     */
    private static PrivateKey getPrivateKey(String id) throws Exception 
    {
        FileInputStream fi = new FileInputStream(new File(id+"/"+id+"-privateKey.txt"));
		ObjectInputStream oi = new ObjectInputStream(fi);
        PrivateKey key = (PrivateKey) oi.readObject();
        fi.close();
        oi.close();
        return key; 
    } 


    /**
     * get the cipher for encryption
     * @param id
     * @return
     * @throws Exception
     */
    public static Cipher getENCipher(String id) throws Exception
    {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey(id));
        return cipher;
    }

    
    /**
     * get the cipher for decrpytion
     * @param id
     * @return
     * @throws Exception
     */
    public static Cipher getDECipher(String id) throws Exception
    {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getPublicKey(id));
        return cipher;
    }


    /**
     * generate key for user and store it as file
     * @param id
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */

    public static void generate(String id) throws IOException, NoSuchAlgorithmException, NoSuchProviderException 
    {
        GenerateKeys gk = new GenerateKeys();
        gk.generateKeys();
        File temp = new File(id);
        temp.mkdirs();
        FileOutputStream f = new FileOutputStream(new File(id+"-publicKey.txt"));
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(gk.publicKey);
        f = new FileOutputStream(new File(id+"/"+id+"-privateKey.txt"));
        o = new ObjectOutputStream(f);
        o.writeObject(gk.privateKey);
        o.close();
        f.close();
    }

}