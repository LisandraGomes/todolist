package br.com.lisandra.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private IUserRepository userRepository;



    @PostMapping("/createUser")
    public ResponseEntity create(@RequestBody User userr){
        var userfind = this.userRepository.findByUsername(userr.getUsername());

        if(userfind !=null)
        {            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario já existe");
        }

        var passwordHash = BCrypt.withDefaults().hashToString(12, userr.getPassword().toCharArray()); 
        userr.setPassword(passwordHash);
        var userCreated = this.userRepository.save(userr);
        return ResponseEntity.status(HttpStatus.OK).body(userCreated);
    }
}
