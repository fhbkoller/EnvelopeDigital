package br.sc.senai.envd;

/**
 * Classe principal da solução de Mensagens cliente-servidor, utilizando criptografia
 * no modelo de envelopamento digital. W3-Security.
 * Esta classe implementa os métodos da interface mensageiro, utilizando-se da 
 * classe de trabalho Cripto_Server.
 * A classe está desenvolvida como um Sigletron para controlar o disparo de serviços
 * através de classes de serviços baseadas em Threads.
 * 
 */

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MensageiroImpl extends UnicastRemoteObject implements Mensageiro {
        private static Map filamsg = Collections.synchronizedMap(new HashMap<String,ArrayList>()); 
        private static MensageiroImpl instanciaMSG = null;
        private static byte[] chaveSim = null;

	private MensageiroImpl() throws RemoteException {
	    super();
 	}

        /**
         * Método para ler mensagens de uma fila previamente gravada.
         * @param nomeFila
         * @return ArrayList com as mensagens.
         * @throws RemoteException 
         */
    @Override
        public ArrayList lerFila(byte[] nomeFila) throws RemoteException{
            
            synchronized(this){
                ArrayList listaMensagens = new ArrayList();
                ArrayList listaMsgCripto = new ArrayList();
                String nomeFilaP = new String(Cripto_Server.decriptaSim(nomeFila, chaveSim));
                filasLER lerF = new filasLER(nomeFilaP, filamsg, listaMensagens);
                lerF.setPriority(Thread.NORM_PRIORITY);
                lerF.setName(nomeFilaP);
                lerF.start();
                if (lerF.getName().equalsIgnoreCase(nomeFilaP)){
                    while (lerF.isAlive()){
                        lerF.getState();
                    }
                }
                for (int k=0;k<listaMensagens.size();k++){
                    String dadosP = (String) listaMensagens.get(k);
                    listaMsgCripto.add(k, Cripto_Server.encriptaSim(dadosP.getBytes(), chaveSim));
               }
                return listaMsgCripto;
            }
        }
    
    /**
     * Método para gravar dados numa fila de mensagens.
     * @param nomeFilaG e MSG
     * @param MSG
     * @return 
     */
    @Override
       public String gravaFila(byte[] nomeFilaG, byte[] MSG){
           synchronized(this){
             String msgpuro = new String(Cripto_Server.decriptaSim(MSG, chaveSim));
             String nomeFilaP = new String(Cripto_Server.decriptaSim(nomeFilaG,chaveSim));
             //System.out.println("Msg texto: " + msgpuro);
             filasGRAVAR gravaF = new filasGRAVAR(nomeFilaP,filamsg,msgpuro);
             gravaF.setPriority(Thread.MAX_PRIORITY);
             gravaF.start();
             //System.out.println("Gravando na " + nomeFilaG + " Mensagem " + MSG);
             return "[Servidor] Mensagem gravada na: " + nomeFilaP;
             }
          }
    
    /**
     * Método para excluir (deletar) uma fila de mensagens.
     * @param nomeFilaG
     * @return mensagem de exclusão.
     */
    @Override
       public String deletaFila(byte[] nomeFilaG){
           synchronized(this){
             String nomeFilaP = new String(Cripto_Server.decriptaSim(nomeFilaG,chaveSim));
             if (filamsg.containsKey(nomeFilaP)){
                 filamsg.remove(nomeFilaP);
                 System.out.println("Servidor - " + nomeFilaP + " Excluída com sucesso!");
                 return "[Servidor] " + nomeFilaP + " Excluída com sucesso!";
             } else {
                 System.out.println("Servidor - " + nomeFilaP + " Não EXISTE!");
                 return "[Servidor] " + nomeFilaP + " Não EXISTE!";                 
             }
           }
       }
    
    /**
     * Método para retornar a chave Pública do Keystore e disponibilizá-la via serviço
     * apresentado aos clientes por intermédio da interface mensageiro.
     * @return
     * @throws RemoteException 
     */
    @Override
    public PublicKey getChavePub() throws RemoteException {
        try {
            Cripto_Server krps = new Cripto_Server();
            return krps.getPublicKeyFromFile();
        } catch (Exception ex) {
            Logger.getLogger(MensageiroImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }
    
    /**
     * Método para inicializar a variável global chaveSim, com o valor de uma chave
     * recebida do cliente que deseja se comunicar com o servidor de mensagens.
     * @param chaveSimetrica
     * @return 
     */
    
    public boolean gravaChaveSim(byte[] chaveSimetrica) {
        try {
            Cripto_Server krp = new Cripto_Server();            
            PrivateKey chavepriv = krp.getPrivateKeyFromFile();
            chaveSim = Cripto_Server.decripta(chaveSimetrica,chavepriv);
            return true;           
        } catch (Exception ex) {
            Logger.getLogger(MensageiroImpl.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    /**
     * Método para obtenção de uma instância desta classe
     * 
     * @return 
     */
     
    public static synchronized MensageiroImpl getinstance() {
        if(instanciaMSG == null){
            try {
                instanciaMSG = new MensageiroImpl();
                return instanciaMSG;
            } catch (RemoteException ex) {
                Logger.getLogger(MensageiroImpl.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Erro grave: " + ex.getMessage());
                return null;
            }
        } else {
            return instanciaMSG;
        }
    }
}
