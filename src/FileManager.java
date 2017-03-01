import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileManager {

  int pageNumber = 0;
  ArrayList<String> listOfFiles = new ArrayList<String>();
  List<String> listOfDirectories = new ArrayList<String>();
  String superPath = Credentials.PATH_SUPER;
  String cachePathFile = Credentials.PATH_CACHE;

  public ArrayList<String> getRootFolder() {
    File file = new File(superPath);
    File files[] = file.listFiles();

    for (int i = 0; i < files.length; i++) {
      if(files[i].isDirectory()) {
        listOfDirectories.add(files[i].getName());
      }
      if(files[i].isFile()) {
        listOfFiles.add(files[i].getName());
      } else {
//        System.out.println("error" + i);
      }

    }
    return listOfFiles;
  }

  public String getFirstImagePath() {
    String firstFilePath = getRootFolder().get(0);
    return firstFilePath;
  }

  public void removeFirstImage() {
    listOfFiles.remove(0);
  }


  public File loadImage(String filePath) throws IOException {
    String fullPath = (superPath+"\\"+filePath);
    File source = new File(fullPath);
    File dest = new File(cachePathFile);
    copyFile(source, dest);
    BufferedImage srcImage = ImageIO.read(dest); // Load image
    BufferedImage scaledImage = Scalr.resize(srcImage, 600); // Scale image
    ImageIO.write(scaledImage, "PNG", dest);
    return dest;
  }

  public void copyFile(File source, File end) {
    try {
      FileUtils.copyFile(source, end);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void moveFile(String sourcePath, String endPath) {
    File source = new File(sourcePath);
    File end = new File(endPath);
    try {
      FileUtils.moveFile(source, end);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void setListOfDirectories() {
    File file = new File(superPath);
    File files[] = file.listFiles();
    for (int i = 0; i < files.length; i++) {
      if(files[i].isDirectory()) {
        listOfDirectories.add(files[i].getName().toString());
      }
    }
  }


  public List<String> getListOfDirectories() {

    List<String> listOfDirectoriesCache = new ArrayList<String>();
    for (int i = 0; i < 29; i++) {
      listOfDirectoriesCache.add(getPageNumber()+i, listOfDirectories.get(getPageNumber()+i));
    }
    listOfDirectoriesCache.sort(String::compareToIgnoreCase);

    return listOfDirectoriesCache;
  }

  public int getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber;
  }


}
