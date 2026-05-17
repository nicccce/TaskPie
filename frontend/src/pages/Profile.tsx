import { EditOutlined, KeyOutlined, ReloadOutlined } from '@ant-design/icons';
import { Button, Card, Descriptions, Form, Input, Modal, Popconfirm, Space, Typography, message } from 'antd';
import { useEffect, useState } from 'react';
import { closeAccount, updatePassword } from '../api/auth';
import { getMe, updateMe } from '../api/user';
import type { UserProfile } from '../types';
import { formatDate, roleText } from '../utils/format';
import { clearAuth, updateNickName } from '../utils/auth';
import { useNavigate } from 'react-router-dom';

interface PasswordForm {
  oldPassword: string;
  newPassword: string;
}

export default function Profile() {
  const navigate = useNavigate();
  const [profile, setProfile] = useState<UserProfile>();
  const [loading, setLoading] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [passwordOpen, setPasswordOpen] = useState(false);
  const [closeOpen, setCloseOpen] = useState(false);
  const [editForm] = Form.useForm<UserProfile>();
  const [passwordForm] = Form.useForm<PasswordForm>();
  const [closeForm] = Form.useForm<{ password: string }>();

  const fetchProfile = async () => {
    setLoading(true);
    try {
      const data = await getMe();
      setProfile(data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  const openEdit = () => {
    if (profile) {
      editForm.setFieldsValue(profile);
    }
    setEditOpen(true);
  };

  const submitEdit = async () => {
    const values = await editForm.validateFields();
    await updateMe({
      studentId: values.studentId,
      realName: values.realName,
      nickName: values.nickName,
      bio: values.bio,
    });
    updateNickName(values.nickName);
    message.success('资料已更新');
    setEditOpen(false);
    fetchProfile();
  };

  const submitPassword = async () => {
    const values = await passwordForm.validateFields();
    await updatePassword(values);
    message.success('密码已修改');
    setPasswordOpen(false);
    passwordForm.resetFields();
  };

  const submitCloseAccount = async () => {
    const values = await closeForm.validateFields();
    await closeAccount(values.password);
    message.success('账号已注销');
    clearAuth();
    navigate('/login', { replace: true });
  };

  return (
    <div className="page-stack">
      <div className="page-toolbar">
        <div>
          <Typography.Title level={3} style={{ margin: 0 }}>
            个人中心
          </Typography.Title>
          <Typography.Text type="secondary">当前登录用户资料</Typography.Text>
        </div>
        <Space wrap>
          <Button icon={<ReloadOutlined />} onClick={fetchProfile} loading={loading}>
            刷新
          </Button>
          <Button icon={<KeyOutlined />} onClick={() => setPasswordOpen(true)}>
            修改密码
          </Button>
          <Button type="primary" icon={<EditOutlined />} onClick={openEdit}>
            编辑资料
          </Button>
          <Button danger onClick={() => setCloseOpen(true)}>
            注销账号
          </Button>
        </Space>
      </div>
      <Card loading={loading}>
        <Descriptions bordered column={{ xs: 1, md: 2 }}>
          <Descriptions.Item label="用户 ID">{profile?.userId ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="学号">{profile?.studentId || '-'}</Descriptions.Item>
          <Descriptions.Item label="用户名">{profile?.userName || '-'}</Descriptions.Item>
          <Descriptions.Item label="真实姓名">{profile?.realName || '-'}</Descriptions.Item>
          <Descriptions.Item label="昵称">{profile?.nickName || '-'}</Descriptions.Item>
          <Descriptions.Item label="角色">{profile ? roleText[profile.role] || profile.role : '-'}</Descriptions.Item>
          <Descriptions.Item label="创建时间">{formatDate(profile?.createdAt)}</Descriptions.Item>
          <Descriptions.Item label="更新时间">{formatDate(profile?.updatedAt)}</Descriptions.Item>
          <Descriptions.Item label="简介" span={2}>
            {profile?.bio || '-'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Modal title="编辑资料" open={editOpen} onCancel={() => setEditOpen(false)} onOk={submitEdit} destroyOnClose>
        <Form form={editForm} layout="vertical">
          <Form.Item name="studentId" label="学号" rules={[{ required: true, message: '请输入学号' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="realName" label="真实姓名" rules={[{ required: true, message: '请输入真实姓名' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="nickName" label="昵称" rules={[{ required: true, message: '请输入昵称' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="bio" label="简介">
            <Input.TextArea rows={4} maxLength={100} showCount />
          </Form.Item>
        </Form>
      </Modal>

      <Modal title="修改密码" open={passwordOpen} onCancel={() => setPasswordOpen(false)} onOk={submitPassword} destroyOnClose>
        <Form form={passwordForm} layout="vertical">
          <Form.Item name="oldPassword" label="旧密码" rules={[{ required: true, message: '请输入旧密码' }]}>
            <Input.Password />
          </Form.Item>
          <Form.Item
            name="newPassword"
            label="新密码"
            rules={[{ required: true, message: '请输入新密码' }, { min: 6, message: '密码至少 6 位' }]}
          >
            <Input.Password />
          </Form.Item>
        </Form>
      </Modal>

      <Modal title="注销账号" open={closeOpen} onCancel={() => setCloseOpen(false)} footer={null} destroyOnClose>
        <Form form={closeForm} layout="vertical">
          <Typography.Paragraph type="secondary">
            注销会关闭当前账号。请输入密码后确认，操作成功后会退出登录。
          </Typography.Paragraph>
          <Form.Item name="password" label="当前密码" rules={[{ required: true, message: '请输入当前密码' }]}>
            <Input.Password />
          </Form.Item>
          <Popconfirm title="确认注销账号？" description="该操作会关闭当前账号。" onConfirm={submitCloseAccount}>
            <Button danger block>
              确认注销账号
            </Button>
          </Popconfirm>
        </Form>
      </Modal>
    </div>
  );
}
