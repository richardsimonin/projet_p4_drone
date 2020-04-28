package com.example.drone;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TClientTCP implements Runnable
{
    private ClientTCP clientTCP = null;
    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private volatile boolean fini = false;
    private InputStream stream = null;

    public TClientTCP(ClientTCP clientTCP)
    {
        this.clientTCP = clientTCP;
        fini = false;
    }

    public void run()
    {
        //connexion client
        try {
            clientTCP.connecter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        socket = clientTCP.getSocket();
        if(socket == null)
        {
            return;
        }

        initialiser();

        // boucle d'émission et de réception de messages réseaux
        String message = null;
        String messageLu = null;

        while(!fini)
        {
            // un message à envoyer ?
            message = clientTCP.getMessage();
            if(message != null)
            {
                out.println(message);
                out.flush();
                System.out.println("Envoi : " + message);
            }

            // un message à receptionner ?
            try
            {
                if(stream.available() > 1)
                {
                    messageLu = in.readLine();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            if(messageLu != null)
            {
                clientTCP.setMessageLu(messageLu);
                System.out.println("Lu : " + messageLu);
                messageLu = null;
            }
        }
        // fin de la boucle
        try {
            clientTCP.deconnecter();//déconnexion client
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void initialiser()
    {
        // crée et associe le flux de sortie à la socket
        try
        {
            if(socket != null)
            {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            }
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // crée et associe le flux d'entrée à la socket
        try
        {
            if(socket != null)
            {
                stream = socket.getInputStream();
                in = new BufferedReader(new InputStreamReader(stream));
            }
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void stop() throws IOException
    {
        if(fini == false)
        {
            fini = true;
            if(in != null && out != null)
            {
                in.close();
                out.close();
            }
        }
    }
}
