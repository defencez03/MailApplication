package Client;

import UI.ClientForm;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client {
    private Socket socket;
    private DataOutputStream oos;
    private DataInputStream ois;
    private ClientForm form;
    private String name;

    public Client(String name) {
        this.name = name;
        
        try {
            socket = new Socket("localhost", 80);
            ois = new DataInputStream(socket.getInputStream());
            oos = new DataOutputStream(socket.getOutputStream());

            if (!socket.isOutputShutdown()) {
                oos.writeUTF(name);
            }
            
            new Runnable() {
                public void run() {
                    try {
                        form = new ClientForm(socket);
                        form.setVisible(true);
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.run();
            
            new ClientListener().start();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public ArrayList<String> LoadClients(String nameFile) throws IOException {
        ArrayList<String> contacts = new ArrayList<String>();
        
        // Открыл файл для чтения
        try (FileReader file = new FileReader(nameFile))
        {
            BufferedReader br = new BufferedReader(file);
            String line;
            
            // Записал данные из файла
            while ((line = br.readLine()) != null)
            {
                contacts.add(line);
            }
        }
        return contacts;
    }

    // Нить прослушивания
    private class ClientListener extends Thread {

        public ClientListener() {
            super("ClientListener");
        }

        public void run() {
            try {
                // Проверяем живой ли канал и работаем если живой
                while (!socket.isOutputShutdown()) {
                    // Проверка на приход от сервера данных
                    while (ois.available() == 0)
                        Thread.sleep(1);

                    String data = ois.readUTF();
                    ArrayList<String> arrStr = new ArrayList<String>(
                            Arrays.asList(data.split(" ")));
                    
                    // Добавление сообщения в таблицу
                    String address = arrStr.get(0);
                    arrStr.remove(0);
                    String message = String.join(" ", arrStr);
                    form.InsertRow(address, message);           
                }

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
