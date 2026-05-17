import request from './request';
import type { PageResponse, SelectParams, Team, TeamMember, TeamSummary } from '../types';

export function getMyTeams() {
  return request.get<unknown, { teams: TeamSummary[] }>('/team/me');
}

export function createTeam(data: { name: string; description?: string }) {
  return request.post<unknown, number>('/team/create', data);
}

export function deleteTeam(teamId: number) {
  return request.delete<unknown, void>(`/team/${teamId}`);
}

export function selectTeams(data: SelectParams) {
  return request.post<unknown, PageResponse<Team>>('/team/select', {
    page: 0,
    size: 10,
    ...data,
  });
}

export function getTeam(teamId: number) {
  return request.get<unknown, Team>(`/team/${teamId}`);
}

export function getMembers(teamId: number) {
  return request.get<unknown, TeamMember[]>(`/team/${teamId}/member`);
}

export function applyTeam(teamId: number) {
  return request.post<unknown, void>(`/team/${teamId}/apply`);
}

export function inviteMember(teamId: number, userId: number) {
  return request.post<unknown, void>(`/team/${teamId}/invite`, { userId });
}

export function removeMembers(teamId: number, userIds: number[]) {
  return request.delete<unknown, void>(`/team/${teamId}/remove`, { data: { userIds } });
}

export function quitTeam(teamId: number) {
  return request.delete<unknown, void>(`/team/${teamId}/quit`);
}

export function transferLeader(teamId: number, userId: number) {
  return request.patch<unknown, void>(`/team/${teamId}/transfer`, { userId });
}
