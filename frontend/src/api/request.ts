import axios, { type AxiosRequestConfig, type AxiosResponse } from 'axios';
import { message } from 'antd';
import type { ApiResponse } from '../types';
import { clearAuth, getToken } from '../utils/auth';

export const API_BASE_URL = 'http://localhost:8080/api';

const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
});

function redirectToLogin() {
  clearAuth();
  if (!window.location.pathname.includes('/login')) {
    window.location.href = '/login';
  }
}

request.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse<unknown>>) => {
    if (response.config.responseType === 'blob') {
      return response as unknown as AxiosResponse<unknown>;
    }

    const body = response.data;
    if (body && typeof body.code === 'number') {
      if (body.code !== 200) {
        if (body.code === 401) {
          redirectToLogin();
        }
        message.error(body.message || '请求失败');
        return Promise.reject(new Error(body.message || '请求失败'));
      }
      return body.data as never;
    }

    return body as never;
  },
  (error) => {
    const status = error?.response?.status;
    const responseData = error?.response?.data;
    const errorMessage = responseData?.message || error?.message || '网络请求失败';

    if (status === 401 || responseData?.code === 401) {
      redirectToLogin();
    }
    message.error(errorMessage);
    return Promise.reject(error);
  },
);

export function rawRequest<T = unknown>(config: AxiosRequestConfig) {
  return request.request<T, AxiosResponse<T>>(config);
}

export default request;
