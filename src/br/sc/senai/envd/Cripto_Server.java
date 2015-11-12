package br.sc.senai.envd;

/**
 * Classe de trabalho que implementa os métodos criptográficos necessários para
 * o funcionamento do processo de criptografia entre um Cliente e o Servidor.
 * Esta classe atende somente ao servidor de comunicação. * 
 * 
 */
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

    /**
     KeyStore de criptografia gerado com prazo de vida de 10 anos.
     Utilizado apenas para armazenar as chaves Pública e Privada,
     necessárias ao processo de criptografia. Utiliza algorítmo RSA.
     Comando para geração do certificado:
     keytool -genkey -alias autentica -keyalg RSA -keypass seg2012 -storepass seg2012 -keystore env_digital.jks -validity 3650
    */

public class Cripto_Server {
    
       private final InputStream keystore = getClass().getResourceAsStream("/conf/envdigital.jks");
       private static final String alias = "envdigital"; 
       private static final String pwd = "123456";

       public Cripto_Server(){
     }
    /**
     Método que decripta dados, encriptados pela chave Pública
     contida no Keystore.
     Após decriptado, retorna um array de bytes dos dados.
    * @param dados, chavePriv
    * @return 
    */

    /**
     * Método que decripta dados, encriptados pela chave Pública
     contida no Keystore.Após decriptado, retorna um array de bytes dos dados.
     * @param dados , chavePriv
     * @param chavePriv
     * @return
     */
    public static byte[] decripta (byte[] dados, PrivateKey chavePriv){
        try {
        Cipher cifra = Cipher.getInstance("RSA");
        cifra.init(Cipher.DECRYPT_MODE, chavePriv);
        System.out.println("Crypto_Server: Tam bytes decriptados: " + dados.length);
        return cifra.doFinal(dados);
        }
        catch ( NoSuchAlgorithmException e ) {
		System.out.println("Classe Crypto_Server - Erro no Algoritmo RSA na decriptação..." + e.getMessage());
                return null; //"Erro decriptação!".getBytes();
        }  catch (NoSuchPaddingException e) {
            System.out.println("Classe Crypto_Server - Erro no Padding do RSA na decriptação..." + e.getMessage());
            return null; //"Erro decriptação!".getBytes();
           } catch (InvalidKeyException e) {
               System.out.println("Classe Crypto_Server - Erro na Chave RSA na  decriptação..." + e.getMessage());
               return null; //"Erro decriptação!".getBytes();
           } catch (IllegalBlockSizeException e) {
               System.out.println("Classe Crypto_Server - Erro no tamanho de Bloco RSA na decriptação..." + e.getMessage());
               return null; //"Erro decriptação!".getBytes();
           } catch (BadPaddingException e) {
               System.out.println("Classe Crypto_Server - Erro Padding inválido no RSA para decriptação..." + e.getMessage());
               return null; //"Erro decriptação!".getBytes();
           }
       }
     
    
        /**
         Método para ler a chave Privada do Keystore
         Retorna um objeto chave Privada PrivateKey
        * @return
        * @throws Exception 
        */
        public PrivateKey getPrivateKeyFromFile() throws Exception {
        KeyStore ks = KeyStore.getInstance ( "JKS" );
        ks.load( keystore, pwd.toCharArray());
        keystore.close();
        Key key = ks.getKey( alias, pwd.toCharArray() );
        if( key instanceof PrivateKey ) {
            return (PrivateKey) key;
        }
        return null;
    }
        /**
         Método para ler a chave Pública do Keystore
         Retorna um objeto chave Pública PublicKey
        * @return
        * @throws Exception 
        */
    
    public PublicKey getPublicKeyFromFile() throws Exception {
        KeyStore ks = KeyStore.getInstance ( "JKS" );
        ks.load( keystore, pwd.toCharArray() );
        Certificate c = ks.getCertificate( alias );
        PublicKey p = c.getPublicKey();
        System.out.println("Chave Pública: " + p.toString());
        return p;
	}
    
    /**
     * Método para encriptar dados com a chave Simétrica.
     * @param textoP
     * @param chaveS
     * @return bytes encriptados
     */
    public static byte[] encriptaSim(byte[] textoP, byte[] chaveS){
         try {
             Cipher cifra = Cipher.getInstance("AES/CBC/PKCS5Padding");
             IvParameterSpec ivspec = new IvParameterSpec (new byte[16]);
             cifra.init(Cipher.ENCRYPT_MODE, new SecretKeySpec (chaveS,"AES"),ivspec);
             return cifra.doFinal(textoP);
         } catch (NoSuchAlgorithmException ex){
           System.out.println("Erro no Algoritmo de  decriptação simétrica, Verifique! " + ex.getMessage());
              return null;
         } catch (NoSuchPaddingException ex) {
             System.out.println("Erro no Padding da decriptação simétrica, Verifique! " + ex.getMessage());
             return null;
           } catch (InvalidKeyException ex) {
               System.out.println("Erro na Chave simétrica, Verifique! " + ex.getMessage());
               return null;
           } catch (InvalidAlgorithmParameterException ex) {
               System.out.println("Erro nos parâmtros para decriptação simétrica, Verifique! " + ex.getMessage());
               return null;
           } catch (IllegalBlockSizeException ex) {
               System.out.println("Erro no tamanho de blocos para decriptação simétrica, Verifique! " + ex.getMessage());
               return null;
           } catch (BadPaddingException ex) {
               System.out.println("Erro no Padding ou Inválido para decriptação simétrica, Verifique! " + ex.getMessage());
               return null;
           }
     }
    /**
     * Método para decriptar dados com uma chave Simétrica.
     * @param textoC
     * @param chaveSeg
     * @return bytes decriptados.
     */
    public static byte[] decriptaSim(byte[] textoC, byte[] chaveSeg){
         try {
             Cipher cifra = Cipher.getInstance("AES/CBC/PKCS5Padding");
             IvParameterSpec ivspec = new IvParameterSpec (new byte[16]);             
             cifra.init(Cipher.DECRYPT_MODE, new SecretKeySpec (chaveSeg,"AES"),ivspec);
             return cifra.doFinal(textoC);             
         } 
         catch (NoSuchAlgorithmException ex){
           System.out.println("Erro no Algoritmo de  decriptação simétrica, Verifique! " + ex.getMessage());
              return null;
         } catch (NoSuchPaddingException ex) {
             System.out.println("Erro no Padding da decriptação simétrica, Verifique! " + ex.getMessage());
             return null;
           } catch (InvalidKeyException ex) {
               System.out.println("Erro na Chave simétrica, Verifique! " + ex.getMessage());
               return null;
           } catch (InvalidAlgorithmParameterException ex) {
               System.out.println("Erro nos parâmtros para decriptação simétrica, Verifique! " + ex.getMessage());
               return null;
           } catch (IllegalBlockSizeException ex) {
               System.out.println("Erro no tamanho de blocos para decriptação simétrica, Verifique! " + ex.getMessage());
               return null;
           } catch (BadPaddingException ex) {
               System.out.println("Erro no Padding ou Inválido para decriptação simétrica, Verifique! " + ex.getMessage());
               return null;
           }
     }
    
}