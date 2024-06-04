package Server;

import Client.User;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.Arrays;
import javax.swing.table.DefaultTableModel;
import java.lang.Object;


public class Server {
    //private int clientNum;
    private ArrayList<DataOutputStream> listOut = new ArrayList<DataOutputStream>();
    private ArrayList<DataInputStream> listIn = new ArrayList<DataInputStream>();
    private ArrayList<Socket> listSocket = new ArrayList<Socket>();
    
    private ArrayList<User> users = new ArrayList<User>();
    private ArrayList<User> usersOnline = new ArrayList<User>();

    // Запуск прослушивания подключения новых игроков
    public Server()
    {
        new ServerHost(this).start();
    }

    public ArrayList<DataOutputStream> GetListOut() { return listOut; }
    public ArrayList<DataInputStream> GetListIn() { return listIn; }
    public ArrayList<Socket> GetListSocket() { return listSocket; }
    public ArrayList<User> GetUsersOnline() { return usersOnline; }
    public ArrayList<User> GetUsers() { return users; }
    
    public void AddUser(ArrayList<User> users, User user) {
        if (users.indexOf(user) != -1)
            return;
        
        users.add(user); 
    }
    
    // Нить прослушивания клиента
    public class ServerListener extends Thread {
        Socket socket;
        DataInputStream in;
        DataOutputStream out;

        public ServerListener(Socket socket) {
            super("ServerListener");
            this.socket = socket;
        }

        public void run() {
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                // Проверяем живой ли канал и работаем если живой
                while (!socket.isOutputShutdown()) {
                    // Проверка на приход от клиента данных
                    while (in.available() == 0)
                        Thread.sleep(1);

                    String data = in.readUTF();
                    ArrayList<String> arrStr = new ArrayList<String>(
                            Arrays.asList(data.split(" ")));
                    String name = arrStr.get(0);
                    arrStr.remove(0);
                    String message = String.join(" ", arrStr);    
                    boolean findUser = false;                    
                    
                    for (var user : usersOnline) {
                        if (user.GetName().equals(name)) {                        
                            listOut.get(user.GetId()).writeUTF(
                                    usersOnline.get(listSocket.indexOf(socket)).GetName() + " " + message);
                            listOut.get(user.GetId()).flush();
                            Thread.sleep(10);
                        }
                    }
                }
            }
            catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

}
