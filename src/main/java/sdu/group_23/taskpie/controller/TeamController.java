package sdu.group_23.taskpie.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sdu.group_23.taskpie.data.dto.team.CreateRequest;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.service.TeamService;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/create")
    public Response<Integer> create(@RequestBody @Valid CreateRequest createRequest) {
        return teamService.create(createRequest);
    }
}
