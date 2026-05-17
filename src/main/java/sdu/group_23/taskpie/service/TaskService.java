package sdu.group_23.taskpie.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import sdu.group_23.taskpie.data.dto.PageResponse;
import sdu.group_23.taskpie.data.dto.task.CreateTaskRequest;
import sdu.group_23.taskpie.data.dto.task.ReviewTaskRequest;
import sdu.group_23.taskpie.data.dto.task.SubmitTaskRequest;
import sdu.group_23.taskpie.data.dto.task.TaskResponse;
import sdu.group_23.taskpie.data.dto.task.UpdateTaskRequest;
import sdu.group_23.taskpie.data.enums.TaskPriority;
import sdu.group_23.taskpie.data.enums.TaskStatus;
import sdu.group_23.taskpie.data.po.Task;
import sdu.group_23.taskpie.data.po.TeamMember;
import sdu.group_23.taskpie.data.vo.CommonErr;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.repository.TaskRepository;
import sdu.group_23.taskpie.repository.TeamMemberRepository;
import sdu.group_23.taskpie.repository.TeamRepository;
import sdu.group_23.taskpie.repository.UserRepository;
import sdu.group_23.taskpie.util.PermissionChecker;
import sdu.group_23.taskpie.util.UserContextUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final PermissionChecker permissionChecker;

    @Value("${attach.folder:uploads}")
    private String attachFolder;

    @Transactional
    public Response<Integer> create(CreateTaskRequest request) {
        if (teamRepository.findByTeamId(request.getTeamId()) == null) {
            return Response.error(CommonErr.TEAM_NOT_FOUND);
        }
        if (!permissionChecker.isTeamLeader(request.getTeamId())) {
            return Response.error(CommonErr.PERMISSION_DENIED);
        }
        if (request.getAssigneeId() != null && !teamMemberRepository.existsByTeamIdAndUserId(request.getTeamId(), request.getAssigneeId())) {
            return Response.error(CommonErr.NOT_TEAM_MEMBER);
        }

        Integer priority = request.getPriority() == null ? TaskPriority.NORMAL.getValue() : request.getPriority();
        if (!TaskPriority.contains(priority)) {
            return Response.error(CommonErr.PARAM_ERROR);
        }

        Task task = Task.builder()
                .teamId(request.getTeamId())
                .creatorId(UserContextUtil.getCurrentUserId())
                .assigneeId(request.getAssigneeId())
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(priority)
                .status(request.getAssigneeId() == null ? TaskStatus.PENDING.getValue() : TaskStatus.IN_PROGRESS.getValue())
                .deadline(request.getDeadline())
                .build();

        taskRepository.save(task);
        return Response.success(task.getTaskId());
    }

    public Response<TaskResponse> getTask(Integer taskId) {
        Task task = taskRepository.findByTaskId(taskId);
        if (task == null) {
            return Response.error(CommonErr.TASK_NOT_FOUND);
        }
        if (!permissionChecker.isTeamMember(task.getTeamId())) {
            return Response.error(CommonErr.PERMISSION_DENIED);
        }
        return Response.success(toResponse(task));
    }

    public Response<PageResponse<TaskResponse>> getMyTasks(Integer status, int page, int size) {
        Pageable pageable = page(page, size);
        Page<Task> tasks = taskRepository.findAssignedTasks(UserContextUtil.getCurrentUserId(), normalizeStatus(status), pageable);
        return Response.success(PageResponse.pageToResponse(tasks.map(this::toResponse)));
    }

    public Response<PageResponse<TaskResponse>> getUserTasks(Integer userId, Integer status, int page, int size) {
        if (!userRepository.existsByUserId(userId)) {
            return Response.error(CommonErr.USER_NOT_FOUND);
        }

        Pageable pageable = page(page, size);
        if (userId.equals(UserContextUtil.getCurrentUserId())) {
            return getMyTasks(status, page, size);
        }

        List<Integer> joinedTeamIds = teamMemberRepository.findByUserId(UserContextUtil.getCurrentUserId())
                .stream()
                .map(TeamMember::getTeamId)
                .toList();
        if (joinedTeamIds.isEmpty()) {
            return Response.success(PageResponse.pageToResponse(Page.empty(pageable)));
        }

        Page<Task> tasks = taskRepository.findAssignedTasksInTeams(userId, joinedTeamIds, normalizeStatus(status), pageable);
        return Response.success(PageResponse.pageToResponse(tasks.map(this::toResponse)));
    }

    public Response<PageResponse<TaskResponse>> list(Integer teamId, Integer status, int page, int size) {
        Pageable pageable = page(page, size);
        Integer normalizedStatus = normalizeStatus(status);

        if (teamId != null && teamId > 0) {
            if (teamRepository.findByTeamId(teamId) == null) {
                return Response.error(CommonErr.TEAM_NOT_FOUND);
            }
            if (!permissionChecker.isTeamMember(teamId)) {
                return Response.error(CommonErr.PERMISSION_DENIED);
            }
            Page<Task> tasks = taskRepository.findTeamTasks(teamId, normalizedStatus, pageable);
            return Response.success(PageResponse.pageToResponse(tasks.map(this::toResponse)));
        }

        List<Integer> joinedTeamIds = teamMemberRepository.findByUserId(UserContextUtil.getCurrentUserId())
                .stream()
                .map(TeamMember::getTeamId)
                .toList();
        if (joinedTeamIds.isEmpty()) {
            return Response.success(PageResponse.pageToResponse(Page.empty(pageable)));
        }

        Page<Task> tasks = taskRepository.findJoinedTeamTasks(joinedTeamIds, normalizedStatus, pageable);
        return Response.success(PageResponse.pageToResponse(tasks.map(this::toResponse)));
    }

    @Transactional
    public Response<Void> update(Integer taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findByTaskId(taskId);
        if (task == null) {
            return Response.error(CommonErr.TASK_NOT_FOUND);
        }
        if (!permissionChecker.isTeamLeader(task.getTeamId())) {
            return Response.error(CommonErr.PERMISSION_DENIED);
        }

        if (request.getAssigneeId() != null) {
            if (!teamMemberRepository.existsByTeamIdAndUserId(task.getTeamId(), request.getAssigneeId())) {
                return Response.error(CommonErr.NOT_TEAM_MEMBER);
            }
            task.setAssigneeId(request.getAssigneeId());
            if (task.getStatus().equals(TaskStatus.PENDING.getValue())) {
                task.setStatus(TaskStatus.IN_PROGRESS.getValue());
            }
        }
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            if (!TaskPriority.contains(request.getPriority())) {
                return Response.error(CommonErr.PARAM_ERROR);
            }
            task.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            if (!TaskStatus.contains(request.getStatus())) {
                return Response.error(CommonErr.PARAM_ERROR);
            }
            task.setStatus(request.getStatus());
        }
        if (request.getDeadline() != null) {
            task.setDeadline(request.getDeadline());
        }

        taskRepository.save(task);
        return Response.ok();
    }

    @Transactional
    public Response<Void> delete(Integer taskId) {
        Task task = taskRepository.findByTaskId(taskId);
        if (task == null) {
            return Response.error(CommonErr.TASK_NOT_FOUND);
        }
        if (!permissionChecker.isTeamLeader(task.getTeamId())) {
            return Response.error(CommonErr.PERMISSION_DENIED);
        }
        taskRepository.delete(task);
        return Response.ok();
    }

    @Transactional
    public Response<Void> apply(Integer taskId) {
        Task task = taskRepository.findByTaskId(taskId);
        if (task == null) {
            return Response.error(CommonErr.TASK_NOT_FOUND);
        }
        if (!permissionChecker.isTeamMember(task.getTeamId())) {
            return Response.error(CommonErr.PERMISSION_DENIED);
        }
        if (task.getStatus().equals(TaskStatus.COMPLETED.getValue()) || task.getStatus().equals(TaskStatus.SUBMITTED.getValue())) {
            return Response.error(CommonErr.TASK_STATUS_INVALID);
        }
        Integer currentUserId = UserContextUtil.getCurrentUserId();
        if (task.getAssigneeId() != null && !task.getAssigneeId().equals(currentUserId)) {
            return Response.error(CommonErr.TASK_ALREADY_ASSIGNED);
        }

        task.setAssigneeId(currentUserId);
        task.setStatus(TaskStatus.IN_PROGRESS.getValue());
        taskRepository.save(task);
        return Response.ok();
    }

    @Transactional
    public Response<Void> abandon(Integer taskId) {
        Task task = taskRepository.findByTaskId(taskId);
        if (task == null) {
            return Response.error(CommonErr.TASK_NOT_FOUND);
        }
        if (!UserContextUtil.getCurrentUserId().equals(task.getAssigneeId())) {
            return Response.error(CommonErr.PERMISSION_DENIED);
        }
        if (task.getStatus().equals(TaskStatus.COMPLETED.getValue()) || task.getStatus().equals(TaskStatus.SUBMITTED.getValue())) {
            return Response.error(CommonErr.TASK_STATUS_INVALID);
        }

        task.setAssigneeId(null);
        task.setStatus(TaskStatus.PENDING.getValue());
        taskRepository.save(task);
        return Response.ok();
    }

    @Transactional
    public Response<Void> submit(Integer taskId, SubmitTaskRequest request) {
        Task task = taskRepository.findByTaskId(taskId);
        if (task == null) {
            return Response.error(CommonErr.TASK_NOT_FOUND);
        }
        if (task.getAssigneeId() == null) {
            return Response.error(CommonErr.TASK_NOT_ASSIGNED);
        }

        boolean assignee = UserContextUtil.getCurrentUserId().equals(task.getAssigneeId());
        boolean leader = permissionChecker.isTeamLeader(task.getTeamId());
        if (!assignee && !leader) {
            return Response.error(CommonErr.PERMISSION_DENIED);
        }
        if (!task.getStatus().equals(TaskStatus.IN_PROGRESS.getValue()) && !task.getStatus().equals(TaskStatus.REJECTED.getValue())) {
            return Response.error(CommonErr.TASK_STATUS_INVALID);
        }

        task.setSubmitContent(request.getContent());
        task.setStatus(TaskStatus.SUBMITTED.getValue());
        task.setSubmittedAt(LocalDateTime.now());
        taskRepository.save(task);
        return Response.ok();
    }

    @Transactional
    public Response<Void> review(Integer taskId, ReviewTaskRequest request) {
        Task task = taskRepository.findByTaskId(taskId);
        if (task == null) {
            return Response.error(CommonErr.TASK_NOT_FOUND);
        }
        if (!permissionChecker.isTeamLeader(task.getTeamId())) {
            return Response.error(CommonErr.PERMISSION_DENIED);
        }
        if (!task.getStatus().equals(TaskStatus.SUBMITTED.getValue())) {
            return Response.error(CommonErr.TASK_STATUS_INVALID);
        }

        task.setReviewFeedback(request.getFeedback());
        task.setReviewedAt(LocalDateTime.now());
        task.setStatus(Boolean.TRUE.equals(request.getPassed()) ? TaskStatus.COMPLETED.getValue() : TaskStatus.REJECTED.getValue());
        taskRepository.save(task);
        return Response.ok();
    }

    @Transactional
    public Response<Void> upload(Integer taskId, MultipartFile file) {
        Task task = taskRepository.findByTaskId(taskId);
        if (task == null) {
            return Response.error(CommonErr.TASK_NOT_FOUND);
        }
        if (!canOperateTaskFile(task)) {
            return Response.error(CommonErr.PERMISSION_DENIED);
        }
        if (file == null || file.isEmpty()) {
            return Response.error(CommonErr.PARAM_ERROR);
        }

        try {
            String originalFilename = file.getOriginalFilename() == null ? "attachment" : file.getOriginalFilename();
            String safeName = originalFilename.replaceAll("[\\\\/:*?\"<>|]", "_");
            Path dir = Paths.get(attachFolder, "tasks", String.valueOf(taskId)).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            Path target = dir.resolve(System.currentTimeMillis() + "_" + safeName).normalize();
            if (!target.startsWith(dir)) {
                return Response.error(CommonErr.PARAM_ERROR);
            }

            file.transferTo(target);
            task.setAttachmentName(originalFilename);
            task.setAttachmentPath(target.toString());
            taskRepository.save(task);
            return Response.ok();
        } catch (Exception e) {
            return Response.error(500, "附件保存失败：" + e.getMessage());
        }
    }

    public ResponseEntity<Resource> download(Integer taskId) {
        Task task = taskRepository.findByTaskId(taskId);
        if (task == null || !permissionChecker.isTeamMember(task.getTeamId()) || task.getAttachmentPath() == null) {
            return ResponseEntity.notFound().build();
        }

        FileSystemResource resource = new FileSystemResource(task.getAttachmentPath());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String fileName = task.getAttachmentName() == null ? "attachment" : task.getAttachmentName();
        String encodedName = UriUtils.encode(fileName, StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .body(resource);
    }

    private boolean canOperateTaskFile(Task task) {
        Integer currentUserId = UserContextUtil.getCurrentUserId();
        return currentUserId.equals(task.getAssigneeId()) || permissionChecker.isTeamLeader(task.getTeamId());
    }

    private Integer normalizeStatus(Integer status) {
        if (status == null || status <= 0) {
            return null;
        }
        return TaskStatus.contains(status) ? status : null;
    }

    private Pageable page(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : Math.min(size, 100);
        return PageRequest.of(safePage, safeSize);
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .taskId(task.getTaskId())
                .teamId(task.getTeamId())
                .creatorId(task.getCreatorId())
                .assigneeId(task.getAssigneeId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .deadline(task.getDeadline())
                .submitContent(task.getSubmitContent())
                .reviewFeedback(task.getReviewFeedback())
                .attachmentName(task.getAttachmentName())
                .submittedAt(task.getSubmittedAt())
                .reviewedAt(task.getReviewedAt())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
