package br.com.enz0rd.todolist.task;

import br.com.enz0rd.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        if (taskModel.getDate_finish().isBefore(LocalDateTime.now()) || taskModel.getDate_init().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(400).body("Date to start / finish task must greater than the actual date");
        } else if (taskModel.getDate_finish().isBefore(taskModel.getDate_init())) {
            return ResponseEntity.status(400).body("Date to finish task must greater than the date to start task");
        }

        taskModel.setUserId((UUID) request.getAttribute("userId")); // Facilitou com um cast
        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(200).body(task);
    }

    @GetMapping("/")
    public ResponseEntity<List<TaskModel>> listTasks(HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("userId");
        var tasks = this.taskRepository.findAllByUserId(userId);
        return ResponseEntity.status(200).body(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity listTasksById(HttpServletRequest request, @PathVariable UUID id) {
        var task = this.taskRepository.findById(id);
        return ResponseEntity.status(200).body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateTask(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {
        var idUser = (UUID) request.getAttribute("userId");
        var thisTask = this.taskRepository.findById(id).orElse(null);
        if(thisTask == null) {
            return ResponseEntity.status(403).body("Task not found");
        }
        if(!thisTask.getUserId().equals(idUser)) {
            return ResponseEntity.status(403).body("This user cannot update this task");
        }
        Utils.copyNonNullProp(taskModel, thisTask);
        var task = this.taskRepository.save(thisTask);
        return ResponseEntity.status(200).body(task);
    }
}
