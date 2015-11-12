package br.sc.senai.envd;


import java.util.ArrayList;
import java.util.Map;


public class filasGRAVAR extends Thread{
    
    private Map<String,ArrayList> filas;
    private String nomeFILA = "";
    private String Mensagem = null;
    
    public filasGRAVAR(String nomeFila, Map<String,ArrayList> filasMSG, String MSG){
        super(nomeFila);
        filas = filasMSG;
        nomeFILA= nomeFila;
        Mensagem = MSG;
    }
    
    public void gravarFilamsg(){
        ArrayList listaMsg = new ArrayList();
        if (filas.containsKey(nomeFILA)){
            listaMsg = filas.get(nomeFILA);
            listaMsg.add(Mensagem);
            filas.put(nomeFILA, listaMsg);
            System.out.println("Thread Gravar nova Mensagem na " + nomeFILA +" MSG: " + Mensagem.toString());
        } else {
            listaMsg.add(Mensagem);
            filas.put(nomeFILA, listaMsg);
            System.out.println("Thread Gravar Mensagem inicial na " + nomeFILA +" MSG: " + Mensagem.toString());
        }
    }
    @Override
    public void run(){
      
            gravarFilamsg();
    }
    
}
