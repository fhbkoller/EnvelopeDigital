package br.sc.senai.envd;

/**
 * Classe que implementa os métodos do Cliente no sistema de mensagens.
 * Esta classe implementa os métodos necessários para a comunicação segura com o 
 * servidor, através do uso da criptografia forte com RSA e criptografia simétrica
 * o algoritmo AES
 * A solução implementa o conceito de envelopamento digital no padrão W3-security.
 */

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Iterator;

public class MensageiroCliente {
    
        private static String servidor = "localhost";
        private static String nomeFila = "";
        private static String mensagem = "";
        private static ArrayList listaMensagens = new ArrayList();
        private static int nrfilas = 5;
        private static int nrmsg = 3;
        private static byte[] chs = null;
        private static PublicKey pubKey = null;

	public static void main( String args[] ) {
		try {
			Mensageiro msgs = (Mensageiro) Naming.lookup( "rmi://" + servidor + "/ServicoEnvelopeDigital" );
                        String servicos[] = Naming.list("rmi://" + servidor);
                        System.out.println("Lista de serviços disponíveis:");
                        for (int i=0; i < servicos.length ; i++){
                        System.out.println(servicos[i]);
                        }
                        //Obtem chave pública do servidor
                        //
                        pubKey = msgs.getChavePub();
                        //Cria uma chave simétrica de sessão e encripta com
                        //a chave pública obtida do servidor.
                        //
                        chs = Cripto_Cliente.getChaveSimetrica();
                        byte[] chsByte = Cripto_Cliente.encripta(chs, pubKey);
                        //
                        //Envia a chave simétrica criptografada ao servidor
                        //e em caso de sucesso, inicia o processo de comunicação
                        //criando filas e mensagens.
                        //
                        if (msgs.gravaChaveSim(chsByte)){
                         //
                         // Cria as filas e mensagens, encripta e grava no servidor
                         //                           
                         for (int i=1;i <= nrfilas; i++){
                            nomeFila = "Professor [" + i + "]";
                            for (int k=1;k<=nrmsg;k++){
                                mensagem = " MSG nr: " + k;
                                byte[] msgCript = Cripto_Cliente.encriptaSim(mensagem.getBytes(),chs);
                                byte[] nomeFilaC = Cripto_Cliente.encriptaSim(nomeFila.getBytes(),chs);
                                System.out.println(msgs.gravaFila(nomeFilaC, msgCript));
                            }
                        }
                         //
                         // Define os nomes das filas, encripta e consulta no servidor
                         //                           
                        for (int j=1;j<=nrfilas;j++){
                            nomeFila = "Professor [" + j + "]";
                            byte[] nomeFilaC = Cripto_Cliente.encriptaSim(nomeFila.getBytes(),chs);
                            listaMensagens = msgs.lerFila(nomeFilaC);
                            System.out.print(nomeFila);
                            byte[] msgCrip = null;
                            Iterator it = listaMensagens.iterator();
                            System.out.print(" - msg decriptadas: ");
                            while (it.hasNext()){
                                msgCrip = (byte[]) it.next();
                                String msgPuro = new String(Cripto_Cliente.decriptaSim(msgCrip,chs));
                                System.out.print("["+ msgPuro + "]");
                            }
                            System.out.print("\n");
                        }
                        //
                        // Define as filas, encripta e solicita a exclusão do servidor
                        //
                        for (int k=1;k<=nrfilas;k++){
                            nomeFila = "Professor [" + k + "]";
                            byte[] nomeFilaC = Cripto_Cliente.encriptaSim(nomeFila.getBytes(),chs);
                            System.out.println("Excluíndo: " + nomeFila + " " + msgs.deletaFila(nomeFilaC));
                        }
                      } else {
                         System.out.println("Erro ao enviar chave simétrica ao servidor!");
                         System.exit(1);
                        }
		}
		catch( MalformedURLException e ) {
			System.out.println();
			System.out.println( "MalformedURLException: " + e.toString() );
		}
		catch( RemoteException e ) {
			System.out.println();
			System.out.println( "RemoteException: " + e.toString() );
		}
		catch( NotBoundException e ) {
			System.out.println();
			System.out.println( "NotBoundException: " + e.toString() );
		}
		catch( Exception e ) {
			System.out.println();
			System.out.println( "Exception: " + e.toString() );
		}
	}
}