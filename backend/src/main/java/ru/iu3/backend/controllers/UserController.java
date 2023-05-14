package ru.iu3.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.iu3.backend.models.Museum;
import ru.iu3.backend.models.User;
import ru.iu3.backend.repositories.MuseumRepository;
import ru.iu3.backend.repositories.UserRepository;
import ru.iu3.backend.tools.DataValidationException;
import ru.iu3.backend.tools.Utils;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class UserController {
    // По аналогии здесь используется два репозитория
    @Autowired
    UserRepository userRepository;

    @Autowired
    MuseumRepository museumRepository;


    @GetMapping("/users")
    public Page<User> getAllUsers(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return userRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "login")));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable(value = "id") Long userID)
            throws DataValidationException {
        User user = userRepository.findById(userID).
                orElseThrow(() -> new DataValidationException("User not founding"));

        return ResponseEntity.ok(user);
    }


    @PostMapping("/users")
    public ResponseEntity<Object> createUsers(@RequestBody User users) throws DataValidationException {
        try {
            User nc = userRepository.save(users);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        } catch (Exception exception) {
            if (exception.getMessage().contains("ConstraintViolationException")) {
                throw new DataValidationException("Этот пользователь есть в БД");
            } else {
                throw new DataValidationException("Неизвестная ошибка");
            }
        }
    }


    @PutMapping("/users/{id}")
    public ResponseEntity updateUser(@PathVariable(value = "id") Long userId,
                                     @Valid @RequestBody User userDetails)
            throws DataValidationException
    {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DataValidationException(" Пользователь с таким индексом не найден"));
            user.email = userDetails.email;
            String np = userDetails.np;
            if (np != null  && !np.isEmpty()) {
                byte[] b = new byte[32];
                new Random().nextBytes(b);
                String salt = new String(Hex.encode(b));
                user.password = Utils.ComputeHash(np, salt);
                user.salt = salt;
            }
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }
        catch (Exception ex) {
            String error;
            if (ex.getMessage().contains("users.email_UNIQUE"))
                throw new DataValidationException("Пользователь с такой почтой уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }



    @PostMapping("/deleteusers")
    public ResponseEntity deleteUsers(@Validated @RequestBody List<User> users) {
        userRepository.deleteAll(users);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUsers(@PathVariable(value = "id") Long userId) {
        Optional<User> users = userRepository.findById(userId);
        Map<String, Boolean> resp = new HashMap<>();

        // Возвратит true, если объект существует (не пустой)
        if (users.isPresent()) {
            userRepository.delete(users.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }

        return ResponseEntity.ok(resp);
    }
}