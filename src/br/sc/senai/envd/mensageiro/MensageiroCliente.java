package br.sc.senai.envd.mensageiro;

/**
 * Classe que implementa os métodos do Cliente no sistema de mensagens. Esta
 * classe implementa os métodos necessários para a comunicação segura com o
 * servidor, através do uso da criptografia forte com RSA e criptografia
 * simétrica o algoritmo AES A solução implementa o conceito de envelopamento
 * digital no padrão W3-security.
 */
import br.sc.senai.envd.cripto.Cripto_Cliente;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Iterator;

public class MensageiroCliente {

    private static final String SERVIDOR_RMI = "localhost";
    private static String nomeFila = "";
    private static String mensagem = "";
    private static ArrayList listaMensagens = new ArrayList();
    private static final int NUMERO_FILAS = 5;
    private static final int NUMERO_MENSAGENS = 3;
    private static byte[] chaveSimetrica = null;
    private static PublicKey chavePublica = null;

    private MensageiroCliente(){}
    
    public static void main(String args[]) {
        try {
            Mensageiro mensageiro = (Mensageiro) Naming.lookup("rmi://" + SERVIDOR_RMI + "/ServicoEnvelopeDigital");
            String servicos[] = Naming.list("rmi://" + SERVIDOR_RMI);
            System.out.println("Lista de servi�os dispon�veis:");

            for (String servico : servicos) {
                System.out.println(servico);
            }
            
            //Obtem chave pública do servidor
            chavePublica = mensageiro.getChavePub();
            //Cria uma chave simétrica de sessão e encripta com
            //a chave pública obtida do servidor.
            chaveSimetrica = Cripto_Cliente.getChaveSimetrica();
            byte[] chaveSimetricaEncriptada = Cripto_Cliente.encriptarComChavePublica(chaveSimetrica, chavePublica);
            //Envia a chave simétrica criptografada ao servidor
            //e em caso de sucesso, inicia o processo de comunicação
            //criando filas e mensagens.
            if (!mensageiro.gravarChaveSimetricaNoServidor(chaveSimetricaEncriptada)) {
                System.out.println("Erro ao enviar chave sim�trica ao servidor!");
                System.exit(1);
            }
            // Cria as filas e mensagens, encripta e grava no servidor
            //                           
            for (int i = 1; i <= NUMERO_FILAS; i++) {
                nomeFila = "Professor [" + i + "]";
                for (int k = 1; k <= NUMERO_MENSAGENS; k++) {
                    mensagem = " MSG nr: " + k;
                    byte[] bytesMensagemEncriptado = Cripto_Cliente.encriptarComChaveSimetrica(mensagem.getBytes(), chaveSimetrica);
                    byte[] bytesNomeFilaEncriptado = Cripto_Cliente.encriptarComChaveSimetrica(nomeFila.getBytes(), chaveSimetrica);
                    System.out.println(mensageiro.gravaFila(bytesNomeFilaEncriptado, bytesMensagemEncriptado));
                }
            }
            // Define os nomes das filas, encripta e consulta no servidor
            //                           
            for (int j = 1; j <= NUMERO_FILAS; j++) {
                nomeFila = "Professor [" + j + "]";
                byte[] bytesNomeFilaCriptografado = Cripto_Cliente.encriptarComChaveSimetrica(nomeFila.getBytes(), chaveSimetrica);
                listaMensagens = mensageiro.lerFila(bytesNomeFilaCriptografado);
                System.out.print(nomeFila);
                byte[] bytesMensagemEncriptado;
                Iterator iterator = listaMensagens.iterator();
                System.out.print(" - msg decriptadas: ");
                while (iterator.hasNext()) {
                    bytesMensagemEncriptado = (byte[]) iterator.next();
                    String mensagemDecriptada = new String(Cripto_Cliente.decriptarComChaveSimetrica(bytesMensagemEncriptado, chaveSimetrica));
                    System.out.print("[" + mensagemDecriptada + "]");
                }
                System.out.print("\n");
            }
            // Define as filas, encripta e solicita a exclusão do servidor
            //
            for (int k = 1; k <= NUMERO_FILAS; k++) {
                nomeFila = "Professor [" + k + "]";
                byte[] bytesNomeFilaEncriptado = Cripto_Cliente.encriptarComChaveSimetrica(nomeFila.getBytes(), chaveSimetrica);
                System.out.println("Excluindo: " + nomeFila + " " + mensageiro.deletaFila(bytesNomeFilaEncriptado));
            }

        } catch (MalformedURLException e) {
            System.out.println();
            System.out.println("MalformedURLException: " + e.getMessage());
        } catch (RemoteException e) {
            System.out.println();
            System.out.println("RemoteException: " + e.getMessage());
        } catch (NotBoundException e) {
            System.out.println();
            System.out.println("NotBoundException: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println();
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
