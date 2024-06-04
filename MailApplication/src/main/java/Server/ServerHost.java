/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;
/**
 *
 * @author Vasya
 */
import Client.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

public class ServerHost extends Thread{
    private Server srv;
    private Integer countUsers = 0;

    public ServerHost(Server srv)
    {
        super("ServerHost");
        this.srv = srv;
    }

    public void run()
    {
        // Запуск серверного сокета
        try (ServerSocket serverSocket = new ServerSocket(80))
        {
            System.out.println("Server to start...");

            // Проверка канала
            while (!serverSocket.isClosed())
            {
                // Ожидание подключения клиента
                srv.GetListSocket().add(serverSocket.accept());
                srv.GetListOut().add(new DataOutputStream(srv.GetListSocket().getLast().getOutputStream()));
                srv.GetListIn().add(new DataInputStream(srv.GetListSocket().getLast().getInputStream()));
                
                // Получение потока для чтения
                DataInputStream in = new DataInputStream(
                        srv.GetListSocket().getLast().getInputStream());
                
                // Считывание от клиента его имени и занесение его в список
                // подключенных пользователей
                String userInfo = in.readUTF();
                User user = new User(userInfo, countUsers);
                countUsers++;
                srv.AddUser(srv.GetUsersOnline(), user);
                
//                if (srv.GetUsers().indexOf(user) == -1)
//                    srv.AddUser(srv.GetUsers(), user);
                    
                
                // Отправка измененного списка, подключенным пользователям 
                srv.new ServerListener(srv.GetListSocket().getLast()).start();
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
