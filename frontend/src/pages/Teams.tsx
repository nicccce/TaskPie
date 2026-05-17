import { PlusOutlined, ReloadOutlined, SearchOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, Modal, Space, Table, Tabs, Typography, message } from 'antd';
import type { ColumnsType, TablePaginationConfig } from 'antd/es/table';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { applyTeam, createTeam, getMyTeams, selectTeams } from '../api/team';
import type { PageResponse, Team, TeamSummary } from '../types';
import { formatDate } from '../utils/format';

interface CreateTeamForm {
  name: string;
  description?: string;
}

export default function Teams() {
  const navigate = useNavigate();
  const [teams, setTeams] = useState<TeamSummary[]>([]);
  const [myLoading, setMyLoading] = useState(false);
  const [createOpen, setCreateOpen] = useState(false);
  const [createForm] = Form.useForm<CreateTeamForm>();
  const [searchForm] = Form.useForm<{ keyword: string }>();
  const [keyword, setKeyword] = useState('');
  const [searchLoading, setSearchLoading] = useState(false);
  const [searchResult, setSearchResult] = useState<PageResponse<Team>>({
    currentPage: 1,
    items: [],
    totalPages: 0,
    totalItems: 0,
    hasNext: false,
  });

  const fetchMyTeams = async () => {
    setMyLoading(true);
    try {
      const data = await getMyTeams();
      setTeams(data.teams || []);
    } finally {
      setMyLoading(false);
    }
  };

  const fetchSearch = async (nextKeyword = keyword, page = 0, size = 10) => {
    if (!nextKeyword.trim()) {
      setSearchResult((current) => ({ ...current, items: [], totalItems: 0 }));
      return;
    }
    setSearchLoading(true);
    try {
      const data = await selectTeams({ keyword: nextKeyword.trim(), page, size });
      setSearchResult(data);
      setKeyword(nextKeyword.trim());
    } finally {
      setSearchLoading(false);
    }
  };

  useEffect(() => {
    fetchMyTeams();
  }, []);

  const submitCreate = async () => {
    const values = await createForm.validateFields();
    const teamId = await createTeam(values);
    message.success('小组已创建');
    setCreateOpen(false);
    createForm.resetFields();
    await fetchMyTeams();
    navigate(`/dashboard/teams/${teamId}`);
  };

  const myColumns: ColumnsType<TeamSummary> = [
    { title: '小组 ID', dataIndex: 'teamId', width: 120 },
    { title: '小组名称', dataIndex: 'teamName' },
    {
      title: '操作',
      width: 160,
      render: (_, record) => (
        <Button type="link" onClick={() => navigate(`/dashboard/teams/${record.teamId}`)}>
          查看详情
        </Button>
      ),
    },
  ];

  const searchColumns: ColumnsType<Team> = [
    { title: '小组 ID', dataIndex: 'teamId', width: 120 },
    { title: '名称', dataIndex: 'name' },
    { title: '描述', dataIndex: 'description', ellipsis: true },
    { title: '组长 ID', dataIndex: 'leaderId', width: 120 },
    { title: '创建时间', dataIndex: 'createdAt', render: formatDate },
    {
      title: '操作',
      width: 220,
      render: (_, record) => (
        <Space>
          <Button type="link" onClick={() => navigate(`/dashboard/teams/${record.teamId}`)}>
            详情
          </Button>
          <Button
            type="link"
            onClick={async () => {
              await applyTeam(record.teamId);
              message.success('加入申请已发送');
            }}
          >
            申请加入
          </Button>
        </Space>
      ),
    },
  ];

  const handleSearchPageChange = (pagination: TablePaginationConfig) => {
    fetchSearch(keyword, (pagination.current || 1) - 1, pagination.pageSize || 10);
  };

  return (
    <div className="page-stack">
      <div className="page-toolbar">
        <div>
          <Typography.Title level={3} style={{ margin: 0 }}>
            我的小组
          </Typography.Title>
          <Typography.Text type="secondary">创建、搜索和管理课程协作小组</Typography.Text>
        </div>
        <Space wrap>
          <Button icon={<ReloadOutlined />} onClick={fetchMyTeams} loading={myLoading}>
            刷新
          </Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => setCreateOpen(true)}>
            创建小组
          </Button>
        </Space>
      </div>

      <Tabs
        items={[
          {
            key: 'mine',
            label: '我的团队',
            children: (
              <Card>
                <Table rowKey="teamId" loading={myLoading} columns={myColumns} dataSource={teams} pagination={false} />
              </Card>
            ),
          },
          {
            key: 'search',
            label: '搜索小组',
            children: (
              <Card>
                <Form
                  form={searchForm}
                  layout="inline"
                  onFinish={(values) => fetchSearch(values.keyword, 0, 10)}
                  style={{ marginBottom: 16 }}
                >
                  <Form.Item name="keyword" rules={[{ required: true, message: '请输入关键字' }]}>
                    <Input allowClear placeholder="输入小组名称" />
                  </Form.Item>
                  <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                    搜索
                  </Button>
                </Form>
                <Table
                  rowKey="teamId"
                  loading={searchLoading}
                  columns={searchColumns}
                  dataSource={searchResult.items}
                  onChange={handleSearchPageChange}
                  pagination={{
                    current: searchResult.currentPage || 1,
                    total: searchResult.totalItems,
                    pageSize: 10,
                    showSizeChanger: true,
                  }}
                />
              </Card>
            ),
          },
        ]}
      />

      <Modal title="创建小组" open={createOpen} onCancel={() => setCreateOpen(false)} onOk={submitCreate} destroyOnClose>
        <Form form={createForm} layout="vertical">
          <Form.Item name="name" label="小组名称" rules={[{ required: true, message: '请输入小组名称' }]}>
            <Input maxLength={20} showCount />
          </Form.Item>
          <Form.Item name="description" label="小组描述">
            <Input.TextArea rows={4} maxLength={500} showCount />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
