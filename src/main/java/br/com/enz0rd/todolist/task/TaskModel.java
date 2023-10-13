package br.com.enz0rd.todolist.task;

import br.com.enz0rd.todolist.priority.PriorityEnum;
import br.com.enz0rd.todolist.status.StatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
@Data
@Entity(name = "tb_tasks")
public class TaskModel {
    /**
     * id
     * user_id
     * description
     * title
     * date_init
     * date_finish
     * priority
     * status
     */
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String description;
    @Column(length = 50)
    private String title;
    private LocalDateTime date_init;
    private LocalDateTime date_finish;
    private PriorityEnum priority;
    private StatusEnum status;
    @CreationTimestamp
    private LocalDateTime created_at;
    private UUID userId;

    public void setTitle(String title) throws Exception {
        if(title.length() > 50) {
            throw new Exception("Title must be less than 50 characters long");
        }
        this.title = title;
    }

    public void setDescription(String description) throws Exception {
        if(description.length() > 255) {
            throw new Exception("Description must be less than 255 characters long");
        }
        this.description = description;
    }

    public void setDate_init(LocalDateTime date_init) throws Exception {
        if(date_init.isBefore(LocalDateTime.now())) {
            throw new Exception("Date to start task must be greater than the actual date");
        }
        this.date_init = date_init;
    }

    public void setDate_finish(LocalDateTime date_finish) throws Exception {
        if(date_finish.isBefore(LocalDateTime.now())) {
            throw new Exception("Date to finish task must be greater than the actual date");
        }
        this.date_finish = date_finish;
    }

    public void setPriority(PriorityEnum priority) throws Exception {
        if (priority == null) {
            throw new Exception("Priority cannot be null");
        } else if (priority != PriorityEnum.LOW && priority != PriorityEnum.MEDIUM && priority != PriorityEnum.HIGH) {
            throw new Exception("Priority must be LOW, MEDIUM or HIGH");
        }
        this.priority = priority;
    }

    public void setStatus(StatusEnum status) throws Exception {
        if (status == null) {
            throw new Exception("Status cannot be null");
        } else if (status != StatusEnum.TODO && status != StatusEnum.DOING && status != StatusEnum.DONE) {
            throw new Exception("Status must be TODO, DOING or DONE");
        }
        this.status = status;
    }

}
