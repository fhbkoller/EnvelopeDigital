package br.sc.senai.envd.fila;

import java.util.ArrayList;
import java.util.Map;

public class FilasLER extends Thread{
    
    private final Map<String,ArrayList> filas;
    private ArrayList Mensagens = new ArrayList();
    private String nomeFila = "";
    
    public FilasLER(String nomeFILA, Map<String,ArrayList> caixaMSG, ArrayList listaMensagens){
        super(nomeFILA);
        filas = caixaMSG;
        Mensagens = listaMensagens;
        nomeFila= nomeFILA;
    }
    
    public void lerFilamsg(){
        if(filas.containsKey(nomeFila)){
            ArrayList fila = filas.get(nomeFila);
            for (Object mensagem : fila) {
                Mensagens.add(mensagem);
                String msgP = (String) mensagem;
                System.out.println("Thread ler: " + nomeFila + " MSG: " + msgP);
            }
        } else {
            Mensagens.add(nomeFila + " sem Mensagens!");
           }    
    }

    @Override
    public void run(){
        lerFilamsg();
    }
}
