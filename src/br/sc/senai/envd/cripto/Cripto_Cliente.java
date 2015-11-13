package br.sc.senai.envd.cripto;

/**
 * Classe de servi�o que implementa os m�todos criptogr�ficos necess�rios para o funcionamento
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
     * M�todo que encripta dados, atrav�s de uma chave P�blica
     recebida via RMI
     Ap�s decriptado, retorna um array de bytes dos dados.
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
            System.out.println("[Cripto_Cliente] Erro na encripta��o..." + e.getMessage());                
        }
        return null; //"Erro decripta��o!".getBytes();
     }
      
    /**
     * M�todo para encriptar dados com uma chave sim�trica.
     * @param bytes e chaveS
     * @param bytesChaveSimetrica
     * @return 
     */   
    public static byte[] encriptaComChaveSimetrica(byte[] bytes, byte[] bytesChaveSimetrica){
         try {
             Cipher cifra = inicializarCifra(TIPO_SIMETRICO, Cipher.ENCRYPT_MODE, new SecretKeySpec (bytesChaveSimetrica,TIPO_SIMETRICO));
             return cifra.update(bytes);
         } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex){
           System.out.println("Erro de encripta��o sim�trica, Verifique! " + ex.getMessage());
         }
         return null;
     }
   /**
    * M�todo para decriptar dados com uma chave sim�trica
    * @param bytesCripto
    * @param bytesChaveSimetrica
    * @return 
    */
   public static byte[] decriptaComChaveSimetrica(byte[] bytesCripto, byte[] bytesChaveSimetrica){
         try {
             Cipher cifra = inicializarCifra(TIPO_SIMETRICO, Cipher.DECRYPT_MODE, new SecretKeySpec (bytesChaveSimetrica,TIPO_SIMETRICO));
             return cifra.update(bytesCripto);             
         } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex){
           System.out.println("Erro na decripta��o sim�trica, Verifique! " + ex.getMessage());
         }
         return null;
    }
    /**
     * M�todo para gera��o de uma chave sim�trica de sess�o, a qual ser� usada nas
     * comunica��es entre o cliente e o servidor.
     * Este m�todo retorna os bytes codificados de uma chave sim�trica.
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