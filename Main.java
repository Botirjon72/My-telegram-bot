package org.example;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Main extends TelegramLongPollingBot {
    @SneakyThrows
    public static void main(String[] args) {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(new Main());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            String text = update.getMessage().getText();
            SendMessage sendMessage = new SendMessage();
            UserList userList = new UserList();
            File file = new File("C:\\Users\\anonim\\Desktop\\user.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            if (text.equals("/start")) {
                sendMessage.setText("Assalomu aleykum Botimizga hush kelibsiz");

                String fistname = update.getMessage().getFrom().getFirstName();
                String lastname = update.getMessage().getFrom().getLastName();
                String username = update.getMessage().getFrom().getUserName();
                Long userId = update.getMessage().getFrom().getId();

                userList.setFirstName(fistname);
                userList.setLastName(lastname);
                userList.setUsername(username);
                userList.setUserId(userId);

                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
                bufferedWriter.write(userList.toString());
                bufferedWriter.newLine();
                bufferedWriter.close();


            } else if (text.equals("Kursni Hisoblash")) {
                sendMessage.setText("Iltimos, o'zingizga kerakli valyuta miqdorini kiriting (masalan: 50 USD)");
            } else if (text.matches("\\d+\\s+[A-Z]{3}")) {
                try {

                    URL url = new URL("https://cbu.uz/oz/arkhiv-kursov-valyut/json");
                    URLConnection urlConnection = url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    Gson gson = new Gson();
                    Collation[] currencies = gson.fromJson(bufferedReader, Collation[].class);
                    String price = text;

                    String[] textSplit = price.split(" ");
                    int amount = Integer.parseInt(textSplit[0]);
                    String currency = textSplit[1];

                    double rateToUsd = 0;
                    String currencyName = "";

                    for (Collation currents : currencies) {
                        if (currents.getCcy() != null && currents.getRate() != null) {
                            if (currents.getCcy().equals(currency)) {
                                rateToUsd = Double.parseDouble(currents.getRate());
                                currencyName = currents.getCcyNm_UZ();
                                break;
                            }
                        }
                    }

                    String result;
                    if (rateToUsd > 0) {
                        double amountInUZS = amount * rateToUsd;
                        result = amount + " " + currencyName + " => " + amountInUZS + " So'm";
                    } else {
                        result = "Kiritilgan valyuta kodi noto'g'ri yoki mavjud emas.";
                    }


                    sendMessage.setText(result);

                } catch (IOException e) {
                    sendMessage.setText("API bilan ulanishda xatolik yuz berdi.");
                } catch (Exception e) {
                    sendMessage.setText("Xatolik yuz berdi. Iltimos, qayta urinib ko'ring.");
                }
            } else if (text.equals("Valuta kurslari")) {
                try {
                    URL url = new URL("https://cbu.uz/oz/arkhiv-kursov-valyut/json");
                    URLConnection urlConnection = url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    Gson gson = new Gson();
                    Collation[] currencies = gson.fromJson(bufferedReader, Collation[].class);
                    if (currencies == null || currencies.length == 0) {
                        sendMessage.setText("Valyutalar ro'yxati bo'sh yoki xato yuz berdi.");
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Collation currents : currencies) {
                            String Ccy = currents.getCcy();
                            String Name = currents.getCcyNm_UZ();
                            String Price = currents.getRate();
                            String Date = currents.getDate();
                            String Diff = currents.getDiff();

                            stringBuilder.append("Ccy : ").append(Ccy).append("\n")
                                    .append("Name : ").append(Name).append("\n")
                                    .append("Price : ").append(Price).append("\n")
                                    .append("Date : ").append(Date).append("\n")
                                    .append("Diff : ").append(Diff).append("\n\n");
                        }

                        String message = stringBuilder.toString();
                        if (message.length() > 4066) {
                            message = message.substring(0, 4066);
                        }
                        sendMessage.setText(message);
                    }
                } catch (IOException e) {
                    sendMessage.setText("Xatolik yuz berdi: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (text.equals("Namoz vaqatlari")) {
                URL url = new URL("https://islomapi.uz/api/present/day?region=Namangan");
                URLConnection connection = url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Gson gson = new Gson();
                Namoz namoz = gson.fromJson(bufferedReader, Namoz.class);

                sendMessage.setText("Namoz Vaqtlari:\n" +
                        "Region: " + namoz.getRegion() + "\n" +
                        "Date: " + namoz.getDate() + "\n" +
                        "Tong Saharlik: " + namoz.getTimes().getTong_saharlik() + "\n" +
                        "Quyosh: " + namoz.getTimes().getQuyosh() + "\n" +
                        "Peshin: " + namoz.getTimes().getPeshin() + "\n" +
                        "Asr: " + namoz.getTimes().getAsr() + "\n" +
                        "Shom Iftorlik: " + namoz.getTimes().getShom_iftor() + "\n" +
                        "Hufton: " + namoz.getTimes().getHufton());
            }  else {
                sendMessage.setText("Boshqa mazmundagi so'zlarni yozmang !");
            }

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);

            List<KeyboardRow> keyboardRows = new ArrayList<>();
            KeyboardRow row1 = new KeyboardRow();
            row1.add(new KeyboardButton("Kursni Hisoblash"));
            row1.add(new KeyboardButton("Valuta kurslari"));

            KeyboardRow row2 = new KeyboardRow();
            row2.add(new KeyboardButton("Namoz vaqatlari"));
            keyboardRows.add(row1);
            keyboardRows.add(row2);
            replyKeyboardMarkup.setKeyboard(keyboardRows);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);


            sendMessage.setChatId(update.getMessage().getChatId());
            execute(sendMessage);
        } catch (TelegramApiException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotToken() {
        return "7666513690:AAG1to9zekdMZwBTNwrwgJlu5VDIwp9QQ6E";
    }

    @Override
    public String getBotUsername() {
        return "Tesjava_Bot";
    }
}