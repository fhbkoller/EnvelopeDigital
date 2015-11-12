package br.sc.senai.envd.cripto;

/**
 * Classe de trabalho que implementa os mÃ©todos criptogrÃ¡ficos necessÃ¡rios para
 * o funcionamento do processo de criptografia entre um Cliente e o Servidor.
 * Esta classe atende somente ao servidor de comunicaÃ§Ã£o. * 
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
     Utilizado apenas para armazenar as chaves PÃºblica e Privada,
     necessÃ¡rias ao processo de criptografia. Utiliza algorÃ­tmo RSA.
     Comando para geraÃ§Ã£o do certificado:
     keytool -genkey -alias autentica -keyalg RSA -keypass seg2012 -storepass seg2012 -keystore env_digital.jks -validity 3650
    */

public class Cripto_Server {
  
    private final InputStream keystore = getClass().getResourceAsStream("/conf/envdigital.jks");
    private static final String alias = "envdigital"; 
    private static final String password = "123456";
       
    public Cripto_Server(){
    }
    /**
     MÃ©todo que decripta dados, encriptados pela chave PÃºblica
     contida no Keystore.
     ApÃ³s decriptado, retorna um array de bytes dos dados.
    * @param dados, chavePriv
    * @return 
    */

    /**
     * MÃ©todo que decripta dados, encriptados pela chave PÃºblica
     contida no Keystore.ApÃ³s decriptado, retorna um array de bytes dos dados.
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
            System.out.println("Classe Crypto_Server - Erro no Algoritmo RSA na decriptação..." + e.getMessage());
        }  catch (NoSuchPaddingException e) {
            System.out.println("Classe Crypto_Server - Erro no Padding do RSA na decriptação..." + e.getMessage());
        } catch (InvalidKeyException e) {
            System.out.println("Classe Crypto_Server - Erro na Chave RSA na  decriptação..." + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.out.println("Classe Crypto_Server - Erro no tamanho de Bloco RSA na decriptação..." + e.getMessage());
        } catch (BadPaddingException e) {
            System.out.println("Classe Crypto_Server - Erro Padding inválido no RSA para decriptação..." + e.getMessage());
        }
        return null;
    }
    
    /**
     * MÃ©todo para ler a chave Privada do Keystore
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
         MÃ©todo para ler a chave PÃºblica do Keystore
         Retorna um objeto chave PÃºblica PublicKey
        * @return
        * @throws Exception 
        */
    
    public PublicKey getPublicKeyFromFile() throws Exception {
        KeyStore keyStore = KeyStore.getInstance ( "JKS" );
        keyStore.load( keystore, password.toCharArray() );
        Certificate certificado = keyStore.getCertificate( alias );
        PublicKey chavePublica = certificado.getPublicKey();
        System.out.println("Chave Pública: " + chavePublica.toString());
        return chavePublica;
    }
    
    /**
     * MÃ©todo para encriptar dados com a chave SimÃ©trica.
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
            System.out.println("Erro no Algoritmo de  decriptação simétrica, verifique! " + ex.getMessage());
         } catch (NoSuchPaddingException ex) {
            System.out.println("Erro no Padding da decriptação simétrica, verifique! " + ex.getMessage());
         } catch (InvalidKeyException ex) {
            System.out.println("Erro na Chave simétrica, verifique! " + ex.getMessage());
         }
        return null;
     }
    /**
     * MÃ©todo para decriptar dados com uma chave SimÃ©trica.
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
           System.out.println("Erro no Algoritmo de  decriptação simétrica, verifique! " + ex.getMessage());
         } catch (NoSuchPaddingException ex) {
            System.out.println("Erro no Padding da decriptação simétrica, verifique! " + ex.getMessage());
         } catch (InvalidKeyException ex) {
            System.out.println("Erro na Chave simétrica, verifique! " + ex.getMessage());
         }
        return null;
     }
}