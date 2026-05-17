export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface PageResponse<T> {
  currentPage: number;
  items: T[];
  totalPages: number;
  totalItems: number;
  hasNext: boolean;
}

export interface LoginPayload {
  userName: string;
  password: string;
}

export interface LoginResponse {
  nickName: string;
  role: number;
  token: string;
}

export interface RegisterPayload {
  studentId: string;
  userName: string;
  password: string;
  realName: string;
  nickName: string;
}

export interface UserProfile {
  userId: number;
  studentId: string;
  userName?: string;
  avatar_url?: string | null;
  realName: string;
  nickName: string;
  bio?: string | null;
  role: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface TeamSummary {
  teamId: number;
  teamName: string;
}

export interface Team {
  teamId: number;
  name: string;
  description?: string | null;
  leaderId: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface TeamMember {
  userId: number;
  studentId?: string;
  realName?: string;
  nickName?: string;
  avatar_url?: string | null;
  role: number;
  joinedAt?: string;
}

export interface UserSearchItem {
  userId: number;
  studentId?: string;
  realName?: string;
  nickName?: string;
  avatar_url?: string | null;
}

export interface Notice {
  noticeId: number;
  type: number;
  title: string;
  content?: string;
  senderId?: number;
  teamId?: number;
  receiverId?: number;
  status?: number;
  hasRead?: boolean;
  top?: boolean;
  createdAt?: string;
  readAt?: string;
}

export interface Task {
  taskId: number;
  teamId: number;
  creatorId: number;
  assigneeId?: number | null;
  title: string;
  description?: string | null;
  priority: number;
  status: number;
  deadline?: string | null;
  submitContent?: string | null;
  reviewFeedback?: string | null;
  attachmentName?: string | null;
  submittedAt?: string | null;
  reviewedAt?: string | null;
  createdAt?: string;
  updatedAt?: string;
}

export interface SelectParams {
  keyword: string;
  page?: number;
  size?: number;
}
