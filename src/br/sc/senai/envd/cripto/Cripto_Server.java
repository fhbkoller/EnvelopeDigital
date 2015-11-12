package br.sc.senai.envd.cripto;

/**
 * Classe de trabalho que implementa os métodos criptográficos necessários para
 * o funcionamento do processo de criptografia entre um Cliente e o Servidor.
 * Esta classe atende somente ao servidor de comunicação. * 
 * 
 */
import br.sc.senai.envd.cripto.Cripto_Cliente;
import java.io.InputStream; 
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
    private static final String password = "123456";
       
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
     * @param byteTextoCriptografado , chavePriv
     * @param chavePrivada
     * @return
     */
    public static byte[] decriptarComChavePrivada (byte[] byteTextoCriptografado, PrivateKey chavePrivada){
        try {
            Cipher cifra = Cipher.getInstance(Cripto_Cliente.TIPO_ASSIMETRICO);
            cifra.init(Cipher.DECRYPT_MODE, chavePrivada);
            System.out.println("Crypto_Server: Tam bytes decriptados: " + byteTextoCriptografado.length);
            return cifra.doFinal(byteTextoCriptografado);
        }
        catch ( NoSuchAlgorithmException e ) {
            System.out.println("Classe Crypto_Server - Erro no Algoritmo RSA na decripta��o..." + e.getMessage());
        }  catch (NoSuchPaddingException e) {
            System.out.println("Classe Crypto_Server - Erro no Padding do RSA na decripta��o..." + e.getMessage());
        } catch (InvalidKeyException e) {
            System.out.println("Classe Crypto_Server - Erro na Chave RSA na  decripta��o..." + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.out.println("Classe Crypto_Server - Erro no tamanho de Bloco RSA na decripta��o..." + e.getMessage());
        } catch (BadPaddingException e) {
            System.out.println("Classe Crypto_Server - Erro Padding inv�lido no RSA para decripta��o..." + e.getMessage());
        }
        return null;
    }
    
    /**
     * Método para ler a chave Privada do Keystore
     * Retorna um objeto chave Privada PrivateKey
     * @return
     * @throws Exception 
    */
    public PrivateKey getPrivateKeyFromFile() throws Exception {
        KeyStore ks = KeyStore.getInstance ( "JKS" );
        ks.load( keystore, password.toCharArray());
        keystore.close();
        Key key = ks.getKey( alias, password.toCharArray() );
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
        KeyStore keyStore = KeyStore.getInstance ( "JKS" );
        keyStore.load( keystore, password.toCharArray() );
        Certificate certificado = keyStore.getCertificate( alias );
        PublicKey chavePublica = certificado.getPublicKey();
        System.out.println("Chave P�blica: " + chavePublica.toString());
        return chavePublica;
    }
    
    /**
     * Método para encriptar dados com a chave Simétrica.
     * @param bytesTextoPuro
     * @param bytesChaveSimetrica
     * @return bytes encriptados
     */
    public static byte[] encriptarComChaveSimetrica(byte[] bytesTextoPuro, byte[] bytesChaveSimetrica){
         try {
             Cipher cifra = Cipher.getInstance(Cripto_Cliente.TIPO_SIMETRICO);
             cifra.init(Cipher.ENCRYPT_MODE, new SecretKeySpec (bytesChaveSimetrica,Cripto_Cliente.TIPO_SIMETRICO));
             return cifra.update(bytesTextoPuro);
         } catch (NoSuchAlgorithmException ex){
            System.out.println("Erro no Algoritmo de  decripta��o sim�trica, verifique! " + ex.getMessage());
         } catch (NoSuchPaddingException ex) {
            System.out.println("Erro no Padding da decripta��o sim�trica, verifique! " + ex.getMessage());
         } catch (InvalidKeyException ex) {
            System.out.println("Erro na Chave sim�trica, verifique! " + ex.getMessage());
         }
        return null;
     }
    /**
     * Método para decriptar dados com uma chave Simétrica.
     * @param bytesTextoCriptografado
     * @param bytesChaveSimetrica
     * @return bytes decriptados.
     */
    public static byte[] decriptarComChaveSimetrica(byte[] bytesTextoCriptografado, byte[] bytesChaveSimetrica){
         try {
             Cipher cifra = Cipher.getInstance(Cripto_Cliente.TIPO_SIMETRICO);
             SecretKeySpec chaveSimetrica = new SecretKeySpec (bytesChaveSimetrica,Cripto_Cliente.TIPO_SIMETRICO);
             cifra.init(Cipher.DECRYPT_MODE, chaveSimetrica);
             return cifra.update(bytesTextoCriptografado);             
         }
         catch (NoSuchAlgorithmException ex){
           System.out.println("Erro no Algoritmo de  decripta��o sim�trica, verifique! " + ex.getMessage());
         } catch (NoSuchPaddingException ex) {
            System.out.println("Erro no Padding da decripta��o sim�trica, verifique! " + ex.getMessage());
         } catch (InvalidKeyException ex) {
            System.out.println("Erro na Chave sim�trica, verifique! " + ex.getMessage());
         }
        return null;
     }
}