package day20.workshop.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Random;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Coin {

    private String index;

    private String name;

    private LocalDate date;

    private String price;

    private String currency;

    public String getName() {
        return name;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Coin() {
        this.date = LocalDate.now();
        this.index = generateId(5);
    }

    private synchronized String generateId(int numOfChar) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < numOfChar) {
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, numOfChar);
    }

    public static Coin createCoin(String json, String id, String currency) throws IOException {
        Coin c = new Coin();
        try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
            JsonReader r = Json.createReader(is);
            JsonObject o = r.readObject();
            c.setName(id.toUpperCase());
            c.setCurrency(currency.toUpperCase());
            c.setPrice(o.getJsonObject(id).getJsonNumber(currency).toString());
        }
        return c;
    }

}
