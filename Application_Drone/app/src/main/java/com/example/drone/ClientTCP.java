package com.example.drone;

import android.os.Handler;
import android.os.Message;
import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientTCP
{
    private Socket socket = null;
    private String adresseIPDuServeur;
    private int numeroDePortDuServeur;
    private Handler handler;
    public final static int CODE_CONNEXION = 0;
    public final static int CODE_RECEPTION = 1;
    public final static int CODE_DECONNEXION = 2;
    private LinkedBlockingQueue<String> qReception;
    private LinkedBlockingQueue<String> qEmission;
    private TClientTCP threadClientTCP;
    private Thread threadClient;

    public ClientTCP(String adresseServeur, int portServeur, Handler handlerUI)
    {
        adresseIPDuServeur = adresseServeur;
        numeroDePortDuServeur = portServeur;
        handler = handlerUI;
        qReception = new LinkedBlockingQueue<String>();
        qEmission = new LinkedBlockingQueue<String>();
    }

    public void demarrer()
    {
        if(threadClient == null)
        {
            threadClientTCP = new TClientTCP(this);
            threadClient = new Thread(threadClientTCP);
            threadClient.start();
        }
    }

    public void arreter() throws IOException
    {
        if(threadClient != null)
        {
            threadClientTCP.stop();
            Thread t = threadClient;
            threadClient = null;
            t.interrupt();
        }
    }

    public void connecter() throws IOException {
        socket =new Socket("192.168.1.9", 4242);
    }

    public void deconnecter() throws IOException {

        if(socket!=null){
        // socket connecté ?
        qReception.clear();
        qEmission.clear();
        socket.close();}
    }

    public void envoyer(String message)
    {
        qEmission.add(message);
    }

    public void setMessageLu(String message)
    {
        byte[] src = message.getBytes();
        byte[] dst = new byte[message.length()];
        System.arraycopy(src, 0, dst, 0, dst.length);
        String messageLu = new String(dst);
        diffuser(CODE_RECEPTION, messageLu);
        qReception.add(messageLu);
    }

    public String getMessageLu()
    {
        return qReception.poll();
    }

    public String getMessage()
    {
        return qEmission.poll();
    }

    public Socket getSocket()
    {
        return socket;
    }

    public void diffuser(int code, String message)
    {
        Message msg = Message.obtain();
        msg.what = code;
        if(message != null)
            msg.obj = message;
        handler.sendMessage(msg);
    }

    public void fermer() throws IOException
    {
        arreter();
        deconnecter();
    }
}