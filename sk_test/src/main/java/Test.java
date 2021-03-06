import java.io.IOException;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Class.forName("LokalniExporter");
        MyExporter myExporter = ExporterManager.getExporter("");
        Scanner scanner = new Scanner(System.in);


        String path = "";
        try {
            while(true) {

                System.out.println("Unesite putanju: ");
                path = scanner.nextLine();

                if (myExporter.initStorage(path).equals("new")) {

                    System.out.println("Uspesno!");

                    Scanner scanner1 = new Scanner(System.in);
                    System.out.println("Unesite username: ");
                    String username = scanner1.nextLine();
                    System.out.println("Unesite password: ");
                    String password = scanner1.nextLine();
                    String kategorija = "admin";
                    myExporter.createUser(username,password,kategorija,path);
                    Scanner scanner30 = new Scanner(System.in);

                    System.out.println("Unesite velicinu skladista: ");
                    Long velicina = Long.valueOf(scanner30.nextLine());

                    System.out.println("Unesite putanju foldera:");
                    String imeFoldera = scanner30.nextLine();

                    System.out.println("Unesite maksimalan broj fajlova:");
                    Integer brojFajlova = Integer.valueOf(scanner30.nextLine());

                    System.out.println("Unesite zabranjene ekstenzije fajlova:");
                    String zabrana = scanner30.nextLine();

                    myExporter.konfiguracioniFajl(path,velicina,imeFoldera,brojFajlova,zabrana);

                    if(myExporter.connect(username,password,path)){
                        break;
                    }
                    System.out.println("Neuspesno konektovan!");

                }
                if(myExporter.initStorage(path).equals("connect")){

                    System.out.println("Vec postoji!");
                    System.out.println("Ovo je skladiste");
                    Scanner scanner1 = new Scanner(System.in);
                    String username = "";
                    String password = "";
                    System.out.println("Unesite username: ");
                    username = scanner1.nextLine();
                    System.out.println("Unesite password: ");
                    password = scanner1.nextLine();
                    if(myExporter.connect(username,password,path)){
                        System.out.println("Uspesno konektovan!");
                        break;
                    }
                    System.out.println("Neuspesno konektovanje!");

                }
                if(myExporter.initStorage(path).equals("false")){
                    System.out.println("Neuspesno!");
                }
                if(myExporter.initStorage(path).equals("confalse")){
                    System.out.println("Vec postoji!");
                    System.out.println("Ovo ne moze da bude skladiste!");
                }




            }

            while(true){

                int p = -1;
                String currentUser = myExporter.meni();


                if(currentUser.equals("admin")){

                    System.out.println("(1) Save");
                    System.out.println("(2) Create folder");
                    System.out.println("(3) Create file");
                    System.out.println("(4) Delete folder");
                    System.out.println("(5) Delete file");
                    System.out.println("(6) Read");
                    System.out.println("(7) Move file");
                    System.out.println("(8) Download file");
                    System.out.println("(9) Create user");
                    System.out.println("(10) Disconnect");
                    System.out.println("(11) Last modified");
                    System.out.println("(12) Set storage size");
                    System.out.println("(13) Set restricted extensions");
                    System.out.println("(14) Set folder restriction");
                    Scanner scanner100 = new Scanner(System.in);
                    int br = scanner100.nextInt();
                    if(br>14 || br<1){
                        p=-1;
                    }
                    p=br;

                }
                if(currentUser.equals("korisnik")){
                    System.out.println("(1) Save");
                    System.out.println("(2) Create folder");
                    System.out.println("(3) Create file");
                    System.out.println("(4) Disconnect");
                    System.out.println("(5) Last modified");
                    System.out.println("(6) Read");
                    Scanner scanner101 = new Scanner(System.in);
                    int br = scanner101.nextInt();
                    if(br>6 || br<1){
                        p = -1;
                    }
                    p = br+20;

                }
                if(currentUser.equals("guest")){
                    System.out.println("(1) Read");
                    System.out.println("(2) Disconnect");
                    System.out.println("(3) Last modified");
                    Scanner scanner102 = new Scanner(System.in);
                    int br = scanner102.nextInt();
                    if(br>3 || br<1){
                        p = -1;
                    }
                    p = br+30;

                }




                switch(p){

                    case 1: case 21:

                        Scanner scanner11 = new Scanner(System.in);
                        System.out.println("Unesite putanju koji fajl zelite da sacuvate:");
                        String source11 = scanner11.nextLine();
                        System.out.println("Unesite putanju gde u skladistu zelite da sacuvate fajl:");
                        String destination11 = scanner11.nextLine();
                        myExporter.save(source11,destination11);

                        break;
                    case 2: case 22:
                        Scanner scanner2 = new Scanner(System.in);
                        System.out.println("Unesite putanju gde zelite da kreirate folder:");
                        String path2 = scanner2.nextLine();
                        myExporter.createFolder(path2);
                        break;
                    case 3: case 23:
                        Scanner scanner3 = new Scanner(System.in);
                        System.out.println("Unesite putanju gde zelite da kreirate fajl:");
                        String path3 = scanner3.nextLine();
                        myExporter.createFile(path3);
                        break;
                    case 4:
                        Scanner scanner4 = new Scanner(System.in);
                        System.out.println("Unesite koji folder zelite da obrisete:");
                        String path4 = scanner4.nextLine();
                        myExporter.deleteFolder(path4);
                        break;
                    case 5:
                        Scanner scanner5 = new Scanner(System.in);
                        System.out.println("Unesite koji file zelite da obrisete:");
                        String path5 = scanner5.nextLine();
                        myExporter.deleteFile(path5);
                        break;
                    case 6: case 31: case 26:
                        Scanner scanner6 = new Scanner(System.in);
                        System.out.println("Unesite koji folder zelite da izlistate:");
                        String path6 = scanner6.nextLine();
                        myExporter.read(path6);
                        break;
                    case 7:
                        Scanner scanner7 = new Scanner(System.in);
                        System.out.println("Unesite koji fajl zelite da premestite:");
                        String source2 = scanner7.nextLine();
                        System.out.println("Unesite gde zelite da premestite fajl:");
                        String destination2 = scanner7.nextLine();
                        myExporter.moveFile(source2,destination2);
                        break;
                    case 8:
                        Scanner scanner8 = new Scanner(System.in);
                        System.out.println("Unesite koji fajl zelite da skinete:");
                        String source = scanner8.nextLine();

                        if(myExporter.downloadFile(source)){
                            System.out.println("Uspesno skinut!");
                        }
                        else{
                            System.out.println("Neuspesno!");
                        }


                        break;
                    case 9:
                        Scanner scanner1 = new Scanner(System.in);
                        System.out.println("Unesite username: ");
                        String username = scanner1.nextLine();
                        System.out.println("Unesite password: ");
                        String password = scanner1.nextLine();
                        System.out.println("Unesite kategoriju: ");
                        String kategorija = scanner1.nextLine();
                        if(!kategorija.equals("korisnik") && !kategorija.equals("guest")){
                            kategorija = "guest";
                        }
                        myExporter.createUser(username,password,kategorija,path);
                        break;
                    case 10: case 24: case 32:
                        System.out.println(myExporter.disconnect());
                        System.exit(0);
                        break;
                    case 11: case 25: case 33:
                        System.out.println("Unesite koji folder zelite da fidite kad je poslednji put modifikovan: ");
                        Scanner scanner21 = new Scanner(System.in);
                        String path21 = scanner21.nextLine();
                        myExporter.lastModified(path21);
                        break;
                    case 12:

                        System.out.println("Unesite velicinu storage-a:");
                        Scanner scanner40 = new Scanner(System.in);
                        Long vel = Long.parseLong(scanner40.nextLine());
                        myExporter.setStorageSize(vel);

                        break;
                    case 13:

                        System.out.println("Unesite zabranjene extenzije:");
                        Scanner scanner50 = new Scanner(System.in);
                        String res = scanner50.nextLine();
                        myExporter.setRestrictedxtensions(res);

                        break;
                    case 14:

                        System.out.println("Unesite putanju foldera:");
                        Scanner scanner60 = new Scanner(System.in);
                        String fol = scanner60.nextLine();
                        System.out.println("Unesite broj foldera:");
                        Integer b = Integer.parseInt(scanner60.nextLine());
                        myExporter.setFolderRestriction(fol,b);

                        break;

                    default:
                        System.out.println("Lose ste uneli!");


                }
                if(p == 0){
                    break;
                }


            }


        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
