package br.sc.senai.envd;

/**
 * Classe de serviço que implementa os métodos criptográficos necessários para o funcionamento
 * do sistema de mensagens.
 * Atende somente a classe cliente do sistema de mensagens.
 */

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Cripto_Cliente {

  public Cripto_Cliente(){
     }
    /**
     Método que encripta dados, através de uma chave Pública
     recebida via RMI
     Após decriptado, retorna um array de bytes dos dados.
    * @param dados e chavePub
    * @return 
    */

     public static byte[] encripta (byte[] dados, PublicKey chavePub){
        try {
        Cipher cifra = Cipher.getInstance("RSA");
        cifra.init(Cipher.ENCRYPT_MODE, chavePub);
        System.out.println("[Cripto_Cliente] Tam bytes encriptados: " + dados.length);
        return cifra.doFinal(dados);
        }
        catch ( Exception e ) {
		System.out.println("[Cripto_Cliente] Erro na encriptação..." + e.getMessage());
                return null; //"Erro decriptação!".getBytes();
        }
       }
       
    /**
     * Método para encriptar dados com uma chave simétrica.
     * @param textoP e chaveS
     * @param chaveS
     * @return 
     */   
    public static byte[] encriptaSim(byte[] textoP, byte[] chaveS){
         try {
             Cipher cifra = Cipher.getInstance("AES/CBC/PKCS5Padding");
             IvParameterSpec ivspec = new IvParameterSpec (new byte[16]);
             cifra.init(Cipher.ENCRYPT_MODE, new SecretKeySpec (chaveS,"AES"),ivspec);
             return cifra.doFinal(textoP);
         } 
         catch (Exception ex){
           System.out.println("Erro de encriptação simétrica, Verifique! " + ex.getMessage());
              return null;
         }
     }
   /**
    * Método para decriptar dados com uma chave simétrica
    * @param textoC
    * @param chaveSeg
    * @return 
    */
   public static byte[] decriptaSim(byte[] textoC, byte[] chaveSeg){
         try {
             Cipher cifra = Cipher.getInstance("AES/CBC/PKCS5Padding");
             IvParameterSpec ivspec = new IvParameterSpec (new byte[16]);
             cifra.init(Cipher.DECRYPT_MODE, new SecretKeySpec (chaveSeg,"AES"),ivspec);
             return cifra.doFinal(textoC);             
         } 
         catch (Exception ex){
           System.out.println("Erro na decriptação simétrica, Verifique! " + ex.getMessage());
              return null;
         }
     }
    /**
     * Método para geração de uma chave simétrica de sessão, a qual será usada nas
     * comunicações entre o cliente e o servidor.
     * Este método retorna os bytes codificados de uma chave simétrica.
     * @return
     * @throws NoSuchAlgorithmException 
     */   
    public static byte[] getChaveSimetrica() throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        SecretKey chaveSim = keygen.generateKey();
        if (chaveSim instanceof SecretKey){
           return chaveSim.getEncoded();
        } else {
            return null;
        }
    }
    
}