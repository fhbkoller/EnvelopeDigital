package br.sc.senai.envd.cripto;

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

    private Cripto_Cliente(){
    }
    
    /**
     * Método que encripta dados, através de uma chave Pública
     recebida via RMI
     Após decriptado, retorna um array de bytes dos dados.
     * @param bytes
     * @param chavePublica
     * @return
     */
    public static byte[] encriptaComChavePublica (byte[] bytes, PublicKey chavePublica){
        try {
            Cipher cifra = inicializarCifra(TIPO_ASSIMETRICO, Cipher.ENCRYPT_MODE, chavePublica);
            System.out.println("[Cripto_Cliente] Tam bytes encriptados: " + bytes.length);
            return cifra.doFinal(bytes);
        } catch ( NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e ) {
            System.out.println("[Cripto_Cliente] Erro na encriptação..." + e.getMessage());                
        }
        return null; //"Erro decriptação!".getBytes();
     }
      
    /**
     * Método para encriptar dados com uma chave simétrica.
     * @param bytes e chaveS
     * @param bytesChaveSimetrica
     * @return 
     */   
    public static byte[] encriptaComChaveSimetrica(byte[] bytes, byte[] bytesChaveSimetrica){
         try {
             Cipher cifra = inicializarCifra(TIPO_SIMETRICO, Cipher.ENCRYPT_MODE, new SecretKeySpec (bytesChaveSimetrica,TIPO_SIMETRICO));
             return cifra.update(bytes);
         } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex){
           System.out.println("Erro de encriptação simétrica, Verifique! " + ex.getMessage());
         }
         return null;
     }
   /**
    * Método para decriptar dados com uma chave simétrica
    * @param bytesCripto
    * @param bytesChaveSimetrica
    * @return 
    */
   public static byte[] decriptaComChaveSimetrica(byte[] bytesCripto, byte[] bytesChaveSimetrica){
         try {
             Cipher cifra = inicializarCifra(TIPO_SIMETRICO, Cipher.DECRYPT_MODE, new SecretKeySpec (bytesChaveSimetrica,TIPO_SIMETRICO));
             return cifra.update(bytesCripto);             
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
        SecretKey chaveSimetrica = keygen.generateKey();
        if (chaveSimetrica instanceof SecretKey){
           return chaveSimetrica.getEncoded();
        }
        return null;       
    }
    
    private static Cipher inicializarCifra(String tipo, int modo, Key chave) throws InvalidKeyException,NoSuchAlgorithmException,NoSuchPaddingException {
        Cipher cifra = Cipher.getInstance(tipo);
        cifra.init(modo, chave);
        return cifra;
    }
}