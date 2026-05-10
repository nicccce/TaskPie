package sdu.group_23.taskpie.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sdu.group_23.taskpie.data.dto.PageResponse;
import sdu.group_23.taskpie.data.dto.team.CreateRequest;
import sdu.group_23.taskpie.data.dto.team.GetMyTeamResponse;
import sdu.group_23.taskpie.data.dto.team.GetTeamResponse;
import sdu.group_23.taskpie.data.dto.team.SelectTeamRequest;
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

    @DeleteMapping("/{teamId}")
    public Response<Void> delete(@PathVariable Integer teamId) {
        return teamService.delete(teamId);
    }

    @GetMapping("/{teamId}")
    public Response<GetTeamResponse> getTeam(@PathVariable Integer teamId) {
        return teamService.getTeam(teamId);
    }

    @GetMapping("/me")
    public Response<GetMyTeamResponse> getMyTeam() {
        return teamService.getMyTeam();
    }

    @PostMapping("/select")
    public Response<PageResponse<GetTeamResponse>> select(@RequestBody @Valid SelectTeamRequest selectTeamRequest) {
        return teamService.select(selectTeamRequest);
    }

}
