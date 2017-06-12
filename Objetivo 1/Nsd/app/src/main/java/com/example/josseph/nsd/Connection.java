package com.example.josseph.nsd;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by josseph on 30/08/16.
 */
public class Connection {
    private Handler mUpdateHandler;
    private Server mServer;
    private Client mClient;

    private static final String TAG = "Nsd";

    private Socket mSocket;
    private int mPort = -1;

    public Connection(Handler handler) {
        mUpdateHandler = handler;
        mServer = new Server(handler);
    }

    public void tearDown() {
        mServer.tearDown();
        if (mClient != null) {
            mClient.tearDown();
        }
    }

    public void connectToServer(InetAddress address, int port) {
        mClient = new Client(address, port);
    }

    public void sendMessage(String msg) {
        if (mClient != null) {
            mClient.send(msg);
        }
    }

    public int getLocalPort() {
        return mPort;
    }

    public void setLocalPort(int port) {
        mPort = port;
    }


    public synchronized void updateMessages(String msg, boolean local) {
       Log.e(TAG, "Mensaje sincronizado: " + msg);

        if (local) {
            msg = "Yo: " + msg;
        } else {
            msg = "El: " + msg;
        }

        Bundle messageBundle = new Bundle();
        messageBundle.putString("msg", msg);

        Message message = new Message();
        message.setData(messageBundle);
        mUpdateHandler.sendMessage(message);

    }

    private synchronized void setSocket(Socket socket) {
        Log.d(TAG, "llamando Socket");

        if (socket == null) {
            Log.d(TAG, "socket nulo");
        }
        if (mSocket != null) {
            if (mSocket.isConnected()) {
                try {
                    mSocket.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }
        mSocket = socket;
    }

    private Socket getSocket() {
        return mSocket;
    }

    private class Server {
        ServerSocket mServerSocket = null;
        Thread mThread = null;

        public Server(Handler handler) {
            mThread = new Thread(new ServerThread());
            mThread.start();
        }

        public void tearDown() {
            mThread.interrupt();
            try {
                mServerSocket.close();
            } catch (IOException ioe) {
                Log.e(TAG, "Error al cerrar socket del servidor");
            }
        }

        class ServerThread implements Runnable {
            @Override
            public void run() {
                try {
                    // Desde el descubrimiento va a pasar a través de Nsd, no tenemos que preocuparnos de qué puerto es
                    // usado. Sólo tienes que arrastrar una disponible y la anunciará a través de Nsd.
                    mServerSocket = new ServerSocket(0);
                    //ServerSocket(0) busca un puerto disponible
                    setLocalPort(mServerSocket.getLocalPort());
                    //captura el puerto
                    while (!Thread.currentThread().isInterrupted()) {
                        Log.d(TAG, "Creado SocketServer, Esperando conexion");

                        setSocket(mServerSocket.accept());
                        // Bloquea la ejecucion del programa hasta que se realiza una conexion
                        Log.d(TAG, "Conectado");
                        /* 
                       if (mClient == null) {
                            int port = mSocket.getPort();
                            InetAddress address = mSocket.getInetAddress();
                            connectToServer(address, port);
                        }*/
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error creando ServerSocket: ", e);
                    e.printStackTrace();
                }
            }
        }
    }

    private class Client {

        private InetAddress mAddress;
        private int PORT;

        private final String CLIENT_TAG = "Cliente";

        private Thread mSendThread;
        private Thread mRecThread;

        public Client(InetAddress address, int port) {

            Log.d(CLIENT_TAG, "Creando cliente");
            this.mAddress = address;
            this.PORT = port;

            mSendThread = new Thread(new SendingThread());
            mSendThread.start();
        }

        class SendingThread implements Runnable {

            BlockingQueue<String> mMessageQueue;
            private int QUEUE_CAPACITY = 10;

            public SendingThread() {
                mMessageQueue = new ArrayBlockingQueue<String>(QUEUE_CAPACITY);
            }

            @Override
            public void run() {
                try {
                    if (getSocket() == null) {
                        setSocket(new Socket(mAddress, PORT));
                        Log.d(CLIENT_TAG, "Socket del cliente iniciando");

                    } else {
                        Log.d(CLIENT_TAG, "Socket ya se encuentra iniciado");
                    }

                    mRecThread = new Thread(new Receiving());
                    mRecThread.start();

                } catch (UnknownHostException e) {
                    Log.d(CLIENT_TAG, "Fallo la habilitacion del Socket, UHE", e);
                } catch (IOException e) {
                    Log.d(CLIENT_TAG, "Fallo la habilitacion del Socket, IOE.", e);
                }

                while (true) {
                    try {
                        String msg = mMessageQueue.take();
                        send(msg);
                    } catch (InterruptedException ie) {
                        Log.d(CLIENT_TAG, "Envio de mensaje interrumpido, Enviando anterior");
                    }
                }
            }
        }

        class Receiving implements Runnable {

            @Override
            public void run() {

                BufferedReader input;
                try {
                    input = new BufferedReader(new InputStreamReader(
                            mSocket.getInputStream()));
                    while (!Thread.currentThread().isInterrupted()) {

                        String messageStr = null;
                        messageStr = input.readLine();
                        if (messageStr != null) {
                            Log.d(CLIENT_TAG, "Leyendo mensaje entrante: " + messageStr);
                            updateMessages(messageStr, false);
                        } else {
                            Log.d(CLIENT_TAG, "Nulo! Nulo!");
                            break;
                        }
                    }
                    input.close();

                } catch (IOException e) {
                    Log.e(CLIENT_TAG, "Error bucle del servidor: ", e);
                }
            }
        }

        public void tearDown() {
            try {
                getSocket().close();
            } catch (IOException ioe) {
                Log.e(CLIENT_TAG, "Error al cerrar socket del servidor.");
            }
        }

        public void send(String msg) {
            try {
                Socket socket = getSocket();
                if (socket == null) {
                    Log.d(CLIENT_TAG, "Socket es nulo");
                } else if (socket.getOutputStream() == null) {
                    Log.d(CLIENT_TAG, "flujo de salida del Socket nulo");
                }

                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(getSocket().getOutputStream())), true);
                out.println(msg);
                out.flush();
                updateMessages(msg, true);
            } catch (UnknownHostException e) {
                Log.d(CLIENT_TAG, "Host desconocido", e);
            } catch (IOException e) {
                Log.d(CLIENT_TAG, "I/O Exception", e);
            } catch (Exception e) {
                Log.d(CLIENT_TAG, "Error", e);
            }
            Log.d(CLIENT_TAG, "Enviando el siguiente mensaje: " + msg);
        }
    }
}
