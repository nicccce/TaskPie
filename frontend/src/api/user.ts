import request from './request';
import type { PageResponse, SelectParams, UserProfile, UserSearchItem } from '../types';

export function getMe() {
  return request.get<unknown, UserProfile>('/user/me');
}

export function updateMe(data: Pick<UserProfile, 'studentId' | 'realName' | 'nickName' | 'bio'>) {
  return request.put<unknown, void>('/user/me', data);
}

export function getUser(userId: number) {
  return request.get<unknown, UserProfile>(`/user/${userId}`);
}

export function selectUsers(data: SelectParams) {
  return request.post<unknown, PageResponse<UserSearchItem>>('/user/select', {
    page: 0,
    size: 10,
    ...data,
  });
}
