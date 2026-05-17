export const roleText: Record<number, string> = {
  1: '普通用户',
  100: '管理员',
};

export const teamRoleText: Record<number, string> = {
  1: '成员',
  10: '组长',
};

export const noticeTypeText: Record<number, string> = {
  10: '系统通知',
  11: '个人通知',
  20: '小组通知',
  21: '成员通知',
  30: '邀请通知',
  31: '申请通知',
};

export const noticeStatusText: Record<number, string> = {
  1: '待处理',
  2: '已同意',
  3: '已拒绝',
};

export const taskStatusText: Record<number, string> = {
  1: '待领取',
  2: '进行中',
  3: '待审核',
  4: '已完成',
  5: '已驳回',
};

export const taskPriorityText: Record<number, string> = {
  1: '低',
  2: '普通',
  3: '高',
  4: '紧急',
};

export const taskStatusColor: Record<number, string> = {
  1: 'default',
  2: 'processing',
  3: 'warning',
  4: 'success',
  5: 'error',
};

export const taskPriorityColor: Record<number, string> = {
  1: 'blue',
  2: 'green',
  3: 'orange',
  4: 'red',
};

export function formatDate(value?: string | null) {
  if (!value) return '-';
  return value.replace('T', ' ').slice(0, 19);
}

export function compactObject<T extends Record<string, unknown>>(value: T) {
  const result: Record<string, unknown> = {};
  Object.entries(value).forEach(([key, item]) => {
    if (item !== undefined && item !== null && item !== '') {
      result[key] = item;
    }
  });
  return result as Partial<T>;
}
