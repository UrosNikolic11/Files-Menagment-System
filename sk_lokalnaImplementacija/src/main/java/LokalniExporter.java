import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.function.Consumer;

public class LokalniExporter extends MyExporter{

    static {
        ExporterManager.registerExporter(new LokalniExporter());
    }

    public static User currentUser;
    public JSONArray useri = new JSONArray();
    public String nazivSkladista = new String();
    public Long velicinaSkladista = new Long(0);
    public Long unetaVelicina;
    public String pathKonfiguracioni;
    public List<String> zabrane = new ArrayList<>();
    public String zabraneJson;
    JSONArray imeFoldera1 = new JSONArray();
    JSONArray brFajlova = new JSONArray();


    private LokalniExporter() {

    }

    @Override
    public String initStorage(String path) throws IOException {

        File dir = new File(path);
        if(!dir.exists()){

            if(dir.mkdirs()){

                String paths[] = path.split("\\\\");
                nazivSkladista = paths[paths.length-1];
                velicinaSkladista = FileUtils.sizeOfDirectory(dir);
                return "new";

            }else {
                return "false";
            }



        }else{

            File[] files = dir.listFiles();
            boolean flag = false;
            for(File file : files){
                if(file.getName().contains("json")){
                    flag = true;
                    break;
                }
            }
            if(flag == false){
                return "confalse";
            }else{

                String paths[] = path.split("\\\\");
                nazivSkladista = paths[paths.length-1];
                velicinaSkladista = FileUtils.sizeOfDirectory(dir);

                pathKonfiguracioni = path + "\\konfiguracija.json";

                File file = new File(path + "\\konfiguracija.json");
                JSONObject object = new JSONObject();
                JSONParser jsonP = new JSONParser();



                try(FileReader reader = new FileReader(file)) {
                    Object obj = jsonP.parse(reader);
                    object = (JSONObject) obj;

                    unetaVelicina = (Long) object.get("velicina");
                    String zaSplit = (String) object.get("zabrana");
                    String[] split = zaSplit.split(",");
                    zabraneJson = (String) object.get("zabrana");
                    for(int i=0; i< split.length; i++){

                        zabrane.add(split[i]);

                    }
                    imeFoldera1 = (JSONArray) object.get("imeFoldera");
                    brFajlova = (JSONArray) object.get("brFajlova");


                } catch (ParseException | FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return "connect";

            }



        }
    }



    @Override
    public void save(String sourceFilePath, String destinationFilePath) {

        File sourceFile = new File(sourceFilePath);
        File destinationFile = new File(destinationFilePath);
        if(!destinationFilePath.contains(nazivSkladista)){
            System.out.println("Niste izabrali skladiste!");
            return;
        }

        try {

            boolean flag2 = true;
            readKonfiguracioni(pathKonfiguracioni);

            String ime = "";
            String[] zaSplit = destinationFilePath.split("\\\\");
            for(int j=0 ; j<zaSplit.length-1 ; j++){

                ime = ime + "\\" + zaSplit[j];

            }

            File ime1 = new File(ime);

            for(int i=0; i<imeFoldera1.size();i++){

                Long b = (Long) brFajlova.get(i);

                if(ime1.getAbsolutePath().equals((String) imeFoldera1.get(i))){
                    if(b < ime1.listFiles().length + 1)
                    {
                        flag2 = false;
                    }

                }

            }

            if(unetaVelicina >= velicinaSkladista){
                if(flag2){
                    Files.copy(sourceFile.toPath(),destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Uspesno sacuvan!");
                }else{
                    System.out.println("Premasili ste ogranicen broj fajlova!");
                }
            }else{
                System.out.println("Nema mesta na skladistu!");
            }





        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void createFolder(String path) {

        File dir = new File(path);
        if(!dir.exists()){
            try{

                boolean flag2 = true;
                readKonfiguracioni(pathKonfiguracioni);


                String ime = "";
                String[] zaSplit = path.split("\\\\");
                for(int j=0 ; j<zaSplit.length-1 ; j++){

                    ime = ime + "\\" + zaSplit[j];

                }

                File ime1 = new File(ime);

                for(int i=0; i<imeFoldera1.size();i++){

                    Long b = (Long) brFajlova.get(i);

                    if(ime1.getAbsolutePath().equals((String) imeFoldera1.get(i))){
                        if(b < ime1.listFiles().length + 1)
                        {
                            flag2 = false;
                        }

                    }

                }

                if(unetaVelicina >= velicinaSkladista){

                    if(flag2){
                        if(dir.mkdirs()){
                            System.out.println("Uspesno");
                        }else {
                            System.out.println("Neuspesno");
                        }
                    }else{
                        System.out.println("Prekoracili ste broj fajlova");
                    }

                }else{
                    System.out.println("Nema mesta na skladistu!");
                }



            }catch (Exception e) {
                System.out.println("Pogresna putanja!");
            }

        }else{
            System.out.println("Folder vec postoji");
        }

    }



    @Override
    public void createFile(String path) {


        if(path.contains(nazivSkladista)){
            File file = new File(path);
            if(!file.exists()){
                try {
                    boolean flag = true;
                    boolean flag2 = true;
                    readKonfiguracioni(pathKonfiguracioni);
                    String ext1 = FilenameUtils.getExtension(path);
                    for(String ext: zabrane){


                        if(ext.equals(ext1)){
                            flag = false;
                        }

                    }
                    String ime = "";
                    String[] zaSplit = path.split("\\\\");
                    for(int j=0 ; j<zaSplit.length-1 ; j++){

                         ime = ime + "\\" + zaSplit[j];

                    }

                    File ime1 = new File(ime);

                    for(int i=0; i<imeFoldera1.size();i++){

                        Long b = (Long) brFajlova.get(i);

                        if(ime1.getAbsolutePath().equals((String) imeFoldera1.get(i))){
                            if(b < ime1.listFiles().length + 1)
                            {
                                flag2 = false;
                            }

                        }

                    }

                    if(unetaVelicina >= velicinaSkladista){
                        if(flag){
                            if(flag2){
                                if(file.createNewFile()){

                                    System.out.println("Uspesno kreiran file");
                                }
                            }else{
                                System.out.println("Prekoracili ste broj fajlova");
                            }

                        }else{
                            System.out.println("Uneli ste zabranjenu ekstenziju");
                        }

                    }else{
                        System.out.println("Nema mesta na skladistu!");
                    }
                } catch (IOException e) {
                    System.out.println("Pogresna putanja!");
                }
            }else{
                System.out.println("Fajl vec postoji !");
            }
        }else{
            System.out.println("Izabrali ste putanju izvan skladista!");
        }

    }


    @Override
    public void deleteFolder(String path) {

        File file = new File(path);
        deleteFolder2(file);

    }

    private void deleteFolder2(File file){

        if(file.isDirectory()){

            if(file.list().length == 0){

                for(int i=0; i<imeFoldera1.size(); i++){

                    if(file.getAbsolutePath().equals(imeFoldera1.get(i))) {
                        imeFoldera1.remove(i);
                        brFajlova.remove(i);
                    }

                }


                File file2 = new File(pathKonfiguracioni);


                JSONObject object = new JSONObject();
                object.put("velicina",unetaVelicina);
                object.put("zabrana",zabraneJson);
                object.put("imeFoldera", imeFoldera1);
                object.put("brFajlova", brFajlova);

                try {
                    FileWriter writer = new FileWriter(file2,false);
                    writer.write(object.toJSONString());
                    writer.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }


                file.delete();
                System.out.println("Directory is deleted : " + file.getAbsolutePath());
            }else{

                File files[] = file.listFiles();

                for(File fileDelete:files){
                    deleteFolder2(fileDelete);
                }

                if(file.list().length == 0){

                    for(int i=0; i<imeFoldera1.size(); i++){

                        if(file.getAbsolutePath().equals(imeFoldera1.get(i))) {
                            imeFoldera1.remove(i);
                            brFajlova.remove(i);
                        }

                    }


                    File file2 = new File(pathKonfiguracioni);


                    JSONObject object = new JSONObject();
                    object.put("velicina",unetaVelicina);
                    object.put("zabrana",zabraneJson);
                    object.put("imeFoldera", imeFoldera1);
                    object.put("brFajlova", brFajlova);

                    try {
                        FileWriter writer = new FileWriter(file2,false);
                        writer.write(object.toJSONString());
                        writer.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    file.delete();
                    System.out.println("Directory is deleted : " + file.getAbsolutePath());
                }

            }


        }else{
            file.delete();
            System.out.println("File is deleted : " + file.getAbsolutePath());
        }


    }

    @Override
    public void deleteFile(String path) {

        File file = new File(path);
        if(file.delete()){
            System.out.println("Uspesno");
        }else{
            System.out.println("Neuspesno");
        }


    }

    @Override
    public void read(String path) {

        File folder = new File(path);
        File[] files = folder.listFiles();


        for(File file:files){

            if(file.isFile()){
                System.out.println("File -> " + file.getName());
            }else{
                if(file.isDirectory()){
                    System.out.println("Folder-> "+ file.getName());
                }
            }

        }


    }


    @Override
    public void moveFile(String sourceFilePath , String destinationFilePath) {

        File sourceFile = new File(sourceFilePath);
        File destinationFile = new File(destinationFilePath);
        if(!sourceFilePath.contains(nazivSkladista) || !destinationFilePath.contains(nazivSkladista)){
            System.out.println("Morate izabrati fajlove unutar skladista!");
            return;
        }
        try {

            boolean flag2 = true;
            String ime = "";
            String[] zaSplit = destinationFilePath.split("\\\\");
            for(int j=0 ; j<zaSplit.length-1 ; j++){

                ime = ime + "\\" + zaSplit[j];

            }

            File ime1 = new File(ime);

            for(int i=0; i<imeFoldera1.size();i++){

                Long b = (Long) brFajlova.get(i);

                if(ime1.getAbsolutePath().equals((String) imeFoldera1.get(i))){
                    if(b < ime1.listFiles().length + 1)
                    {
                        flag2 = false;
                    }

                }

            }
            if(flag2){

                Files.copy(sourceFile.toPath(),destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                if(sourceFile.isDirectory()){
                    deleteFolder(sourceFilePath);
                }else{
                    deleteFile(sourceFilePath);
                }
                System.out.println("Uspesno kopiran!");

            }else{
                System.out.println("Premasili ste ogranicen broj fajlova!");
            }




        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void lastModified(String path) throws IOException {

        Path p = Paths.get(path);
        BasicFileAttributes attr = Files.readAttributes(p, BasicFileAttributes.class);
        FileTime a = attr.lastModifiedTime();
        System.out.println("Last modified: " + a);
        FileTime b = attr.creationTime();
        System.out.println("Created: " + b);

    }

    @Override
    public boolean downloadFile(String sourceFilePath) {

        File sourceFile = new File(sourceFilePath);
        File destinationFile = new File("C:\\Users\\Nikola\\Downloads");
        try {
            Files.copy(sourceFile.toPath(),destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean connect(String username,String password, String path) throws IOException {

        File ucitaj = new File(path + "\\users.json");
        List<User> users = ucitajJson(ucitaj.getAbsolutePath());
        for(User user : users){

            if(user.getUsername().equals(username) && user.getPasword().equals(password)){
                currentUser = user;
                return true;
            }

        }
        return false;

    }

    @Override
    public String disconnect() {


        return currentUser.getUsername() + " je diskonektovan!";


    }

    @Override
    public void createUser(String username, String pasword, String kategorija, String path) throws IOException {

        File file = new File(path + "\\users.json");
        User user = new User(username,pasword,kategorija);
        sacuvajJson(file.getAbsolutePath(),user);


    }

    @Override
    public List<User> ucitajJson(String path) throws IOException {


        File file = new File(path);
        JSONParser jsonP = new JSONParser();
        List<User> users = new ArrayList<>();

        try( FileReader reader = new FileReader(file)) {
            Object obj = jsonP.parse(reader);
            JSONArray empList = (JSONArray) obj;
            for(int i=0 ; i<empList.size(); i++){

                JSONObject jo = (JSONObject) empList.get(i);
                String username = (String) jo.get("username");
                String password = (String) jo.get("password");
                String kategorija = (String) jo.get("kategorija");
                User user = new User(username,password,kategorija);
                users.add(user);

            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return users;

    }

    @Override
    public void sacuvajJson(String path, User user) throws IOException {


        File file = new File(path);
        JSONObject object = new JSONObject();
        JSONParser jsonP = new JSONParser();
        file.createNewFile();
        FileReader reader = new FileReader(file);

        if(!user.getKategorija().equals("admin") && !user.getKategorija().equals("korisnik") && !user.getKategorija().equals("guest")){
            user.setKategorija("guest");
        }

        try {
            Object obj = jsonP.parse(reader);
            useri = (JSONArray) obj;


        } catch (ParseException e) {

        }


        object.put("username" , user.getUsername());
        object.put("password" , user.getPasword());
        object.put("kategorija", user.getKategorija());

        useri.add(object);

        FileWriter fw = new FileWriter(file,false);
        fw.write(useri.toJSONString());
        fw.close();


    }

    @Override
    public void konfiguracioniFajl(String path, Long velicina,String imeFoldera, Integer brojFajlova , String zabrana) {

        File file = new File(path + "\\konfiguracija.json");
        pathKonfiguracioni = path + "\\konfiguracija.json";

        unetaVelicina = velicina;
        zabraneJson = zabrana;
        imeFoldera1.add(imeFoldera);
        brFajlova.add(brojFajlova);

        JSONObject object = new JSONObject();
        object.put("velicina",unetaVelicina);
        object.put("zabrana",zabraneJson);
        object.put("imeFoldera", imeFoldera1);
        object.put("brFajlova", brFajlova);

        try {
            FileWriter writer = new FileWriter(file,false);
            writer.write(object.toJSONString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setStorageSize(Long velicina) {

        File file = new File(pathKonfiguracioni);

        unetaVelicina = velicina;

        JSONObject object = new JSONObject();
        object.put("velicina",unetaVelicina);
        object.put("zabrana",zabraneJson);
        object.put("imeFoldera", imeFoldera1);
        object.put("brFajlova", brFajlova);

        try {
            FileWriter writer = new FileWriter(file,false);
            writer.write(object.toJSONString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setRestrictedxtensions(String restrictions) {

        File file = new File(pathKonfiguracioni);

        zabraneJson = restrictions;

        JSONObject object = new JSONObject();
        object.put("velicina",unetaVelicina);
        object.put("zabrana",zabraneJson);
        object.put("imeFoldera", imeFoldera1);
        object.put("brFajlova", brFajlova);

        try {
            FileWriter writer = new FileWriter(file,false);
            writer.write(object.toJSONString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setFolderRestriction(String folderName,Integer broj) {

        File file = new File(pathKonfiguracioni);

        imeFoldera1.add(folderName);
        brFajlova.add(broj);

        JSONObject object = new JSONObject();
        object.put("velicina",unetaVelicina);
        object.put("zabrana",zabraneJson);
        object.put("imeFoldera", imeFoldera1);
        object.put("brFajlova", brFajlova);

        try {
            FileWriter writer = new FileWriter(file,false);
            writer.write(object.toJSONString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void readKonfiguracioni(String path) {

        File file = new File(pathKonfiguracioni);
        JSONObject object = new JSONObject();
        JSONParser jsonP = new JSONParser();



        try(FileReader reader = new FileReader(file)) {
            Object obj = jsonP.parse(reader);
            object = (JSONObject) obj;
            unetaVelicina = (Long) object.get("velicina");
            String zaSplit = (String) object.get("zabrana");
            String[] split = zaSplit.split(",");
            for(int i=0; i< split.length; i++){

                zabrane.add(split[i]);

            }
            imeFoldera1 = (JSONArray) object.get("imeFoldera");
            brFajlova = (JSONArray) object.get("brFajlova");


        } catch (ParseException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String meni() {

        if(currentUser.getKategorija().equals("admin")){

            return "admin";

        }
        if(currentUser.getKategorija().equals("korisnik")){

            return "korisnik";

        }
        if(currentUser.getKategorija().equals("guest")) {

            return "guest";

        }

        return "guest";
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        LokalniExporter.currentUser = currentUser;
    }
}