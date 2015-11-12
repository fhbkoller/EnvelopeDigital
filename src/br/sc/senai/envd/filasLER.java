package br.sc.senai.envd;


import java.util.ArrayList;
import java.util.Map;


public class FilasLER extends Thread{
    
    private Map<String,ArrayList> filas;
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
            ArrayList temp = new ArrayList();
            temp = filas.get(nomeFila);
            for (int i=0;i<temp.size();i++){
                Mensagens.add(temp.get(i));
                String msgP = (String) temp.get(i);
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
