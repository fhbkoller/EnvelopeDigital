package br.sc.senai.envd;

/**
 * Classe servidora que inicia o serviço RMI, disponibilizando o objeto da
 * classe MensageiroImpl, no rmiregistry onde a classe clinte pode consumir
 * os métodos
 */
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MensageiroServer {
    private String servidor = "localhost";

	public MensageiroServer() {
		try {
			Mensageiro msg = MensageiroImpl.getinstance();
                        Registry registry = LocateRegistry.createRegistry(1099);
                        registry = LocateRegistry.getRegistry(servidor, 1099);
                        registry.bind("ServicoEnvelopeDigital", msg);
                        System.out.println("MensageiroServer - Servidor RMI iniciado na porta 1099\n"
                                +          "Servico Envelope com criptografia RSA e AES no ar!\n");
                } 
		catch( Exception e ) {
			System.out.println( "Erro no servidor: " + e + "\n Servidor saindo.." );
                        System.exit(1);
		}
	}
        
	public static void main(String[] args) {
        MensageiroServer msgServer = new MensageiroServer();
	}
}