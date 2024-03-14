package com.example.library_book.Premiun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/Premiun")
public class PremiunController {
    @Autowired
    private PremiunService premiunService;

    @PostMapping("/create")
    public ResponseEntity<?> createPremiun(@RequestBody List<Premiun> premiunList){
        return ResponseEntity.ok().body(premiunService.createPremiun(premiunList));
    }
}
