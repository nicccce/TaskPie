import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, Space, Typography, message } from 'antd';
import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { login } from '../api/auth';
import type { LoginPayload } from '../types';
import { getToken, setAuthInfo } from '../utils/auth';

export default function Login() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  if (getToken()) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleSubmit = async (values: LoginPayload) => {
    setLoading(true);
    try {
      const data = await login(values);
      setAuthInfo(data);
      message.success('登录成功');
      navigate('/dashboard', { replace: true });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <Card className="auth-card">
        <div className="auth-title">
          <Typography.Title level={2}>TaskPie</Typography.Title>
          <Typography.Text type="secondary">高校小组协作任务管理系统</Typography.Text>
        </div>
        <Form<LoginPayload> layout="vertical" size="large" onFinish={handleSubmit}>
          <Form.Item name="userName" label="用户名" rules={[{ required: true, message: '请输入用户名' }]}>
            <Input prefix={<UserOutlined />} placeholder="userName" />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: true, message: '请输入密码' }]}>
            <Input.Password prefix={<LockOutlined />} placeholder="password" />
          </Form.Item>
          <Button block type="primary" htmlType="submit" loading={loading}>
            登录
          </Button>
        </Form>
        <Space style={{ marginTop: 16 }}>
          <Typography.Text type="secondary">还没有账号？</Typography.Text>
          <Link to="/register">注册新用户</Link>
        </Space>
      </Card>
    </div>
  );
}
