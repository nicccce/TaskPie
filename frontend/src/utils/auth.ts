import type { LoginResponse } from '../types';

const TOKEN_KEY = 'taskpie_token';
const NICK_NAME_KEY = 'taskpie_nickName';
const ROLE_KEY = 'taskpie_role';

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function getNickName() {
  return localStorage.getItem(NICK_NAME_KEY) || 'TaskPie 用户';
}

export function getRole() {
  const role = localStorage.getItem(ROLE_KEY);
  return role ? Number(role) : undefined;
}

export function setAuthInfo(data: LoginResponse) {
  localStorage.setItem(TOKEN_KEY, data.token);
  localStorage.setItem(NICK_NAME_KEY, data.nickName || 'TaskPie 用户');
  localStorage.setItem(ROLE_KEY, String(data.role ?? ''));
  window.dispatchEvent(new Event('taskpie-auth-change'));
}

export function updateNickName(nickName: string) {
  localStorage.setItem(NICK_NAME_KEY, nickName);
  window.dispatchEvent(new Event('taskpie-auth-change'));
}

export function clearAuth() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(NICK_NAME_KEY);
  localStorage.removeItem(ROLE_KEY);
  window.dispatchEvent(new Event('taskpie-auth-change'));
}
