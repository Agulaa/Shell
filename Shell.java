package bambOS;

import java.awt.event.KeyEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;


public class Shell {

    //private IProcessManager processManager;
    private ProcesorInterface procesor;
    private IFileSystem fileSystem;
    private IUserController userController;
    private IInterpreter interpreter;
    private ILoginService loginService;
    private RAM memory;
    //private IPCB PCB;
  //private IACLController ACLController;

    /**
     * Mapa ze wszytskimi komendami, ktore obsluguje Shell
     */
    private Map<String, String> allCommands;

    /**
     * Konstructor
     * @param userController
     * @param interpreter
     */
    public Shell(IUserController userController, IInterpreter interpreter, IFileSystem fileSystem, RAM memory, ProcesorInterface procesor) {
        this.userController = userController;
        this.interpreter=interpreter;
        this.fileSystem=fileSystem;
        this.loginService=loginService;
        this.memory=memory;
        this.procesor=procesor;
        //this.IACLController=ACLController;
        //this.
        allCommands = new HashMap<>();

       // loadShell();
    }

    /**
     * Metoda, ktora wypelnia mape komendami, wyswietla logo, oraz dopoki nie zostanie przerwana przez uzytkownika wykonuje metode readCommend()
     */
    public void start(){
        addAllCommands();
        logo();
        loginLoad();
        while (true){
            readCommend();
        }
    }

    /**
     * Metoda, ktora:
     * -czyta komende od uzytkownika z konsoli
     * -pobiera stringa, ktory dzieli na czesci przedzielone spacja
     * -w zaleznosci od piewsego czlonu komendy wywoluje inne metody
     */
    private void readCommend(){
        System.out.print(">");
        BufferedReader in= new BufferedReader(new InputStreamReader(System.in));
        try {
            String line=in.readLine();
            if(line.length()>0) {
                String[] separateCommand = line.split(" "); // podzielenie komendy na czesci odzielone spacja
                if (separateCommand.length > 0) {
                    if (isCommandGood(separateCommand[0])) {
                        switch (separateCommand[0]) {
                            case "help":
                                help();
                                break;
                            case "exit":
                                exit();
                                break;
                            case "about":
                                about();
                                break;
                            case "user":
                                user(separateCommand);
                                break;
                            case "group":
                                group(separateCommand);
                                break;
                            case "interpreter":
                                interpreter(separateCommand);
                                break;
                            case "cr":
                                create(separateCommand);
                                break;
                            case "append":
                                append(separateCommand);
                                break;
                            case "delete":
                                delete(separateCommand);
                                break;
                            case "rename":
                                rename(separateCommand);
                                break;
                            case "list":
                                list(separateCommand);
                                break;
                            case "open":
                                open(separateCommand);
                                break;
                            case "process":
                                process(separateCommand);
                                break;
                            case "memory":
                                memory(separateCommand);
                                break;
                            case "bcontrol":
                                bcontrol(separateCommand);
                                break;
                            case "go":
                                go(separateCommand);
                                break;

                        }

                    } else if (!isCommandGood(separateCommand[0])) {
                        System.out.println("Bledna komenda");
                        help();
                    }
                }
            }
            else {
                readCommend();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Metoda, ktora laduje wszytskie metody do mapy
     */

    private void addAllCommands(){

        allCommands.put("exit", "Koniec pracy systemu");
        allCommands.put("help", "Pomoc");
        allCommands.put("about", "Informacje o autorach systemu");
        allCommands.put("user", "Kontrola uzytkownikow");
        allCommands.put("group", "Kontrola grup");
        allCommands.put("interpreter", "Interpreter-czytanie z pliku");
        allCommands.put("cr", "Tworzenie wpisu");
        allCommands.put("append", "Dodanie danych na koniec pliku");
        allCommands.put("delete", "Usuwa plik o podanej nazwie");
        allCommands.put("rename", "Zmiana nazwy pliku");
        allCommands.put("list", "Wyswietla zawartosc katalogow");
        allCommands.put("process", "Dzaialnia dotyczace procesu");
        allCommands.put("memory", "Wyswietlenie pamieci");
        allCommands.put("bcontrol", "Blok kontrolny");
        allCommands.put("go", "Wykonanie jednego rozkau");


    }

    /**
     * Metoda, ktora zostaje wywolana gdy uzytkownik poda do konsoli komende 'interpreter [nazwe_pliku]'
     * Wywoluje ona metode interpretera i przekazuje jej nazwe pliku
     * @param command
     */
    private void interpreter(String[] command){
        if(command.length==2){
            interpreter.Exe(command[1]);
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }


    }

    /**
     * Metoda, ktora sprawdza, czy komenda podana przez uzytkownika, znajduje sie wsrod komend zapisanych w shellu
     * @param command
     * @return zwraca ture, gdy jest
     */

    private boolean isCommandGood(String command){
        if(allCommands.containsKey(command))return true;
        return false;
    }

    /**
     * Metoda, ktora zostaje wywolana, gdy uzytkownik poda komende 'help'
     * Wyswietla ona wszystkie komendy dostepne dla uzytkownika i ich opis
     */
    private void help(){
        System.out.println("            POMOC");
        System.out.println("Komenda:          Opis:");
        for(Map.Entry<String, String> command : allCommands.entrySet()){
            if(command.getKey().length()<=2){
                System.out.println(command.getKey()+"                "+command.getValue());
            }else if(command.getKey().length()>2&&command.getKey().length()<5) {
                System.out.println(command.getKey() + "              " + command.getValue());
            }else if(command.getKey().length()>7){
                System.out.println(command.getKey() + "       " + command.getValue());
            }
            else{
                System.out.println(command.getKey() + "             " + command.getValue());
            }
        }
    }

    /**
     *Metoda, ktora zostaje wywolana, gdy uzytkownik poda komende 'exit'
     * Asekuracyjnie sprawdza czu uzytkownik chce zakonczyc prace,
     *      jesli tak-> koniec pracy systemu
     *      jesli nie-> kontynuuje odczytywanie komendy
     *
     */
    private void exit(){
        System.out.println("Chcesz zakonczyc prace systemu ?[ TAK - T, NIE - N]");
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        try {
            String answer=in.readLine();
            if(answer.equals("t")|answer.equals("T")){
                System.exit(0);
            }else if(answer.equals("n")|answer.equals("N")){
                readCommend();
            }else {
                System.out.println("Bledna odpowiedz");
                exit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda, ktora zostaje wywolana, gdy uzytkownik poda komedne 'about'
     * Wyswietla ona informacje dotyczace autorow systemu operacyjnego
     */

    private void about(){
        System.out.println("Autorzy systemu: ");
        System.out.println("Klaudia Bartoszak        Zarzadzanie pamiecia");
        System.out.println("Kamila Urbaniak          Interpreter/Programy ");
        System.out.println("Agnieszka Rusin          Shell");
        System.out.println("Marcin Hilt              Zarzadzanie procesorem ");
        System.out.println("Jakub Smierzchalski      Komunikacja miedzyprocesorowa");
        System.out.println("Michał Sciborski         Konta/Grupy");
        System.out.println("Bartosz Wieckowski       Zarzadzanie procesami");
        System.out.println("Michal Wlodarczyk        Zarzadrzanie pliaki/katalogami");
        System.out.println("Jedrzej Wyzgala          Mechanizm synchronizacji");


    }

    /**
     * Metoda, ktora zostaje wywolana, gdy uzytkownik poda komedne 'user'
     * W zaleznosci od dalszych czlonow komendy :
     * user --add [nazwa_uzytkownika]
     * ->wywolywane jest dodawanie uzytkownika
     * user --add [nazwa_uzytkownika] --group [nazwa_grupy]
     * ->wywolywane jest dodawanie uzytkownika do konkretnej grupy
     * user --remove [nazwa_uzytkownika]
     * ->usuwanie uzytkownika
     * user --show
     * ->wyswtetlanie listy uzytkownikow
     * @param command - w zaleznosci od jego dlugosci, wykonywan sa rozne metody
     */

    private void user(String[] command){
        if(command.length>1) {
            if (command[1].equals("--add")) {
                //user --add [nazwa_uzytkownika] --group [nazwa_groupy]
                if (command.length == 5) {
                    try {
                        userController.addUser(command[2], command[4]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                }
                //user --add [nazwa_uzytkownika]
                if (command.length == 3) {
                    try {
                        userController.addUser(command[2], null);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                } else {
                    System.out.println("Bledna komenda");
                    readCommend();
                }
            }
            if (command[1].equals("--remove")) {
                //user --remove [nazwa_uzytkownika]
                if (command.length == 3) {
                    try {
                        userController.removeUser(command[2]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                } else {
                    System.out.println("Bledna komenda");
                    readCommend();
                }
            }
            if (command[1].equals("--show")) {
                //user --show
                if (command.length == 2) {
                    System.out.println(userController.showUserList());
                } else {
                    System.out.println("Bledna komenda");
                    readCommend();
                }
            }
        }
        else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }

    /**
     *Metoda, ktora zostaje wywolana, gdy uzytkownik poda komedne 'group'
     * w zaleznosci od dalczysz czlonow komendy:
     * group --add [nazwa_uzytkownika] [nazwa_grupy]
     * ->dodawany jest uzytkownik do konkretnej grupy
     * group --remove [nazwa_uzytkownika] [nazwa_grupy]
     * ->usuwany jest uzytkwonik z konkretnej grupy
     * group --remove [nazwa_uzytkownika]
     * ->usuwany jest uzytkownik ze wszystkich grup
     * @param command
     */
    private void group(String[] command){
        if(command.length>1) {
            // group --add [nazwa_uzytkownika] [nazwa_grupy]
            if (command[1].equals("--add")) {
                if(command.length==4) {
                    try {
                        userController.addUserToGroup(userController.findUser(command[2]), command[3]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                }else {
                    System.out.println("Bledna komenda");
                    readCommend();
                }

            } else if (command[1].equals("--remove")) {
                //group --remove [nazwa_uzytkownika]
                if (command.length == 3) {
                    try {
                        userController.removeUserFromAllGroups(userController.findUser(command[2]));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                }
                //group --remove [nazwa_uzytkownika] [nazwa_grupy]
                if (command.length == 4) {
                    try {
                        userController.removeUserFromGroup(userController.findUser(command[2]), command[3]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                }else {
                    System.out.println("Bledna komenda");
                    readCommend();
                }
            }
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }

    /**
     * Metoda, ktora zostaje wywolalan gdy uzytkownik poda komende 'cr [nazwa_pliku]'
     *
     * @param command
     */
    private void create(String[] command){
        if(command.length>1) {
            //create [nazwa_pliku]
            if (command.length == 2) {
                try {
                    fileSystem.createEmptyFile(command[1]);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    readCommend();
                }
            }
            //create [nazwa_pliku] [rozmiar] + pozniej tresc
            if (command.length == 3) {
                try {
                    ///

                    fileSystem.createFile(command[1], command[2]);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Bledna komenda");
                readCommend();
            }
        }else {
            System.out.println("Bledna komenda");
            readCommend();
        }
    }


    /**
     * Metoda, ktora zostaje wywolana gdy uzytkownik poda komende 'append [nazwa_pliku] [ZawartoscDoDodania]'
     * @param command
     */

    private void append(String[] command) {
        if (command.length > 1) {
            if (command.length == 2) {
                try {
                    fileSystem.appendFile(command[1], command[2]);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    readCommend();
                }

            } else {
                System.out.println("Bledna komenda");
                readCommend();
            }
        } else {
            System.out.println("Bledna komenda");
            readCommend();
        }
    }

    /**
     * Metoda,ktora zostaje wywolalan gdy uzytkownik poda komende 'delete [nazwa_pliku]'
     * @param command
     */

    private void delete(String[] command) {
        if (command.length > 1) {
            if (command.length == 2) {
                try {
                    fileSystem.deleteFile(command[1]);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    readCommend();
                }
            } else {
                System.out.println("Bledna komenda");
                readCommend();
            }
        }else {
            System.out.println("Bledna komenda");
            readCommend();
        }
    }

    /**
     * Metoda, ktora zostaje wywolana gdy uzytkownik poda komende 'rename [stara_nazwa_pliku] [nowa_nazwa_pliku]'
     * @param command
     */
    private void rename(String[] command) {
        if (command.length > 1) {
            if (command.length == 3) {
                try {
                    fileSystem.renameFile(command[1], command[2]);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    readCommend();
                }
            }
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }


    /**
     * Metoda, ktora zostaje wywoalana gdy uzytkownik poda komende 'list'
     * -> Metoda list zwraca w tablicy Stringow nazwy i rozmiar wpisow w katalogu domyslnym
     * @param command
     */
    private void list(String[] command){
        if(command.length==1){
            fileSystem.list();
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }

    }

    /**
     * Metoda, ktora zostanie wywolana gdy uzytkwonik poda komende 'open [nazwa_pliku]'
     *  Metoda readFile zwraca w Stringu zawartosc pliku zczytana z dysku
     *
     * @param command
     */
    private void open(String[] command){
        if(command.length==2){
            try {
                System.out.println(fileSystem.readFile(command[1]));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                readCommend();
            }
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }

    /**
     * Metda, ktora zostanie wywolana gdy uzytkownik poda komende ' process [...] '
     * -> process --del
     * @param command
     */
    private void process(String[] command){
        if(command.length>1){
            if(command.length==2){
                if(command[1].equals("--del")){
                    //zakonczenie pracy procesu
                }
                if(command[1].equals(("--list"))){
                    //wyswietla liste procesow
                }
            }
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }

    }

    /**
     * Metoda, ktora zostanie wywolana gdy użytkownik poda komende 'bcontrol'
     * Wyswietla blok kontrolny
     * @param command
     */

    private void bcontrol(String[] command){
        if(command.length==1){
            // wyswietlanie bloku kontrolengo
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }

    /**
     * Metdoa, ktora zostanie wywwoalan gdy uzytwkonik poda komende 'memory print'
     * @param command
     */
    private void memory(String[] command){
        if(command.length==2){
            if(command[1].equals("--print")){
                memory.printRAM();
            }
            else {
                System.out.println("Bledna komenda");
                readCommend();
            }
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }


    private void go(String[]command){
        if(command.length==1){
            procesor.wykonaj();
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }


    /**
     * Metoda ktora wywoluje sie dopoki uzytkownik nie poda prawidlowego loginu
     */
    private void loginLoad(){
        System.out.println("Podaj login:");
        BufferedReader in= new BufferedReader(new InputStreamReader(System.in));
        try {
            String line=in.readLine();
            if(line.equals("exit")){
                exit();
            }else {
                login(line);
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    /**
     * Metoda, ktora zostaje wywolana na poczatku uruchamiania sie shella
     * @param name
     */
    private void login(String name){
            try {
                loginService.LoginUser(name);
            } catch (Exception e) {
                System.out.println("Nie ma takiego uzytkownika");
                loginLoad();
            }
    }

    /**
     * Metoda ktora wyswietla logo z pliku txt oraz akutalna dane i czas
     */
    /*private void logo(){
        FileReader fileReader= null;
        try {
            fileReader = new FileReader("logo.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader=new BufferedReader(fileReader);
        try {
            String line = "";
            while((line = bufferedReader.readLine())!=null){
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
       // Data i czas
        LocalDate data=LocalDate.now();
        LocalTime time=LocalTime.now();
        System.out.println(data +" "+ time.withNano(0));
        System.out.println();


    }*/

    private void logo(){
        System.out.println("__/\\\\\\\\\\\\\\\\\\\\\\\\\\_______/\\\\\\\\\\\\\\\\\\_____/\\\\\\\\____________/\\\\\\\\__/\\\\\\\\\\\\\\\\\\\\\\\\\\_________/\\\\\\\\\\__________/\\\\\\\\\\\\\\\\\\\\\\___        \n" +
                " _\\/\\\\\\/////////\\\\\\___/\\\\\\\\\\\\\\\\\\\\\\\\\\__\\/\\\\\\\\\\\\________/\\\\\\\\\\\\_\\/\\\\\\/////////\\\\\\_____/\\\\\\///\\\\\\______/\\\\\\/////////\\\\\\_       \n" +
                "  _\\/\\\\\\_______\\/\\\\\\__/\\\\\\/////////\\\\\\_\\/\\\\\\//\\\\\\____/\\\\\\//\\\\\\_\\/\\\\\\_______\\/\\\\\\___/\\\\\\/__\\///\\\\\\___\\//\\\\\\______\\///__      \n" +
                "   _\\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\__\\/\\\\\\_______\\/\\\\\\_\\/\\\\\\\\///\\\\\\/\\\\\\/_\\/\\\\\\_\\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\___/\\\\\\______\\//\\\\\\___\\////\\\\\\_________     \n" +
                "    _\\/\\\\\\/////////\\\\\\_\\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\_\\/\\\\\\__\\///\\\\\\/___\\/\\\\\\_\\/\\\\\\/////////\\\\\\_\\/\\\\\\_______\\/\\\\\\______\\////\\\\\\______    \n" +
                "     _\\/\\\\\\_______\\/\\\\\\_\\/\\\\\\/////////\\\\\\_\\/\\\\\\____\\///_____\\/\\\\\\_\\/\\\\\\_______\\/\\\\\\_\\//\\\\\\______/\\\\\\__________\\////\\\\\\___   \n" +
                "      _\\/\\\\\\_______\\/\\\\\\_\\/\\\\\\_______\\/\\\\\\_\\/\\\\\\_____________\\/\\\\\\_\\/\\\\\\_______\\/\\\\\\__\\///\\\\\\__/\\\\\\_____/\\\\\\______\\//\\\\\\__  \n" +
                "       _\\/\\\\\\\\\\\\\\\\\\\\\\\\\\/__\\/\\\\\\_______\\/\\\\\\_\\/\\\\\\_____________\\/\\\\\\_\\/\\\\\\\\\\\\\\\\\\\\\\\\\\/_____\\///\\\\\\\\\\/_____\\///\\\\\\\\\\\\\\\\\\\\\\/___ \n" +
                "        _\\/////////////____\\///________\\///__\\///______________\\///__\\/////////////_________\\/////_________\\///////////_____\n" +
                "\n" +
                "                                   ~Bardzo Amatorski, Modulowy, ale Bezpieczny Operacyjny System~");
    }
}
