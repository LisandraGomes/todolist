package br.com.lisandra.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.lisandra.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/created")
    public ResponseEntity created(@RequestBody Task tasks, HttpServletRequest request) {
        var userId = request.getAttribute("idUser");
        System.out.println("id:" + userId);
        tasks.setIdUser((UUID) userId);

        var currentDate = LocalDateTime.now();
        if (currentDate.isAfter(tasks.getStartAt()) || currentDate.isAfter(tasks.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Data de início ou término deve ser maior que a data atual.");
        }
        if (tasks.getStartAt().isAfter(tasks.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Data de início deve ser menor que a de término.");
        }

        var task = this.taskRepository.save(tasks);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/list")
    public List<Task> list(HttpServletRequest request) {
        var userId = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) userId);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody Task task, @PathVariable UUID id, HttpServletRequest request)
    {
        var tasks = this.taskRepository.findById(id).orElse(null);

        if(task==null){
            return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body("Tarefa não encontrada!");
        }

        var idUser = request.getAttribute("idUser");
        if(!task.getIdUser().equals(idUser)){
            return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body("Usuário não permitido");
        }

        Utils.copyNonNullProperties(task, tasks);
        var taskUpdate = this.taskRepository.save(task);
        return ResponseEntity.status((HttpStatus.OK)).body(taskUpdate);
    }
}
