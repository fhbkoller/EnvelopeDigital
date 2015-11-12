package br.sc.senai.envd.mensageiro;

/**
 * Classe principal da solução de Mensagens cliente-servidor, utilizando
 * criptografia no modelo de envelopamento digital. W3-Security. Esta classe
 * implementa os métodos da interface mensageiro, utilizando-se da classe de
 * trabalho Cripto_Server. A classe está desenvolvida como um Sigletron para
 * controlar o disparo de serviços através de classes de serviços baseadas em
 * Threads.
 *
 */
import br.sc.senai.envd.fila.FilasGRAVAR;
import br.sc.senai.envd.fila.FilasLER;
import br.sc.senai.envd.cripto.Cripto_Server;
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

    private static final Map FILAS = Collections.synchronizedMap(new HashMap<String, ArrayList>());
    private static MensageiroImpl mensageiroImpl = null;
    private static byte[] bytesChaveSimetrica = null;

    private MensageiroImpl() throws RemoteException {
        super();
    }

    /**
     * Método para ler mensagens de uma fila previamente gravada.
     *
     * @param nomeFila
     * @return ArrayList com as mensagens.
     * @throws RemoteException
     */
    @Override
    public ArrayList lerFila(byte[] nomeFila) throws RemoteException {

        synchronized (this) {
            ArrayList listaMensagens = new ArrayList();
            ArrayList listaMsgCripto = new ArrayList();
            String nomeFilaDecriptado = new String(Cripto_Server.decriptarComChaveSimetrica(nomeFila, bytesChaveSimetrica));
            FilasLER lerFila = new FilasLER(nomeFilaDecriptado, FILAS, listaMensagens);
            lerFila.setPriority(Thread.NORM_PRIORITY);
            lerFila.setName(nomeFilaDecriptado);
            lerFila.start();
            if (lerFila.getName().equalsIgnoreCase(nomeFilaDecriptado)) {
                while (lerFila.isAlive()) {
                    lerFila.getState();
                }
            }
            for (int i = 0; i < listaMensagens.size(); i++) {
                String textoPuro = (String) listaMensagens.get(i);
                listaMsgCripto.add(i, Cripto_Server.encriptarComChaveSimetrica(textoPuro.getBytes(), bytesChaveSimetrica));
            }
            return listaMsgCripto;
        }
    }

    /**
     * Método para gravar dados numa fila de mensagens.
     *
     * @param nomeFilaG e MSG
     * @param MSG
     * @return
     */
    @Override
    public String gravaFila(byte[] nomeFilaG, byte[] MSG) {
        synchronized (this) {
            String textoPuro = new String(Cripto_Server.decriptarComChaveSimetrica(MSG, bytesChaveSimetrica));
            String nomeFilaPuro = new String(Cripto_Server.decriptarComChaveSimetrica(nomeFilaG, bytesChaveSimetrica));
            //System.out.println("Msg texto: " + msgpuro);
            FilasGRAVAR gravarFilas = new FilasGRAVAR(nomeFilaPuro, FILAS, textoPuro);
            gravarFilas.setPriority(Thread.MAX_PRIORITY);
            gravarFilas.start();
            //System.out.println("Gravando na " + nomeFilaG + " Mensagem " + MSG);
            return "[Servidor] Mensagem gravada na: " + nomeFilaPuro;
        }
    }

    /**
     * Método para excluir (deletar) uma fila de mensagens.
     *
     * @param nomeFilaG
     * @return mensagem de exclusão.
     */
    @Override
    public String deletaFila(byte[] nomeFilaG) {
        synchronized (this) {
            String nomeFilaP = new String(Cripto_Server.decriptarComChaveSimetrica(nomeFilaG, bytesChaveSimetrica));
            if (FILAS.containsKey(nomeFilaP)) {
                FILAS.remove(nomeFilaP);
                System.out.println("Servidor - " + nomeFilaP + " Exclu�da com sucesso!");
                return "[Servidor] " + nomeFilaP + " Exclu�da com sucesso!";
            } else {
                System.out.println("Servidor - " + nomeFilaP + " N�o EXISTE!");
                return "[Servidor] " + nomeFilaP + " N�o EXISTE!";
            }
        }
    }

    /**
     * Método para retornar a chave Pública do Keystore e disponibilizá-la
     * via serviço apresentado aos clientes por intermédio da interface
     * mensageiro.
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public PublicKey getChavePub() throws RemoteException {
        try {
            Cripto_Server criptoServer = new Cripto_Server();
            return criptoServer.getPublicKeyFromFile();
        } catch (Exception ex) {
            Logger.getLogger(MensageiroImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    /**
     * Método para inicializar a variável global chaveSim, com o valor de uma
     * chave recebida do cliente que deseja se comunicar com o servidor de
     * mensagens.
     *
     * @param bytesChaveSimetricaFromClient
     * @return
     */
    @Override
    public boolean gravarChaveSimetricaNoServidor(byte[] bytesChaveSimetricaFromClient) {
        try {
            Cripto_Server krp = new Cripto_Server();
            PrivateKey chavepriv = krp.getPrivateKeyFromFile();
            MensageiroImpl.bytesChaveSimetrica = Cripto_Server.decriptarComChavePrivada(bytesChaveSimetricaFromClient, chavepriv);
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
        if (mensageiroImpl == null) {
            try {
                mensageiroImpl = new MensageiroImpl();
                return mensageiroImpl;
            } catch (RemoteException ex) {
                Logger.getLogger(MensageiroImpl.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Erro grave: " + ex.getMessage());
                return null;
            }
        } else {
            return mensageiroImpl;
        }
    }
}
