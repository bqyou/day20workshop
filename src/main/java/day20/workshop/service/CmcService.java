package day20.workshop.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import day20.workshop.model.Coin;

@Service
public class CmcService {

    private static final String COIN_URL = "https://api.coingecko.com/api/v3/simple/price";

    private static final String COIN_LIST = "COINS";

    public Optional<Coin> getListing(String id, String currency) throws IOException {
        String cmcUrl = UriComponentsBuilder.fromUriString(COIN_URL)
                .queryParam("ids", id)
                .queryParam("vs_currencies", currency)
                .toUriString();
        RestTemplate template = new RestTemplate();

        ResponseEntity<String> response = template.getForEntity(cmcUrl, String.class);
        System.out.println(response.getBody());
        Coin c = Coin.createCoin(response.getBody(), id, currency);
        if (c != null) {
            return Optional.of(c);
        }
        return Optional.empty();
    }

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public void saveQuote(final Coin coin) {
        redisTemplate.opsForList().rightPush(COIN_LIST, coin.getName());
        redisTemplate.opsForHash().put(COIN_LIST + "_MAP", coin.getName(), coin);
    }

    public List<Coin> listAll() {
        List<Object> fromCoins = redisTemplate.opsForList().range(COIN_LIST, 0,
                redisTemplate.opsForList().size(COIN_LIST));
        List<Coin> coins = redisTemplate.opsForHash()
                .multiGet(COIN_LIST + "_MAP", fromCoins)
                .stream()
                .filter(Coin.class::isInstance)
                .map(Coin.class::cast)
                .toList();
        return coins;
    }

    public void clear() {
        while (redisTemplate.opsForList().size(COIN_LIST) > 0) {
            redisTemplate.opsForHash().delete(COIN_LIST + "_MAP", redisTemplate.opsForList().leftPop(COIN_LIST));
        }
    }

}
