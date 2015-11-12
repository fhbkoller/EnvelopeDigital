package br.sc.senai.envd.fila;

import java.util.ArrayList;
import java.util.Map;

public class FilasGRAVAR extends Thread{
    
    private final Map<String,ArrayList> filas;
    private String nomeFILA = "";
    private String Mensagem = null;
    
    public FilasGRAVAR(String nomeFila, Map<String,ArrayList> filasMSG, String MSG){
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
            System.out.println("Thread Gravar nova Mensagem na " + nomeFILA +" MSG: " + Mensagem);
        } else {
            listaMsg.add(Mensagem);
            filas.put(nomeFILA, listaMsg);
            System.out.println("Thread Gravar Mensagem inicial na " + nomeFILA +" MSG: " + Mensagem);
        }
    }
    @Override
    public void run(){
            gravarFilamsg();
    }
    
}
