package com.squad20.sistema_climbe.controller;

import com.squad20.sistema_climbe.entity.User;
import com.squad20.sistema_climbe.entityDTO.UserDTO;
import com.squad20.sistema_climbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable int id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<UserDTO> findByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(userService.findByCpf(cpf));
    }

    @GetMapping("/cargo/{cargo}")
    public ResponseEntity<List<UserDTO>> findByCargo(@PathVariable String cargo) {
        return ResponseEntity.ok(userService.findByCargo(cargo));
    }

    @PostMapping
    public ResponseEntity<User> save(@RequestBody User user) {
        User savedUser = userService.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable int id, @RequestBody User user) {
        User updatedUser = userService.update(user, id);
        return ResponseEntity.ok(updatedUser);
    }
}
