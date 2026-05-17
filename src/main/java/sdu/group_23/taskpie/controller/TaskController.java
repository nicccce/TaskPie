package sdu.group_23.taskpie.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sdu.group_23.taskpie.data.dto.PageResponse;
import sdu.group_23.taskpie.data.dto.task.CreateTaskRequest;
import sdu.group_23.taskpie.data.dto.task.ReviewTaskRequest;
import sdu.group_23.taskpie.data.dto.task.SubmitTaskRequest;
import sdu.group_23.taskpie.data.dto.task.TaskResponse;
import sdu.group_23.taskpie.data.dto.task.UpdateTaskRequest;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.service.TaskService;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    public Response<Integer> create(@RequestBody @Valid CreateTaskRequest request) {
        return taskService.create(request);
    }

    @GetMapping("/me")
    public Response<PageResponse<TaskResponse>> getMyTasks(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return taskService.getMyTasks(status, page, size);
    }

    @GetMapping("/user/{userId}")
    public Response<PageResponse<TaskResponse>> getUserTasks(
            @PathVariable Integer userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return taskService.getUserTasks(userId, status, page, size);
    }

    @GetMapping("/list")
    public Response<PageResponse<TaskResponse>> list(
            @RequestParam(required = false) Integer teamId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return taskService.list(teamId, status, page, size);
    }

    @GetMapping("/{taskId}")
    public Response<TaskResponse> getTask(@PathVariable Integer taskId) {
        return taskService.getTask(taskId);
    }

    @PutMapping("/{taskId}")
    public Response<Void> update(@PathVariable Integer taskId, @RequestBody @Valid UpdateTaskRequest request) {
        return taskService.update(taskId, request);
    }

    @DeleteMapping("/{taskId}")
    public Response<Void> delete(@PathVariable Integer taskId) {
        return taskService.delete(taskId);
    }

    @PatchMapping("/{taskId}/apply")
    public Response<Void> apply(@PathVariable Integer taskId) {
        return taskService.apply(taskId);
    }

    @PatchMapping("/{taskId}/abandon")
    public Response<Void> abandon(@PathVariable Integer taskId) {
        return taskService.abandon(taskId);
    }

    @PostMapping("/{taskId}/submit")
    public Response<Void> submit(@PathVariable Integer taskId, @RequestBody @Valid SubmitTaskRequest request) {
        return taskService.submit(taskId, request);
    }

    @PatchMapping("/{taskId}/review")
    public Response<Void> review(@PathVariable Integer taskId, @RequestBody @Valid ReviewTaskRequest request) {
        return taskService.review(taskId, request);
    }

    @PostMapping("/{taskId}/upload")
    public Response<Void> upload(@PathVariable Integer taskId, @RequestParam("file") MultipartFile file) {
        return taskService.upload(taskId, file);
    }

    @GetMapping("/{taskId}/download")
    public ResponseEntity<Resource> download(@PathVariable Integer taskId) {
        return taskService.download(taskId);
    }
}
