package ru.iu3.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.iu3.backend.models.Artist;
import ru.iu3.backend.models.Country;
import ru.iu3.backend.repositories.*;
import ru.iu3.backend.tools.DataValidationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class ArtistController {

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    CountryRepository countryRepository;

    @GetMapping("/artists")
    public Page<Artist> getAllArtists(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return artistRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity getArtist(@PathVariable(value = "id") Long artistId) throws DataValidationException {
        Artist artists = artistRepository.findById(artistId).
                orElseThrow(() -> new DataValidationException("Художник с таким индексом не найден"));
        return ResponseEntity.ok(artists);
    }
    @PostMapping("/artists")
    public ResponseEntity<Object> createArtist(@RequestBody Artist artists) throws Exception {
        try {
            // Попытка сохранить что-либо в базу данных
            Artist newArtists = artistRepository.save(artists);
            return new ResponseEntity<Object>(newArtists, HttpStatus.OK);
        } catch (Exception exception) {
            if (exception.getMessage().contains("artists.name_UNIQUE")) {
                throw new DataValidationException("Эта страна уже есть в базе");
            } else {
                throw new DataValidationException("Неизвестная ошибка");
            }
        }
    }

    @PostMapping("/deleteartists")
    public ResponseEntity deleteArtists(@Validated @RequestBody List<Artist> artists) {
        artistRepository.deleteAll(artists);
        return new ResponseEntity(HttpStatus.OK);
    }



    @DeleteMapping("/artists/{id}")
    public ResponseEntity<Object> deleteArtist(@PathVariable(value = "id") Long artistID) {
        Optional<Artist> artists = artistRepository.findById(artistID);
        Map<String, Boolean> resp = new HashMap<>();

        // Возвратит true, если объект существует (не пустой)
        if (artists.isPresent()) {
            artistRepository.delete(artists.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }

        return ResponseEntity.ok(resp);
    }
}

