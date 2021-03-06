import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.*;
import java.util.*;

public class DriveImplementacija extends MyExporter{

    static {
        ExporterManager.registerExporter(new DriveImplementacija());
    }

    private Drive service;
    private static User currentUser;
    private JSONArray useri = new JSONArray();

    private DriveImplementacija() {
        try {
            service = DriveConnection.getService();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public List<File> proveriDecu(String folderId) throws IOException {
        List<File> deca;
        String s = "'" + folderId  + "' in parents and visibility = 'limited' and threshed = false";
        Drive.Files.List request = service.files().list().setQ(s);
        return (ArrayList<File>) request.execute().getFiles();
    }

    public String checkPath(List<String> putanja) throws IOException {
        String queryParent = "mimeType='application/vnd.google-apps.folder' and name='";
        String queryChildren = "' in parents and visibility = 'limited' and trashed = false";
        Drive.Files.List request = service.files().list().setQ(queryParent + putanja.get(0) + "'");
        List<File> folderi = (ArrayList<File>) request.execute().getFiles();
        String flag = "";
        if(putanja.size() == 1){
            for(File file: folderi){
                File fo = file;
                System.out.println(fo.getId());
                Drive.Files.List r = service.files().list().setQ("'" + fo.getId() + queryChildren);
                List<File> dec = (ArrayList<File>) r.execute().getFiles();
                System.out.println(dec);
                if(dec.size() == 0){
                    return  "pogresna putanja";
                }
                for(File f: dec){
                    if(f.getName().contains(".json")){
                        return   "skladiste";
                    }
                    if(!f.getName().contains(".json")){
                        flag = "pogresna putanja";
                    }
                }
            }
            return flag;
        }

        int flag2 = 0;
        File folder = null;
        for(File file: folderi){
            folder = file;
            for(int i = 1; i < putanja.size(); i++){
                Drive.Files.List re = service.files().list().setQ("'" + folder.getId() + queryChildren);
                List<File> deca = (ArrayList<File>) re.execute().getFiles();
                System.out.println(deca.size());
                for(File f: deca){
                    System.out.println(f.getName());
                    System.out.println("for");
                    if(f.getName().contains(".json") && i < putanja.size()-1){
                        System.out.println("ima json");
                        return "pogresna putanja";
                    }
                    if(f.getName().equals(putanja.get(i))){
                        folder = f;
                        flag2 = 1;
                        break;
                    }
                    if(f.getName().contains(".json") && i == putanja.size()-1){
                        return "skladiste";
                    }
                    if(!f.getName().contains(".json") && i == putanja.size()-1){
                        System.out.println("to hocu");
                        flag = "pogresna putanja";
                    }
                    flag2 = 0;
                }
            }
        }
        if(!flag.equals("")){
            return flag;
        }
        if(flag2 == 1){
            Drive.Files.List ree = service.files().list().setQ("'" + folder.getId() + queryChildren);
            List<File> de = (ArrayList<File>) ree.execute().getFiles();
            for(File fj: de){
                if(fj.getName().contains(".json")){
                    return "skladiste";
                }
            }
            return "pogresna putanja";
        }
        else
            flag = "nepostojeca putanja";
        return flag;
    }

    @Override
    public String initStorage(String path) throws IOException {
        List<String> putanja = Arrays.asList(path.split("/"));

        String cp = checkPath(putanja);

        if(cp.equals("skladiste")){
            return "connect";
        }
        if(cp.equals("pogresna putanja")){
            System.out.println("Pogresna putanja");
            return "confalse";
        }

        if(cp.equals("nepostojeca putanja")){
            String roditeljId = null;
            File fileMetadata = new File();
            fileMetadata.setName(putanja.get(0));
            fileMetadata.setMimeType("application/vnd.google-apps.folder");
            File file = service.files().create(fileMetadata)
                    .setFields("id, parents, mimeType")
                    .execute();
            roditeljId = file.getId();
            for(int i = 1; i < putanja.size(); i++){
                File f = new File();
                f.setName(putanja.get(i));
                f.setMimeType("application/vnd.google-apps.folder");
                f.setParents(Collections.singletonList(roditeljId));
                File fajl = service.files().create(f)
                        .setFields("id , parents, mimeType")
                        .execute();
                roditeljId = fajl.getId();
            }
            File fileMet = new File();
            fileMet.setName("user.json");
            fileMet.setParents(Collections.singletonList(roditeljId));
            File fil = service.files().create(fileMet)
                    .setFields("id, parents")
                    .execute();

            File fileMe = new File();
            fileMe.setName("konfig.json");
            fileMe.setParents(Collections.singletonList(roditeljId));
            File fi = service.files().create(fileMe)
                    .setFields("id, parents")
                    .execute();

        }
        return "new";
    }

    @Override
    public void save(String location, String destination) throws IOException {
        List<String> driveDestinacija = Arrays.asList(destination.split("/"));
        List<String> lokalDestinacija = Arrays.asList(destination.split("/"));

        String queryParent = "mimeType='application/vnd.google-apps.folder' and name='";
        String queryChildren = "' in parents and visibility = 'limited' and trashed = false";
        Drive.Files.List request = service.files().list().setQ(queryParent + driveDestinacija.get(0) + "'");
        List<File> folderi = (ArrayList<File>) request.execute().getFiles();
        File roditelj = null;
        for(File folder: folderi){
            roditelj = folder;
            for(int i = 1; i < driveDestinacija.size(); i++) {
                Drive.Files.List re = service.files().list().setQ("'" + roditelj.getId() + queryChildren);
                List<File> deca = (ArrayList<File>) re.execute().getFiles();
                System.out.println(deca.size());
                for (File f : deca) {
                    if(f.getName().equals(driveDestinacija.get(i))){
                        roditelj = f;
                        break;
                    }
                }
            }
        }


        File body = new File();
        body.setName(lokalDestinacija.get(lokalDestinacija.size()-1));
        //body.setMimeType("application/vnd.google-apps.file");
        body.setParents(Collections.singletonList(roditelj.getId()));

        FileContent mediaContent = null;
        java.io.File fileContent = new java.io.File(location);

        if(lokalDestinacija.get(lokalDestinacija.size()-1).contains(".txt")){
            mediaContent = new FileContent("plain/text", fileContent);
        }
        if(lokalDestinacija.get(lokalDestinacija.size()-1).contains(".pdf")){
            mediaContent = new FileContent("application/pdf", fileContent);
        }
        if(lokalDestinacija.get(lokalDestinacija.size()-1).contains(".jpg")){
            mediaContent = new FileContent("image/jpeg", fileContent);
        }
        if(lokalDestinacija.get(lokalDestinacija.size()-1).contains(".png")){
            mediaContent = new FileContent("image/png", fileContent);
        }
        if(lokalDestinacija.get(lokalDestinacija.size()-1).contains(".doc")){
            mediaContent = new FileContent("application/msword", fileContent);
        }
        if(lokalDestinacija.get(lokalDestinacija.size()-1).contains(".zip")){
            mediaContent = new FileContent("application/zip", fileContent);
        }
        if(lokalDestinacija.get(lokalDestinacija.size()-1).contains(".rar")){
            mediaContent = new FileContent("application/rar", fileContent);
        }
        try {
            //File file = service.files().insert(body).execute();
            File file = service.files().create(body, mediaContent)
                    .setFields("id, parents").execute();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void createFolder(String path) throws IOException {
        List<String> putanja = Arrays.asList(path.split("/"));
        String queryParent = "mimeType='application/vnd.google-apps.folder' and name='";
        String queryChildren = "' in parents and visibility = 'limited' and trashed = false";
        Drive.Files.List request = service.files().list().setQ(queryParent + putanja.get(0) + "'");
        List<File> folderi = (ArrayList<File>) request.execute().getFiles();
        File roditelj = null;
        for(File folder: folderi){
            roditelj = folder;
            for(int i = 1; i < putanja.size(); i++) {
                Drive.Files.List re = service.files().list().setQ("'" + roditelj.getId() + queryChildren);
                List<File> deca = (ArrayList<File>) re.execute().getFiles();
                System.out.println(deca.size());
                for (File f : deca) {
                    if(f.getName().equals(putanja.get(i))){
                        roditelj = f;
                        break;
                    }
                }
            }
        }

        File fileMetadata = new File();
        fileMetadata.setName(putanja.get(putanja.size()-1));
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Collections.singletonList(roditelj.getId()));
        File file = service.files().create(fileMetadata)
                .setFields("id , parents")
                .execute();
        System.out.println("Folder ID: " + file.getId());
    }

    @Override
    public void createFile(String path) throws IOException {
        List<String> putanja = Arrays.asList(path.split("/"));
        String queryParent = "mimeType='application/vnd.google-apps.folder' and name='";
        String queryChildren = "' in parents and visibility = 'limited' and trashed = false";
        Drive.Files.List request = service.files().list().setQ(queryParent + putanja.get(0) + "'");
        List<File> folderi = (ArrayList<File>) request.execute().getFiles();
        File roditelj = null;
        for(File folder: folderi){
            roditelj = folder;
            for(int i = 1; i < putanja.size(); i++) {
                Drive.Files.List re = service.files().list().setQ("'" + roditelj.getId() + queryChildren);
                List<File> deca = (ArrayList<File>) re.execute().getFiles();
                System.out.println(deca.size());
                for (File f : deca) {
                    if(f.getName().equals(putanja.get(i))){
                        roditelj = f;
                        break;
                    }
                }
            }
        }

        File fileMetadata = new File();
        fileMetadata.setName(putanja.get(putanja.size()-1));
        fileMetadata.setParents(Collections.singletonList(roditelj.getId()));
        File file = service.files().create(fileMetadata)
                .setFields("id, parents")
                .execute();
    }

    @Override
    public void deleteFolder(String path) throws IOException {
        List<String> putanja = Arrays.asList(path.split("/"));
        String queryParent = "mimeType='application/vnd.google-apps.folder' and name='";
        String queryChildren = "' in parents and visibility = 'limited' and trashed = false";
        Drive.Files.List request = service.files().list().setQ(queryParent + putanja.get(0) + "'");
        List<File> folderi = (ArrayList<File>) request.execute().getFiles();
        File roditelj = null;
        for(File folder: folderi){
            roditelj = folder;
            for(int i = 1; i < putanja.size(); i++) {
                Drive.Files.List re = service.files().list().setQ("'" + roditelj.getId() + queryChildren);
                List<File> deca = (ArrayList<File>) re.execute().getFiles();
                System.out.println(deca.size());
                for (File f : deca) {
                    if(f.getName().equals(putanja.get(i))){
                        roditelj = f;
                        break;
                    }
                }
            }
        }
        try {
            service.files().delete(roditelj.getId()).execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void deleteFile(String path) throws IOException {
        List<String> putanja = Arrays.asList(path.split("/"));
        String queryParent = "mimeType='application/vnd.google-apps.folder' and name='";
        String queryChildren = "' in parents and visibility = 'limited' and trashed = false";
        Drive.Files.List request = service.files().list().setQ(queryParent + putanja.get(0) + "'");
        List<File> folderi = (ArrayList<File>) request.execute().getFiles();
        File roditelj = null;
        for(File folder: folderi){
            roditelj = folder;
            for(int i = 1; i < putanja.size(); i++) {
                Drive.Files.List re = service.files().list().setQ("'" + roditelj.getId() + queryChildren);
                List<File> deca = (ArrayList<File>) re.execute().getFiles();
                System.out.println(deca.size());
                for (File f : deca) {
                    if(f.getName().equals(putanja.get(i))){
                        roditelj = f;
                        break;
                    }
                }
            }
        }


        try {
            service.files().delete(roditelj.getId()).execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void read(String path) throws IOException {
        List<String> putanja = Arrays.asList(path.split("/"));
        String queryParent = "mimeType='application/vnd.google-apps.folder' and name='";
        String queryChildren = "' in parents and visibility = 'limited' and trashed = false";
        Drive.Files.List request = service.files().list().setQ(queryParent + putanja.get(0) + "'");
        List<File> folderi = (ArrayList<File>) request.execute().getFiles();
        File roditelj = null;
        for(File folder: folderi){
            roditelj = folder;
            Drive.Files.List re = service.files().list().setQ("'" + roditelj.getId() + queryChildren);
            List<File> deca = (ArrayList<File>) re.execute().getFiles();
            System.out.println(deca);
        }
    }


    @Override
    public void moveFile(String fajl, String folder) throws IOException {
        List<String> putanja = Arrays.asList(fajl.split("/"));
        String queryParent = "mimeType='application/vnd.google-apps.folder' and name='";
        String queryChildren = "' in parents and visibility = 'limited' and trashed = false";
        Drive.Files.List request = service.files().list().setQ(queryParent + putanja.get(0) + "'");
        List<File> folderi = (ArrayList<File>) request.execute().getFiles();
        File roditelj = null;
        File ff = null;
        for(File fold: folderi){
            ff = fold;
            for(int i = 1; i < putanja.size(); i++) {
                Drive.Files.List re = service.files().list().setQ("'" + ff.getId() + queryChildren);
                List<File> deca = (ArrayList<File>) re.execute().getFiles();
                System.out.println(deca.size());
                for (File f : deca) {
                    if(f.getName().equals(putanja.get(i))){
                        ff = f;
                        break;
                    }
                }
            }
        }

        for(File fold: folderi){
            roditelj = fold;
            for(int i = 1; i < putanja.size()-1; i++) {
                Drive.Files.List re = service.files().list().setQ("'" + roditelj.getId() + queryChildren);
                List<File> deca = (ArrayList<File>) re.execute().getFiles();
                System.out.println(deca.size());
                for (File f : deca) {
                    if(f.getName().equals(putanja.get(i))){
                        roditelj = f;
                        break;
                    }
                }
            }
        }

        List<String> putanja2 = Arrays.asList(fajl.split("/"));
        Drive.Files.List req = service.files().list().setQ(queryParent + putanja2.get(0) + "'");
        List<File> folders = (ArrayList<File>) req.execute().getFiles();
        File desFolder = null;
        for(File fold: folders){
            desFolder = fold;
            for(int i = 1; i < putanja.size(); i++) {
                Drive.Files.List re = service.files().list().setQ("'" + desFolder.getId() + queryChildren);
                List<File> deca = (ArrayList<File>) re.execute().getFiles();
                System.out.println(deca.size());
                for (File f : deca) {
                    if(f.getName().equals(putanja.get(i))){
                        desFolder = f;
                        break;
                    }
                }
            }
        }

        File file = service.files().get(ff.getId())
                .setFields("id, parents")
                .execute();


        file = service.files().update(ff.getId(), null)
                .setAddParents(desFolder.getId())
                .setRemoveParents(roditelj.getId())
                .setFields("id, parents")
                .execute();
    }

    @Override
    public void lastModified(String path) throws IOException {

    }

    @Override
    public boolean downloadFile(String path) throws IOException {
        List<String> putanja = Arrays.asList(path.split("/"));
        String queryParent = "mimeType='application/vnd.google-apps.folder' and name='";
        String queryChildren = "' in parents and visibility = 'limited' and trashed = false";
        Drive.Files.List request = service.files().list().setQ(queryParent + putanja.get(0) + "'");
        List<File> folderi = (ArrayList<File>) request.execute().getFiles();
        File roditelj = null;
        for(File folder: folderi){
            roditelj = folder;
            for(int i = 1; i < putanja.size(); i++) {
                Drive.Files.List re = service.files().list().setQ("'" + roditelj.getId() + queryChildren);
                List<File> deca = (ArrayList<File>) re.execute().getFiles();
                System.out.println(deca.size());
                for (File f : deca) {
                    if(f.getName().equals(putanja.get(i))){
                        roditelj = f;
                        break;
                    }
                }
            }
        }

        //OutputStream outputStream = new ByteArrayOutputStream();
        OutputStream outputstream = new FileOutputStream("C:\\Users\\Nikola\\Downloads" + putanja.get(putanja.size()-1));
        try {
            service.files().get(roditelj.getId())
                    .executeMediaAndDownloadTo(outputstream);
        } catch (IOException e) {
            return false;
        }
        outputstream.flush();
        outputstream.close();
        return true;
    }

    @Override
    public boolean connect(String username, String password, String path) throws IOException {
        List<User> users = ucitajJson(path);
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

        User user = new User(username,pasword,kategorija);
        sacuvajJson(path,user);


    }

    @Override
    public List<User> ucitajJson(String path) throws IOException {
        List<String> putanja = Arrays.asList(path.split("/"));
        java.io.File file = new java.io.File("C:\\Users\\Nikola\\Documents\\Softverske komponente\\Projekat1\\sk_driveImplementacija\\src\\main\\resources\\" + putanja.get(putanja.size()-1) + "_users.json");
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

        List<String> putanja = Arrays.asList(path.split("/"));
        java.io.File file = new java.io.File("C:\\Users\\Nikola\\Documents\\Softverske komponente\\Projekat1\\sk_driveImplementacija\\src\\main\\resources\\" + putanja.get(putanja.size()-1) + "_users.json");
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

        System.out.println(file.getAbsolutePath());

        object.put("username" , user.getUsername());
        object.put("password" , user.getPasword());
        object.put("kategorija", user.getKategorija());

        useri.add(object);

        FileWriter fw = new FileWriter(file,false);
        fw.write(useri.toJSONString());
        fw.close();

    }

    @Override
    public void konfiguracioniFajl(String path, Long velicina, String imeFoldera, Integer brojFajlova, String zabrana) {

    }

    @Override
    public void setStorageSize(Long velicina) {

    }

    @Override
    public void setRestrictedxtensions(String restrictions) {

    }

    @Override
    public void setFolderRestriction(String folderName, Integer broj) {

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

}
