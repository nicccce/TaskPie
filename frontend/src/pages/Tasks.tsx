import {
  CheckCircleOutlined,
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
  EyeOutlined,
  PlusOutlined,
  ReloadOutlined,
  SendOutlined,
  UploadOutlined,
} from '@ant-design/icons';
import {
  Button,
  Card,
  DatePicker,
  Form,
  Input,
  InputNumber,
  Modal,
  Popconfirm,
  Select,
  Space,
  Table,
  Tabs,
  Tag,
  Upload,
  message,
  Typography,
} from 'antd';
import type { ColumnsType, TablePaginationConfig } from 'antd/es/table';
import type { UploadProps } from 'antd';
import dayjs, { type Dayjs } from 'dayjs';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  abandonTask,
  applyTask,
  createTask,
  deleteTask,
  downloadAttachment,
  getMyTasks,
  listTasks,
  getUserTasks,
  reviewTask,
  submitTask,
  updateTask,
  uploadAttachment,
  type TaskPayload,
} from '../api/task';
import { getMembers, getMyTeams } from '../api/team';
import { getMe } from '../api/user';
import type { PageResponse, Task, TeamMember, TeamSummary, UserProfile } from '../types';
import { compactObject, formatDate, taskPriorityColor, taskPriorityText, taskStatusColor, taskStatusText } from '../utils/format';

interface TaskFormValues {
  teamId?: number;
  assigneeId?: number;
  title?: string;
  description?: string;
  priority?: number;
  status?: number;
  deadline?: Dayjs;
}

interface TaskFilters {
  teamId?: number;
  userId?: number;
  status?: number;
  page: number;
  size: number;
}

const initialPage: PageResponse<Task> = {
  currentPage: 1,
  items: [],
  totalPages: 0,
  totalItems: 0,
  hasNext: false,
};

function toTaskPayload(values: TaskFormValues, includeTeam: boolean): TaskPayload {
  return compactObject({
    teamId: includeTeam ? values.teamId : undefined,
    assigneeId: values.assigneeId ?? null,
    title: values.title,
    description: values.description,
    priority: values.priority,
    status: values.status,
    deadline: values.deadline ? values.deadline.format('YYYY-MM-DDTHH:mm:ss') : null,
  });
}

function saveBlob(response: Awaited<ReturnType<typeof downloadAttachment>>, fallbackName: string) {
  const contentDisposition = response.headers['content-disposition'];
  const match = /filename\*=UTF-8''([^;]+)/.exec(contentDisposition || '');
  const fileName = match ? decodeURIComponent(match[1]) : fallbackName;
  const url = URL.createObjectURL(response.data);
  const link = document.createElement('a');
  link.href = url;
  link.download = fileName;
  link.click();
  URL.revokeObjectURL(url);
}

export default function Tasks() {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<'all' | 'mine' | 'user'>('all');
  const [filters, setFilters] = useState<TaskFilters>({ page: 0, size: 10 });
  const [result, setResult] = useState<PageResponse<Task>>(initialPage);
  const [loading, setLoading] = useState(false);
  const [teams, setTeams] = useState<TeamSummary[]>([]);
  const [members, setMembers] = useState<TeamMember[]>([]);
  const [me, setMe] = useState<UserProfile>();
  const [taskModalOpen, setTaskModalOpen] = useState(false);
  const [editingTask, setEditingTask] = useState<Task>();
  const [submitOpen, setSubmitOpen] = useState(false);
  const [reviewOpen, setReviewOpen] = useState(false);
  const [selectedTask, setSelectedTask] = useState<Task>();
  const [taskForm] = Form.useForm<TaskFormValues>();
  const [filterForm] = Form.useForm<{ teamId?: number; userId?: number; status?: number }>();
  const [submitForm] = Form.useForm<{ content: string }>();
  const [reviewForm] = Form.useForm<{ passed: boolean; feedback?: string }>();

  const memberOptions = useMemo(
    () =>
      members.map((member) => ({
        value: member.userId,
        label: `${member.nickName || member.realName || member.userId}（${member.userId}）`,
      })),
    [members],
  );

  const fetchTasks = useCallback(
    async (nextFilters = filters, tab = activeTab) => {
      setLoading(true);
      try {
        const params = compactObject({
          teamId: tab === 'all' && nextFilters.teamId && nextFilters.teamId > 0 ? nextFilters.teamId : undefined,
          status: nextFilters.status && nextFilters.status > 0 ? nextFilters.status : undefined,
          page: nextFilters.page,
          size: nextFilters.size,
        });
        if (tab === 'user') {
          if (!nextFilters.userId || nextFilters.userId <= 0) {
            setResult(initialPage);
            setFilters(nextFilters);
            return;
          }
          const data = await getUserTasks(nextFilters.userId, params);
          setResult(data);
          setFilters(nextFilters);
          return;
        }
        const data = tab === 'mine' ? await getMyTasks(params) : await listTasks(params);
        setResult(data);
        setFilters(nextFilters);
      } finally {
        setLoading(false);
      }
    },
    [activeTab, filters],
  );

  const loadMembers = async (teamId?: number) => {
    if (!teamId) {
      setMembers([]);
      return;
    }
    const data = await getMembers(teamId);
    setMembers(data);
  };

  useEffect(() => {
    Promise.allSettled([getMyTeams(), getMe()]).then(([teamResult, meResult]) => {
      if (teamResult.status === 'fulfilled') setTeams(teamResult.value.teams || []);
      if (meResult.status === 'fulfilled') setMe(meResult.value);
    });
    fetchTasks();
  }, []);

  const openCreate = () => {
    setEditingTask(undefined);
    setMembers([]);
    taskForm.resetFields();
    taskForm.setFieldsValue({ priority: 2 });
    setTaskModalOpen(true);
  };

  const openEdit = async (task: Task) => {
    setEditingTask(task);
    await loadMembers(task.teamId);
    taskForm.setFieldsValue({
      teamId: task.teamId,
      assigneeId: task.assigneeId || undefined,
      title: task.title,
      description: task.description || undefined,
      priority: task.priority,
      status: task.status,
      deadline: task.deadline ? dayjs(task.deadline) : undefined,
    });
    setTaskModalOpen(true);
  };

  const submitTaskForm = async () => {
    const values = await taskForm.validateFields();
    const payload = toTaskPayload(values, !editingTask);
    if (editingTask) {
      await updateTask(editingTask.taskId, payload);
      message.success('任务已更新');
    } else {
      await createTask(payload);
      message.success('任务已创建');
    }
    setTaskModalOpen(false);
    taskForm.resetFields();
    fetchTasks();
  };

  const handleSubmitTask = async () => {
    if (!selectedTask) return;
    const values = await submitForm.validateFields();
    await submitTask(selectedTask.taskId, values.content);
    message.success('任务已提交');
    setSubmitOpen(false);
    submitForm.resetFields();
    fetchTasks();
  };

  const handleReviewTask = async () => {
    if (!selectedTask) return;
    const values = await reviewForm.validateFields();
    await reviewTask(selectedTask.taskId, values);
    message.success(values.passed ? '审核已通过' : '任务已驳回');
    setReviewOpen(false);
    reviewForm.resetFields();
    fetchTasks();
  };

  const uploadProps = (task: Task): UploadProps => ({
    showUploadList: false,
    customRequest: async ({ file, onSuccess, onError }) => {
      try {
        await uploadAttachment(task.taskId, file as File);
        message.success('附件已上传');
        onSuccess?.({});
        fetchTasks();
      } catch (error) {
        onError?.(error as Error);
      }
    },
  });

  const columns: ColumnsType<Task> = [
    { title: '任务 ID', dataIndex: 'taskId', width: 100 },
    { title: '标题', dataIndex: 'title', ellipsis: true },
    { title: '小组 ID', dataIndex: 'teamId', width: 100 },
    { title: '负责人 ID', dataIndex: 'assigneeId', width: 110, render: (value?: number) => value || '-' },
    {
      title: '优先级',
      dataIndex: 'priority',
      width: 110,
      render: (priority: number) => <Tag color={taskPriorityColor[priority]}>{taskPriorityText[priority] || priority}</Tag>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 110,
      render: (status: number) => <Tag color={taskStatusColor[status]}>{taskStatusText[status] || status}</Tag>,
    },
    { title: '截止时间', dataIndex: 'deadline', render: formatDate },
    {
      title: '操作',
      width: 420,
      fixed: 'right',
      render: (_, record) => (
        <div className="table-actions">
          <Button size="small" icon={<EyeOutlined />} onClick={() => navigate(`/dashboard/tasks/${record.taskId}`)}>
            详情
          </Button>
          {record.status === 1 && (
            <Button
              size="small"
              type="primary"
              onClick={async () => {
                await applyTask(record.taskId);
                message.success('已领取任务');
                fetchTasks();
              }}
            >
              领取
            </Button>
          )}
          {me?.userId === record.assigneeId && record.status !== 3 && record.status !== 4 && (
            <Button
              size="small"
              onClick={async () => {
                await abandonTask(record.taskId);
                message.success('已放弃任务');
                fetchTasks();
              }}
            >
              放弃
            </Button>
          )}
          {(me?.userId === record.assigneeId || record.creatorId === me?.userId) && [2, 5].includes(record.status) && (
            <Button
              size="small"
              icon={<SendOutlined />}
              onClick={() => {
                setSelectedTask(record);
                submitForm.resetFields();
                setSubmitOpen(true);
              }}
            >
              提交
            </Button>
          )}
          {record.status === 3 && (
            <Button
              size="small"
              icon={<CheckCircleOutlined />}
              onClick={() => {
                setSelectedTask(record);
                reviewForm.setFieldsValue({ passed: true });
                setReviewOpen(true);
              }}
            >
              审核
            </Button>
          )}
          <Button size="small" icon={<EditOutlined />} onClick={() => openEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title="删除任务"
            description={`确认删除任务「${record.title}」？`}
            onConfirm={async () => {
              await deleteTask(record.taskId);
              message.success('任务已删除');
              fetchTasks();
            }}
          >
            <Button size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
          <Upload {...uploadProps(record)}>
            <Button size="small" icon={<UploadOutlined />}>
              上传
            </Button>
          </Upload>
          {record.attachmentName && (
            <Button
              size="small"
              icon={<DownloadOutlined />}
              onClick={async () => saveBlob(await downloadAttachment(record.taskId), record.attachmentName || 'attachment')}
            >
              下载
            </Button>
          )}
        </div>
      ),
    },
  ];

  const handleFilter = (values: { teamId?: number; userId?: number; status?: number }) => {
    fetchTasks({ ...filters, ...values, page: 0 });
  };

  const handlePageChange = (pagination: TablePaginationConfig) => {
    fetchTasks({
      ...filters,
      page: (pagination.current || 1) - 1,
      size: pagination.pageSize || 10,
    });
  };

  return (
    <div className="page-stack">
      <div className="page-toolbar">
        <div>
          <Typography.Title level={3} style={{ margin: 0 }}>
            任务中心
          </Typography.Title>
          <Typography.Text type="secondary">小组任务、我的任务和任务审核</Typography.Text>
        </div>
        <Space wrap>
          <Button icon={<ReloadOutlined />} loading={loading} onClick={() => fetchTasks()}>
            刷新
          </Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>
            创建任务
          </Button>
        </Space>
      </div>

      <Card>
        <Form form={filterForm} layout="inline" onFinish={handleFilter} style={{ marginBottom: 16 }}>
          {activeTab === 'all' && (
            <Form.Item name="teamId" label="小组">
              <Select
                allowClear
                placeholder="全部小组"
                style={{ width: 180 }}
                options={teams.map((team) => ({ value: team.teamId, label: `${team.teamName}（${team.teamId}）` }))}
              />
            </Form.Item>
          )}
          {activeTab === 'user' && (
            <Form.Item name="userId" label="用户 ID" rules={[{ required: true, message: '请输入用户 ID' }]}>
              <InputNumber min={1} placeholder="输入用户 ID" />
            </Form.Item>
          )}
          <Form.Item name="status" label="状态">
            <Select
              allowClear
              placeholder="全部状态"
              style={{ width: 160 }}
              options={Object.entries(taskStatusText).map(([value, label]) => ({ value: Number(value), label }))}
            />
          </Form.Item>
          <Button type="primary" htmlType="submit">
            查询
          </Button>
        </Form>
        <Tabs
          activeKey={activeTab}
          onChange={(key) => {
            const nextTab = key as 'all' | 'mine' | 'user';
            setActiveTab(nextTab);
            fetchTasks({ ...filters, page: 0 }, nextTab);
          }}
          items={[
            { key: 'all', label: '任务列表' },
            { key: 'mine', label: '我的任务' },
            { key: 'user', label: '用户任务' },
          ]}
        />
        <Table
          rowKey="taskId"
          loading={loading}
          columns={columns}
          dataSource={result.items}
          onChange={handlePageChange}
          scroll={{ x: 1300 }}
          pagination={{
            current: result.currentPage || 1,
            total: result.totalItems,
            pageSize: filters.size,
            showSizeChanger: true,
          }}
        />
      </Card>

      <Modal
        title={editingTask ? '编辑任务' : '创建任务'}
        open={taskModalOpen}
        onCancel={() => setTaskModalOpen(false)}
        onOk={submitTaskForm}
        destroyOnClose
      >
        <Form form={taskForm} layout="vertical">
          {!editingTask && (
            <Form.Item name="teamId" label="小组" rules={[{ required: true, message: '请选择小组' }]}>
              <Select
                placeholder="选择小组"
                options={teams.map((team) => ({ value: team.teamId, label: `${team.teamName}（${team.teamId}）` }))}
                onChange={(teamId: number) => {
                  taskForm.setFieldValue('assigneeId', undefined);
                  loadMembers(teamId);
                }}
              />
            </Form.Item>
          )}
          <Form.Item name="assigneeId" label="负责人">
            <Select allowClear placeholder="不选择则任务待领取" options={memberOptions} />
          </Form.Item>
          <Form.Item name="title" label="标题" rules={[{ required: true, message: '请输入任务标题' }]}>
            <Input maxLength={100} showCount />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={4} maxLength={1000} showCount />
          </Form.Item>
          <Form.Item name="priority" label="优先级" initialValue={2}>
            <Select options={Object.entries(taskPriorityText).map(([value, label]) => ({ value: Number(value), label }))} />
          </Form.Item>
          {editingTask && (
            <Form.Item name="status" label="状态">
              <Select options={Object.entries(taskStatusText).map(([value, label]) => ({ value: Number(value), label }))} />
            </Form.Item>
          )}
          <Form.Item name="deadline" label="截止时间">
            <DatePicker showTime style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal title="提交任务" open={submitOpen} onCancel={() => setSubmitOpen(false)} onOk={handleSubmitTask} destroyOnClose>
        <Form form={submitForm} layout="vertical">
          <Form.Item name="content" label="提交内容" rules={[{ required: true, message: '请输入提交内容' }]}>
            <Input.TextArea rows={5} maxLength={1000} showCount />
          </Form.Item>
        </Form>
      </Modal>

      <Modal title="审核任务" open={reviewOpen} onCancel={() => setReviewOpen(false)} onOk={handleReviewTask} destroyOnClose>
        <Form form={reviewForm} layout="vertical" initialValues={{ passed: true }}>
          <Form.Item name="passed" label="审核结果" rules={[{ required: true, message: '请选择审核结果' }]}>
            <Select
              options={[
                { value: true, label: '通过' },
                { value: false, label: '驳回' },
              ]}
            />
          </Form.Item>
          <Form.Item name="feedback" label="审核反馈">
            <Input.TextArea rows={4} maxLength={500} showCount />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
