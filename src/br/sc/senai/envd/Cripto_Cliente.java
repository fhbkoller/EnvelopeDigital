package br.sc.senai.envd;

/**
 * Classe de serviço que implementa os métodos criptográficos necessários para o funcionamento
 * do sistema de mensagens.
 * Atende somente a classe cliente do sistema de mensagens.
 */

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Cripto_Cliente {
    
    public static final String TIPO_ASSIMETRICO = "RSA";
    public static final String TIPO_SIMETRICO = "RC4";

    public Cripto_Cliente(){
    }
    /**
     Método que encripta dados, através de uma chave Pública
     recebida via RMI
     Após decriptado, retorna um array de bytes dos dados.
    * @param bytesTextoPuro e chavePub
    * @return 
    */

    /**
     * Método que encripta dados, através de uma chave Pública
     recebida via RMI
     Após decriptado, retorna um array de bytes dos dados.
     * @param bytesTextoPuro e chavePub
     * @param chavePublica
     * @return
     */
    public static byte[] encriptarComChavePublica (byte[] bytesTextoPuro, PublicKey chavePublica){
        try {
            Cipher cifra = inicializarCifra(TIPO_ASSIMETRICO, Cipher.ENCRYPT_MODE, chavePublica);
            System.out.println("[Cripto_Cliente] Tam bytes encriptados: " + bytesTextoPuro.length);
            return cifra.doFinal(bytesTextoPuro);
        } catch ( NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e ) {
            System.out.println("[Cripto_Cliente] Erro na encriptação..." + e.getMessage());                
        }
        return null; //"Erro decriptação!".getBytes();
     }
      
    /**
     * Método para encriptar dados com uma chave simétrica.
     * @param bytesTextoPuro e chaveS
     * @param bytesChaveSimetrica
     * @return 
     */   
    public static byte[] encriptarComChaveSimetrica(byte[] bytesTextoPuro, byte[] bytesChaveSimetrica){
         try {
             Cipher cifra = inicializarCifra(TIPO_SIMETRICO, Cipher.ENCRYPT_MODE, new SecretKeySpec (bytesChaveSimetrica,TIPO_SIMETRICO));
             return cifra.update(bytesTextoPuro);
         } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex){
           System.out.println("Erro de encriptação simétrica, Verifique! " + ex.getMessage());
         }
         return null;
     }
   /**
    * Método para decriptar dados com uma chave simétrica
    * @param bytesTextoCriptografado
    * @param bytesChaveSimetrica
    * @return 
    */
   public static byte[] decriptarComChaveSimetrica(byte[] bytesTextoCriptografado, byte[] bytesChaveSimetrica){
         try {
             Cipher cifra = inicializarCifra(TIPO_SIMETRICO, Cipher.DECRYPT_MODE, new SecretKeySpec (bytesChaveSimetrica,TIPO_SIMETRICO));
             return cifra.update(bytesTextoCriptografado);             
         } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex){
           System.out.println("Erro na decriptação simétrica, Verifique! " + ex.getMessage());
         }
         return null;
    }
    /**
     * Método para geração de uma chave simétrica de sessão, a qual será usada nas
     * comunicações entre o cliente e o servidor.
     * Este método retorna os bytes codificados de uma chave simétrica.
     * @return
     * @throws NoSuchAlgorithmException 
     */   
    public static byte[] getChaveSimetrica() throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance(TIPO_SIMETRICO);
        keygen.init(128);
        SecretKey chaveSim = keygen.generateKey();
        if (chaveSim instanceof SecretKey){
           return chaveSim.getEncoded();
        }
        return null;       
    }
    
    private static Cipher inicializarCifra(String tipo, int modo, Key chave) throws InvalidKeyException,NoSuchAlgorithmException,NoSuchPaddingException {
        Cipher cifra = Cipher.getInstance(tipo);
        cifra.init(modo, chave);
        return cifra;
    }
}