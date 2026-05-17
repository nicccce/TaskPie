import request from './request';
import type { LoginPayload, LoginResponse, RegisterPayload } from '../types';

export function login(data: LoginPayload) {
  return request.post<unknown, LoginResponse>('/login', data);
}

export function register(data: RegisterPayload) {
  return request.post<unknown, void>('/register', data);
}

export function logout() {
  return request.post<unknown, void>('/logout');
}

export function updatePassword(data: { oldPassword: string; newPassword: string }) {
  return request.patch<unknown, void>('/password', data);
}

export function closeAccount(password: string) {
  return request.post<unknown, void>('/close', null, { params: { password } });
}
