package manager;


import exeption.FileExeption;
import exeption.LackOfPermissionExeption;
import exeption.MissingFileExeption;
import exeption.PathIsDirectoryExeption;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileManager {
    private ConsoleManager consoleManager;

    public FileManager(ConsoleManager consoleManager) {
        this.consoleManager = consoleManager;

    }

    //Reading script program from file and return script from file as list
    public String[] getFileScript() {
        StringBuilder fileData = new StringBuilder();
        String path = consoleManager.getPathScriptFile();

        File file = new File(path);
        FileInputStream fileInputStream;
        BufferedInputStream bufferedInputStream;
        try {

            if (file.isDirectory()) {
                throw new PathIsDirectoryExeption("The specified path is a directory");
            } else if (!file.isFile()) {
                throw new MissingFileExeption("No such file exists");
            } else if (!Files.isReadable(Paths.get(path))) {
                throw new LackOfPermissionExeption("Not enough permissions to read from the file");
            }

            fileInputStream = new FileInputStream(path);

            //reading script from a file in bloks of 200 bytes
            bufferedInputStream = new BufferedInputStream(fileInputStream, 200);
            int k;
            while ((k = bufferedInputStream.read()) != -1) {
                fileData.append((char) k);
            }


            //fill array with data from file
            System.out.println(fileData);
            String[] scriptFile;
            String delimeter = "\n";
            scriptFile = fileData.toString().replaceAll("\r", "").split(delimeter);


            return scriptFile;
        } catch (FileExeption | FileNotFoundException e) {
            System.err.println(e.getMessage());
            return getFileScript();


        } catch (IOException ex) {
            ex.printStackTrace();
            return getFileScript();
        }
    }
}
