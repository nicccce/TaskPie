import { CheckOutlined, CloseOutlined, ReloadOutlined } from '@ant-design/icons';
import { Button, Card, Descriptions, Form, InputNumber, Modal, Select, Space, Table, Tag, Typography, message } from 'antd';
import type { ColumnsType, TablePaginationConfig } from 'antd/es/table';
import { useEffect, useState } from 'react';
import { getNotice, handleNotice, selectNotices } from '../api/notice';
import type { Notice, PageResponse } from '../types';
import { formatDate, noticeStatusText, noticeTypeText } from '../utils/format';

const noticeFilterOptions = [
  { value: 0, label: '全部' },
  { value: 1, label: '系统/个人通知' },
  { value: 2, label: '小组通知' },
  { value: 3, label: '邀请/申请通知' },
];

function isActionable(notice?: Notice) {
  return !!notice && (notice.type === 30 || notice.type === 31) && notice.status === 1;
}

export default function Notices() {
  const [form] = Form.useForm<{ type: number; teamId?: number }>();
  const [query, setQuery] = useState({ type: 0, teamId: 0, page: 0, size: 10 });
  const [loading, setLoading] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detail, setDetail] = useState<Notice>();
  const [detailOpen, setDetailOpen] = useState(false);
  const [result, setResult] = useState<PageResponse<Notice>>({
    currentPage: 1,
    items: [],
    totalPages: 0,
    totalItems: 0,
    hasNext: false,
  });

  const fetchNotices = async (nextQuery = query) => {
    setLoading(true);
    try {
      const data = await selectNotices(nextQuery);
      setResult(data);
      setQuery(nextQuery);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotices();
  }, []);

  const openDetail = async (noticeId: number) => {
    setDetailOpen(true);
    setDetailLoading(true);
    try {
      const data = await getNotice(noticeId);
      setDetail(data);
    } finally {
      setDetailLoading(false);
    }
  };

  const handleAction = async (notice: Notice, accept: boolean) => {
    await handleNotice(notice.noticeId, {
      accept,
      feedback: accept ? '同意加入' : '拒绝处理',
    });
    message.success(accept ? '已同意' : '已拒绝');
    setDetailOpen(false);
    fetchNotices();
  };

  const columns: ColumnsType<Notice> = [
    { title: '通知 ID', dataIndex: 'noticeId', width: 110 },
    {
      title: '类型',
      dataIndex: 'type',
      width: 140,
      render: (type: number) => <Tag color={type >= 30 ? 'purple' : type >= 20 ? 'blue' : 'default'}>{noticeTypeText[type] || type}</Tag>,
    },
    {
      title: '标题',
      dataIndex: 'title',
      render: (title: string, record) => (
        <Button type="link" onClick={() => openDetail(record.noticeId)}>
          {title}
        </Button>
      ),
    },
    { title: '小组 ID', dataIndex: 'teamId', width: 100, render: (value?: number) => value || '-' },
    {
      title: '状态',
      dataIndex: 'status',
      width: 110,
      render: (status?: number) => (status ? <Tag>{noticeStatusText[status] || status}</Tag> : '-'),
    },
    { title: '已读', dataIndex: 'hasRead', width: 80, render: (value?: boolean) => (value ? '是' : '否') },
    { title: '创建时间', dataIndex: 'createdAt', render: formatDate },
    {
      title: '操作',
      width: 220,
      render: (_, record) => (
        <Space>
          <Button type="link" onClick={() => openDetail(record.noticeId)}>
            详情
          </Button>
          {isActionable(record) && (
            <>
              <Button type="link" icon={<CheckOutlined />} onClick={() => handleAction(record, true)}>
                同意
              </Button>
              <Button danger type="link" icon={<CloseOutlined />} onClick={() => handleAction(record, false)}>
                拒绝
              </Button>
            </>
          )}
        </Space>
      ),
    },
  ];

  const handlePageChange = (pagination: TablePaginationConfig) => {
    fetchNotices({
      ...query,
      page: (pagination.current || 1) - 1,
      size: pagination.pageSize || 10,
    });
  };

  return (
    <div className="page-stack">
      <div className="page-toolbar">
        <div>
          <Typography.Title level={3} style={{ margin: 0 }}>
            通知中心
          </Typography.Title>
          <Typography.Text type="secondary">系统通知、小组通知和邀请申请处理</Typography.Text>
        </div>
        <Button icon={<ReloadOutlined />} onClick={() => fetchNotices()} loading={loading}>
          刷新
        </Button>
      </div>

      <Card>
        <Form
          form={form}
          layout="inline"
          initialValues={{ type: 0, teamId: 0 }}
          onFinish={(values) => fetchNotices({ type: values.type, teamId: values.teamId || 0, page: 0, size: query.size })}
          style={{ marginBottom: 16 }}
        >
          <Form.Item name="type" label="类型">
            <Select options={noticeFilterOptions} style={{ width: 180 }} />
          </Form.Item>
          <Form.Item name="teamId" label="小组 ID">
            <InputNumber min={0} placeholder="0 为不限" />
          </Form.Item>
          <Button type="primary" htmlType="submit">
            查询
          </Button>
        </Form>
        <Table
          rowKey="noticeId"
          loading={loading}
          columns={columns}
          dataSource={result.items}
          onChange={handlePageChange}
          pagination={{
            current: result.currentPage || 1,
            total: result.totalItems,
            pageSize: query.size,
            showSizeChanger: true,
          }}
        />
      </Card>

      <Modal
        title="通知详情"
        open={detailOpen}
        onCancel={() => setDetailOpen(false)}
        footer={
          isActionable(detail) ? (
            <Space>
              <Button onClick={() => setDetailOpen(false)}>关闭</Button>
              <Button danger icon={<CloseOutlined />} onClick={() => detail && handleAction(detail, false)}>
                拒绝
              </Button>
              <Button type="primary" icon={<CheckOutlined />} onClick={() => detail && handleAction(detail, true)}>
                同意
              </Button>
            </Space>
          ) : (
            <Button onClick={() => setDetailOpen(false)}>关闭</Button>
          )
        }
      >
        <Card loading={detailLoading} bordered={false}>
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="标题">{detail?.title || '-'}</Descriptions.Item>
            <Descriptions.Item label="类型">{detail ? noticeTypeText[detail.type] || detail.type : '-'}</Descriptions.Item>
            <Descriptions.Item label="状态">{detail?.status ? noticeStatusText[detail.status] || detail.status : '-'}</Descriptions.Item>
            <Descriptions.Item label="发送者 ID">{detail?.senderId || '-'}</Descriptions.Item>
            <Descriptions.Item label="接收者 ID">{detail?.receiverId || '-'}</Descriptions.Item>
            <Descriptions.Item label="小组 ID">{detail?.teamId || '-'}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{formatDate(detail?.createdAt)}</Descriptions.Item>
            <Descriptions.Item label="内容">{detail?.content || '-'}</Descriptions.Item>
          </Descriptions>
        </Card>
      </Modal>
    </div>
  );
}
