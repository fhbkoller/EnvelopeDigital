package br.sc.senai.envd.mensageiro;

/**
 * Classe servidora que inicia o serviço RMI, disponibilizando o objeto da
 * classe MensageiroImpl, no rmiregistry onde a classe clinte pode consumir os
 * métodos
 */
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class MensageiroServer {

    private static final String SERVIDOR_RMI = "localhost";
    
    private MensageiroServer(){}
    
     public static void main(String[] args) {
         createServer();
    }
    
    private static void createServer() {
        try {
            Mensageiro mensageiro = MensageiroImpl.getinstance();
            LocateRegistry.getRegistry(SERVIDOR_RMI, 1099).bind("ServicoEnvelopeDigital", mensageiro);
            System.out.println("MensageiroServer - Servidor RMI iniciado na porta 1099\n"
                    + "Servico Envelope com criptografia RSA e AES no ar!\n");
        } catch (RemoteException | AlreadyBoundException e) {
            System.out.println("Erro no servidor: " + e + "\n Servidor saindo..");
            System.exit(1);
        }
    }
}
