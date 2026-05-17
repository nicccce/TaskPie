import request from './request';
import type { Notice, PageResponse } from '../types';

export function selectNotices(data: { type?: number; teamId?: number; page?: number; size?: number }) {
  return request.post<unknown, PageResponse<Notice>>('/notice/select', {
    type: 0,
    teamId: 0,
    page: 0,
    size: 10,
    ...data,
  });
}

export function getNotice(noticeId: number) {
  return request.get<unknown, Notice>(`/notice/${noticeId}`);
}

export function handleNotice(noticeId: number, data: { accept: boolean; feedback?: string }) {
  return request.patch<unknown, void>(`/notice/${noticeId}/handle`, data);
}
