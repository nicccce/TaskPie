import {
  ArrowLeftOutlined,
  CheckCircleOutlined,
  DownloadOutlined,
  ReloadOutlined,
  SendOutlined,
  UploadOutlined,
} from '@ant-design/icons';
import { Button, Card, Descriptions, Form, Input, Modal, Select, Space, Tag, Upload, Typography, message } from 'antd';
import type { UploadProps } from 'antd';
import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  abandonTask,
  applyTask,
  downloadAttachment,
  getTask,
  reviewTask,
  submitTask,
  uploadAttachment,
} from '../api/task';
import { getMe } from '../api/user';
import type { Task, UserProfile } from '../types';
import { formatDate, taskPriorityColor, taskPriorityText, taskStatusColor, taskStatusText } from '../utils/format';

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

export default function TaskDetail() {
  const { taskId: taskIdParam } = useParams();
  const taskId = Number(taskIdParam);
  const navigate = useNavigate();
  const [task, setTask] = useState<Task>();
  const [me, setMe] = useState<UserProfile>();
  const [loading, setLoading] = useState(false);
  const [submitOpen, setSubmitOpen] = useState(false);
  const [reviewOpen, setReviewOpen] = useState(false);
  const [submitForm] = Form.useForm<{ content: string }>();
  const [reviewForm] = Form.useForm<{ passed: boolean; feedback?: string }>();

  const fetchDetail = useCallback(async () => {
    if (!taskId) return;
    setLoading(true);
    try {
      const [taskData, meData] = await Promise.all([getTask(taskId), getMe()]);
      setTask(taskData);
      setMe(meData);
    } finally {
      setLoading(false);
    }
  }, [taskId]);

  useEffect(() => {
    fetchDetail();
  }, [fetchDetail]);

  const uploadProps: UploadProps = {
    showUploadList: false,
    customRequest: async ({ file, onSuccess, onError }) => {
      if (!task) return;
      try {
        await uploadAttachment(task.taskId, file as File);
        message.success('附件已上传');
        onSuccess?.({});
        fetchDetail();
      } catch (error) {
        onError?.(error as Error);
      }
    },
  };

  const submitCurrentTask = async () => {
    if (!task) return;
    const values = await submitForm.validateFields();
    await submitTask(task.taskId, values.content);
    message.success('任务已提交');
    setSubmitOpen(false);
    submitForm.resetFields();
    fetchDetail();
  };

  const reviewCurrentTask = async () => {
    if (!task) return;
    const values = await reviewForm.validateFields();
    await reviewTask(task.taskId, values);
    message.success(values.passed ? '审核已通过' : '任务已驳回');
    setReviewOpen(false);
    reviewForm.resetFields();
    fetchDetail();
  };

  return (
    <div className="page-stack">
      <div className="page-toolbar">
        <Space>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/dashboard/tasks')}>
            返回
          </Button>
          <div>
            <Typography.Title level={3} style={{ margin: 0 }}>
              {task?.title || '任务详情'}
            </Typography.Title>
            <Typography.Text type="secondary">任务内容、提交和审核记录</Typography.Text>
          </div>
        </Space>
        <Space wrap>
          <Button icon={<ReloadOutlined />} onClick={fetchDetail} loading={loading}>
            刷新
          </Button>
          {task?.status === 1 && (
            <Button
              type="primary"
              onClick={async () => {
                await applyTask(task.taskId);
                message.success('已领取任务');
                fetchDetail();
              }}
            >
              领取
            </Button>
          )}
          {task && me?.userId === task.assigneeId && task.status !== 3 && task.status !== 4 && (
            <Button
              onClick={async () => {
                await abandonTask(task.taskId);
                message.success('已放弃任务');
                fetchDetail();
              }}
            >
              放弃
            </Button>
          )}
          {task && (me?.userId === task.assigneeId || me?.userId === task.creatorId) && [2, 5].includes(task.status) && (
            <Button icon={<SendOutlined />} onClick={() => setSubmitOpen(true)}>
              提交
            </Button>
          )}
          {task?.status === 3 && (
            <Button
              icon={<CheckCircleOutlined />}
              onClick={() => {
                reviewForm.setFieldsValue({ passed: true });
                setReviewOpen(true);
              }}
            >
              审核
            </Button>
          )}
          {task && (
            <Upload {...uploadProps}>
              <Button icon={<UploadOutlined />}>上传附件</Button>
            </Upload>
          )}
          {task?.attachmentName && (
            <Button
              icon={<DownloadOutlined />}
              onClick={async () => saveBlob(await downloadAttachment(task.taskId), task.attachmentName || 'attachment')}
            >
              下载附件
            </Button>
          )}
        </Space>
      </div>

      <Card loading={loading}>
        <Descriptions bordered column={{ xs: 1, md: 2 }}>
          <Descriptions.Item label="任务 ID">{task?.taskId || '-'}</Descriptions.Item>
          <Descriptions.Item label="小组 ID">{task?.teamId || '-'}</Descriptions.Item>
          <Descriptions.Item label="创建者 ID">{task?.creatorId || '-'}</Descriptions.Item>
          <Descriptions.Item label="负责人 ID">{task?.assigneeId || '-'}</Descriptions.Item>
          <Descriptions.Item label="优先级">
            {task ? <Tag color={taskPriorityColor[task.priority]}>{taskPriorityText[task.priority] || task.priority}</Tag> : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="状态">
            {task ? <Tag color={taskStatusColor[task.status]}>{taskStatusText[task.status] || task.status}</Tag> : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="截止时间">{formatDate(task?.deadline)}</Descriptions.Item>
          <Descriptions.Item label="附件">{task?.attachmentName || '-'}</Descriptions.Item>
          <Descriptions.Item label="创建时间">{formatDate(task?.createdAt)}</Descriptions.Item>
          <Descriptions.Item label="更新时间">{formatDate(task?.updatedAt)}</Descriptions.Item>
          <Descriptions.Item label="任务描述" span={2}>
            {task?.description || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="提交内容" span={2}>
            {task?.submitContent || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="审核反馈" span={2}>
            {task?.reviewFeedback || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="提交时间">{formatDate(task?.submittedAt)}</Descriptions.Item>
          <Descriptions.Item label="审核时间">{formatDate(task?.reviewedAt)}</Descriptions.Item>
        </Descriptions>
      </Card>

      <Modal title="提交任务" open={submitOpen} onCancel={() => setSubmitOpen(false)} onOk={submitCurrentTask} destroyOnClose>
        <Form form={submitForm} layout="vertical">
          <Form.Item name="content" label="提交内容" rules={[{ required: true, message: '请输入提交内容' }]}>
            <Input.TextArea rows={5} maxLength={1000} showCount />
          </Form.Item>
        </Form>
      </Modal>

      <Modal title="审核任务" open={reviewOpen} onCancel={() => setReviewOpen(false)} onOk={reviewCurrentTask} destroyOnClose>
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
