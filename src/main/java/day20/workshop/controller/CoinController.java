package day20.workshop.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import day20.workshop.model.Coin;
import day20.workshop.service.CmcService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping
public class CoinController {

    @Autowired
    private CmcService cmcSvc;

    @PostMapping(path = "/coin")
    public String getListings(@RequestParam(required = true) String id, @RequestParam(required = true) String currency,
            Model model, HttpSession session) throws IOException {
        Optional<Coin> coin = cmcSvc.getListing(id.toLowerCase(), currency);
        model.addAttribute("coin", coin.get());
        session.setAttribute("coin", coin.get());
        return "coin";
    }

    @PostMapping(path = "/list")
    public String saveListing(Model model, HttpSession session) {
        Coin c = (Coin) session.getAttribute("coin");
        cmcSvc.saveQuote(c);
        System.out.println("IM HERE");
        List<Coin> coins = cmcSvc.listAll();
        model.addAttribute("coins", coins);
        session.invalidate();
        return "list";
    }

    @PostMapping(path = "/clear")
    public String clearListing(Model model) {
        cmcSvc.clear();
        List<Coin> coins = cmcSvc.listAll();
        model.addAttribute("coins", coins);
        return "list";
    }

}
