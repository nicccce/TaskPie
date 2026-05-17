package sdu.group_23.taskpie.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sdu.group_23.taskpie.data.dto.PageResponse;
import sdu.group_23.taskpie.data.dto.team.CreateRequest;
import sdu.group_23.taskpie.data.dto.team.GetMyTeamResponse;
import sdu.group_23.taskpie.data.dto.team.GetTeamResponse;
import sdu.group_23.taskpie.data.dto.team.InviteRequest;
import sdu.group_23.taskpie.data.dto.team.SelectTeamRequest;
import sdu.group_23.taskpie.data.dto.team.TeamMemberResponse;
import sdu.group_23.taskpie.data.dto.team.TransferRequest;
import sdu.group_23.taskpie.data.dto.team.UserIdsRequest;
import sdu.group_23.taskpie.data.vo.Response;
import sdu.group_23.taskpie.service.TeamService;

import java.util.List;

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

    @GetMapping("/{teamId}/member")
    public Response<List<TeamMemberResponse>> getMembers(@PathVariable Integer teamId) {
        return teamService.getMembers(teamId);
    }

    @PostMapping("/{teamId}/apply")
    public Response<Void> apply(@PathVariable Integer teamId) {
        return teamService.apply(teamId);
    }

    @PostMapping("/{teamId}/invite")
    public Response<Void> invite(@PathVariable Integer teamId, @RequestBody @Valid InviteRequest request) {
        return teamService.invite(teamId, request.getUserId());
    }

    @DeleteMapping("/{teamId}/remove")
    public Response<Void> removeMembers(@PathVariable Integer teamId, @RequestBody @Valid UserIdsRequest request) {
        return teamService.removeMembers(teamId, request.getUserIds());
    }

    @DeleteMapping("/{teamId}/quit")
    public Response<Void> quit(@PathVariable Integer teamId) {
        return teamService.quit(teamId);
    }

    @PatchMapping("/{teamId}/transfer")
    public Response<Void> transfer(@PathVariable Integer teamId, @RequestBody @Valid TransferRequest request) {
        return teamService.transfer(teamId, request.getUserId());
    }

    @PostMapping("/select")
    public Response<PageResponse<GetTeamResponse>> select(@RequestBody @Valid SelectTeamRequest selectTeamRequest) {
        return teamService.select(selectTeamRequest);
    }

}
