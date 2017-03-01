import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends TelegramLongPollingBot {

  static ArrayList<String> listOfFiles = new ArrayList<String>();
  static List<String> listOfDirectories = new ArrayList<>();
  private static String currentFilePath;

  FileManager FileManager = new FileManager();

  public static void main(String[] args) {
    ApiContextInitializer.init();
    TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
    try {
      telegramBotsApi.registerBot(new Main());
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }


  @Override
  public String getBotUsername() {
    return Credentials.BOT_USERNAME;
  }

  @Override
  public String getBotToken() {
    return Credentials.BOT_TOKEN;
  }

  @Override
  public void onUpdateReceived(Update update) {
    Message message = update.getMessage();
    String messageString = message.getText();
    if(messageString != null) {
      if(messageString.startsWith("/")) {
        messageString = messageString.substring(1);
      }
      //start
      if(messageString.equals("start")) {
        currentFilePath = FileManager.getFirstImagePath();
        sendImg(message, currentFilePath);
        FileManager.setPageNumber(0);
        FileManager.setListOfDirectories();
        sendKeyboard(message.getChatId());
      }
      listOfDirectories = FileManager.getListOfDirectories();
      //move to directory
      if(listOfDirectories.contains(messageString)) {
        currentFilePath = FileManager.getFirstImagePath();
        String futureFilepath = currentFilePath+"/"+messageString;
        FileManager.moveFile(currentFilePath, futureFilepath);

        FileManager.removeFirstImage();

      } else {
        sendMsg(message, "Я не знаю что ответить на это,сер");
      }
    }
  }


  public void sendMsg(Message message, String text) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.enableMarkdown(true);
    sendMessage.setChatId(message.getChatId().toString());
//        sendMessage.setReplyToMessageId(message.getMessageId());
    sendMessage.setText(text);

    try {
      sendMessage(sendMessage);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
//        sendMessage.setReplyMarkup(sendKeyboard);
  }

  public void sendImg(Message message, String path) {
    SendPhoto sendPhoto = new SendPhoto();
    sendPhoto.setChatId(message.getChatId().toString());
    File file = null;
    try {
      file = FileManager.loadImage(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
    sendPhoto.setNewPhoto(file);

    try {
      sendPhoto(sendPhoto);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }

  }

  public void sendKeyboard(Long chatID) {
    SendMessage message = new SendMessage();
    message.setChatId(chatID);
    message.setText("Custom message text");
    // Create ReplyKeyboardMarkup object
    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    // Create the keyboard (list of keyboard rows)
    List<KeyboardRow> keyboard = new ArrayList<>();
    // Create a keyboard row
    KeyboardRow row = new KeyboardRow();
    row.add("Next page");
    row.add("Create directory");
    row.add("Delete photo");
    keyboard.add(row);

    List<String> listOfDirectoriesNames = FileManager.getListOfDirectories();

    for (int i = 0; i < 4; i++) {
      row = new KeyboardRow();
      List<String> firstRow = listOfDirectoriesNames.subList(0+(i*7), 7+(i*7));
      for (int j = 0; j < 7; j++) {
        String row1 = firstRow.get(j);
        System.out.println(row1);
        row.add(row1);
      }
      keyboard.add(row);
      System.out.println("-------");
    }

    // Set the keyboard to the markup
    keyboardMarkup.setKeyboard(keyboard);
    // Add it to the message
    message.setReplyMarkup(keyboardMarkup);

    try {
      // Send the message
      sendMessage(message);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }
}