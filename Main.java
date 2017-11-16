package bambOS;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        IUserController userController = new UserController();
        // IACLController aclCtontroller = new ACLControler();
        IFileSystem fileSystem = new FileSystem();
        IInterpreter interpreter = new Interpreter();
        ProcesorInterface process=new Process();
        RAM memory=new RAM();
        //ProcesorInterface procesor = new ProcesorInterface() {



       // Shell shell = new Shell(userController,interpreter, fileSystem, memory, procesor);
       // shell.start();

    }
}
