import type { AxiosResponse } from 'axios';
import request, { rawRequest } from './request';
import type { PageResponse, Task } from '../types';

export interface TaskQuery {
  teamId?: number;
  status?: number;
  page?: number;
  size?: number;
}

export interface TaskPayload {
  teamId?: number;
  assigneeId?: number | null;
  title?: string;
  description?: string;
  priority?: number;
  status?: number;
  deadline?: string | null;
}

export function listTasks(params: TaskQuery) {
  return request.get<unknown, PageResponse<Task>>('/task/list', { params });
}

export function getMyTasks(params: Omit<TaskQuery, 'teamId'>) {
  return request.get<unknown, PageResponse<Task>>('/task/me', { params });
}

export function getUserTasks(userId: number, params: Omit<TaskQuery, 'teamId'>) {
  return request.get<unknown, PageResponse<Task>>(`/task/user/${userId}`, { params });
}

export function getTask(taskId: number) {
  return request.get<unknown, Task>(`/task/${taskId}`);
}

export function createTask(data: TaskPayload) {
  return request.post<unknown, number>('/task/create', data);
}

export function updateTask(taskId: number, data: Omit<TaskPayload, 'teamId'>) {
  return request.put<unknown, void>(`/task/${taskId}`, data);
}

export function deleteTask(taskId: number) {
  return request.delete<unknown, void>(`/task/${taskId}`);
}

export function applyTask(taskId: number) {
  return request.patch<unknown, void>(`/task/${taskId}/apply`);
}

export function abandonTask(taskId: number) {
  return request.patch<unknown, void>(`/task/${taskId}/abandon`);
}

export function submitTask(taskId: number, content: string) {
  return request.post<unknown, void>(`/task/${taskId}/submit`, { content });
}

export function reviewTask(taskId: number, data: { passed: boolean; feedback?: string }) {
  return request.patch<unknown, void>(`/task/${taskId}/review`, data);
}

export function uploadAttachment(taskId: number, file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return request.post<unknown, void>(`/task/${taskId}/upload`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

export function downloadAttachment(taskId: number): Promise<AxiosResponse<Blob>> {
  return rawRequest<Blob>({
    url: `/task/${taskId}/download`,
    method: 'GET',
    responseType: 'blob',
  });
}
