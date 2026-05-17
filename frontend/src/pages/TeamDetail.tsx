import { ArrowLeftOutlined, ReloadOutlined, SearchOutlined, UserAddOutlined } from '@ant-design/icons';
import { Button, Card, Descriptions, Form, Input, Modal, Popconfirm, Space, Table, Tag, Typography, message } from 'antd';
import type { ColumnsType, TablePaginationConfig } from 'antd/es/table';
import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getUser, selectUsers } from '../api/user';
import { deleteTeam, getMembers, getTeam, inviteMember, quitTeam, removeMembers, transferLeader } from '../api/team';
import type { PageResponse, Team, TeamMember, UserProfile, UserSearchItem } from '../types';
import { formatDate, roleText, teamRoleText } from '../utils/format';

export default function TeamDetail() {
  const { teamId: teamIdParam } = useParams();
  const teamId = Number(teamIdParam);
  const navigate = useNavigate();
  const [team, setTeam] = useState<Team>();
  const [members, setMembers] = useState<TeamMember[]>([]);
  const [loading, setLoading] = useState(false);
  const [inviteOpen, setInviteOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const [profileLoading, setProfileLoading] = useState(false);
  const [selectedUser, setSelectedUser] = useState<UserProfile>();
  const [inviteKeyword, setInviteKeyword] = useState('');
  const [inviteLoading, setInviteLoading] = useState(false);
  const [inviteForm] = Form.useForm<{ keyword: string }>();
  const [users, setUsers] = useState<PageResponse<UserSearchItem>>({
    currentPage: 1,
    items: [],
    totalPages: 0,
    totalItems: 0,
    hasNext: false,
  });

  const fetchDetail = useCallback(async () => {
    if (!teamId) return;
    setLoading(true);
    try {
      const [teamData, memberData] = await Promise.all([getTeam(teamId), getMembers(teamId)]);
      setTeam(teamData);
      setMembers(memberData);
    } finally {
      setLoading(false);
    }
  }, [teamId]);

  useEffect(() => {
    fetchDetail();
  }, [fetchDetail]);

  const fetchUsers = async (keyword = inviteKeyword, page = 0, size = 10) => {
    if (!keyword.trim()) return;
    setInviteLoading(true);
    try {
      const data = await selectUsers({ keyword: keyword.trim(), page, size });
      setUsers(data);
      setInviteKeyword(keyword.trim());
    } finally {
      setInviteLoading(false);
    }
  };

  const openUserProfile = async (userId: number) => {
    setProfileOpen(true);
    setProfileLoading(true);
    try {
      const data = await getUser(userId);
      setSelectedUser(data);
    } finally {
      setProfileLoading(false);
    }
  };

  const memberColumns: ColumnsType<TeamMember> = [
    { title: '用户 ID', dataIndex: 'userId', width: 100 },
    { title: '学号', dataIndex: 'studentId' },
    { title: '真实姓名', dataIndex: 'realName' },
    { title: '昵称', dataIndex: 'nickName' },
    {
      title: '角色',
      dataIndex: 'role',
      width: 100,
      render: (role: number) => <Tag color={role === 10 ? 'gold' : 'blue'}>{teamRoleText[role] || role}</Tag>,
    },
    { title: '加入时间', dataIndex: 'joinedAt', render: formatDate },
    {
      title: '操作',
      width: 300,
      render: (_, record) => (
        <Space>
          <Button type="link" onClick={() => openUserProfile(record.userId)}>
            资料
          </Button>
          <Popconfirm
            title="移除成员"
            description={`确认移除 ${record.nickName || record.realName || record.userId}？`}
            onConfirm={async () => {
              await removeMembers(teamId, [record.userId]);
              message.success('成员已移除');
              fetchDetail();
            }}
            disabled={team?.leaderId === record.userId}
          >
            <Button danger type="link" disabled={team?.leaderId === record.userId}>
              移除
            </Button>
          </Popconfirm>
          <Popconfirm
            title="转让组长"
            description={`确认将组长转让给 ${record.nickName || record.realName || record.userId}？`}
            onConfirm={async () => {
              await transferLeader(teamId, record.userId);
              message.success('组长已转让');
              fetchDetail();
            }}
            disabled={team?.leaderId === record.userId}
          >
            <Button type="link" disabled={team?.leaderId === record.userId}>
              转让组长
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const userColumns: ColumnsType<UserSearchItem> = [
    { title: '用户 ID', dataIndex: 'userId', width: 100 },
    { title: '学号', dataIndex: 'studentId' },
    { title: '真实姓名', dataIndex: 'realName' },
    { title: '昵称', dataIndex: 'nickName' },
    {
      title: '操作',
      width: 120,
      render: (_, record) => (
        <Space>
          <Button type="link" onClick={() => openUserProfile(record.userId)}>
            资料
          </Button>
          <Button
            type="link"
            onClick={async () => {
              await inviteMember(teamId, record.userId);
              message.success('邀请已发送');
            }}
          >
            邀请
          </Button>
        </Space>
      ),
    },
  ];

  const handleInvitePageChange = (pagination: TablePaginationConfig) => {
    fetchUsers(inviteKeyword, (pagination.current || 1) - 1, pagination.pageSize || 10);
  };

  return (
    <div className="page-stack">
      <div className="page-toolbar">
        <Space>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/dashboard/teams')}>
            返回
          </Button>
          <div>
            <Typography.Title level={3} style={{ margin: 0 }}>
              {team?.name || '小组详情'}
            </Typography.Title>
            <Typography.Text type="secondary">小组成员与邀请管理</Typography.Text>
          </div>
        </Space>
        <Space wrap>
          <Button icon={<ReloadOutlined />} onClick={fetchDetail} loading={loading}>
            刷新
          </Button>
          <Button icon={<UserAddOutlined />} type="primary" onClick={() => setInviteOpen(true)}>
            邀请成员
          </Button>
          <Popconfirm
            title="解散小组"
            description="确认删除当前小组？只有组长可以执行。"
            onConfirm={async () => {
              await deleteTeam(teamId);
              message.success('小组已删除');
              navigate('/dashboard/teams');
            }}
          >
            <Button danger>解散小组</Button>
          </Popconfirm>
          <Popconfirm
            title="退出小组"
            description="确认退出当前小组？组长需要先转让组长后再退出。"
            onConfirm={async () => {
              await quitTeam(teamId);
              message.success('已退出小组');
              navigate('/dashboard/teams');
            }}
          >
            <Button danger>退出小组</Button>
          </Popconfirm>
        </Space>
      </div>

      <Card loading={loading}>
        <Descriptions bordered column={{ xs: 1, md: 2 }}>
          <Descriptions.Item label="小组 ID">{team?.teamId || '-'}</Descriptions.Item>
          <Descriptions.Item label="小组名称">{team?.name || '-'}</Descriptions.Item>
          <Descriptions.Item label="组长 ID">{team?.leaderId || '-'}</Descriptions.Item>
          <Descriptions.Item label="创建时间">{formatDate(team?.createdAt)}</Descriptions.Item>
          <Descriptions.Item label="更新时间">{formatDate(team?.updatedAt)}</Descriptions.Item>
          <Descriptions.Item label="描述" span={2}>
            {team?.description || '-'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="成员列表">
        <Table rowKey="userId" loading={loading} columns={memberColumns} dataSource={members} pagination={false} />
      </Card>

      <Modal
        title="邀请成员"
        width={760}
        open={inviteOpen}
        onCancel={() => setInviteOpen(false)}
        footer={null}
        destroyOnClose
      >
        <Form
          form={inviteForm}
          layout="inline"
          onFinish={(values) => fetchUsers(values.keyword, 0, 10)}
          style={{ marginBottom: 16 }}
        >
          <Form.Item name="keyword" rules={[{ required: true, message: '请输入用户关键字' }]}>
            <Input allowClear placeholder="用户名、姓名或学号" />
          </Form.Item>
          <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
            搜索用户
          </Button>
        </Form>
        <Table
          rowKey="userId"
          loading={inviteLoading}
          columns={userColumns}
          dataSource={users.items}
          onChange={handleInvitePageChange}
          pagination={{
            current: users.currentPage || 1,
            total: users.totalItems,
            pageSize: 10,
            showSizeChanger: true,
          }}
        />
      </Modal>

      <Modal title="用户资料" open={profileOpen} onCancel={() => setProfileOpen(false)} footer={null} destroyOnClose>
        <Card loading={profileLoading} bordered={false}>
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="用户 ID">{selectedUser?.userId || '-'}</Descriptions.Item>
            <Descriptions.Item label="学号">{selectedUser?.studentId || '-'}</Descriptions.Item>
            <Descriptions.Item label="真实姓名">{selectedUser?.realName || '-'}</Descriptions.Item>
            <Descriptions.Item label="昵称">{selectedUser?.nickName || '-'}</Descriptions.Item>
            <Descriptions.Item label="角色">
              {selectedUser ? roleText[selectedUser.role] || selectedUser.role : '-'}
            </Descriptions.Item>
            <Descriptions.Item label="简介">{selectedUser?.bio || '-'}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{formatDate(selectedUser?.createdAt)}</Descriptions.Item>
          </Descriptions>
        </Card>
      </Modal>
    </div>
  );
}
