import { Button, Card, Form, Input, Space, Typography, message } from 'antd';
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { register } from '../api/auth';
import type { RegisterPayload } from '../types';

export default function Register() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (values: RegisterPayload) => {
    setLoading(true);
    try {
      await register(values);
      message.success('注册成功，请登录');
      navigate('/login', { replace: true });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <Card className="auth-card">
        <div className="auth-title">
          <Typography.Title level={2}>注册 TaskPie</Typography.Title>
          <Typography.Text type="secondary">创建课程小组协作账号</Typography.Text>
        </div>
        <Form<RegisterPayload> layout="vertical" onFinish={handleSubmit}>
          <Form.Item
            name="studentId"
            label="学号"
            rules={[{ required: true, message: '请输入学号' }, { min: 8, message: '学号至少 8 位' }]}
          >
            <Input placeholder="20230001" />
          </Form.Item>
          <Form.Item name="userName" label="用户名" rules={[{ required: true, message: '请输入用户名' }]}>
            <Input placeholder="zhangsan" />
          </Form.Item>
          <Form.Item
            name="password"
            label="密码"
            rules={[{ required: true, message: '请输入密码' }, { min: 6, message: '密码至少 6 位' }]}
          >
            <Input.Password placeholder="至少 6 位" />
          </Form.Item>
          <Form.Item name="realName" label="真实姓名" rules={[{ required: true, message: '请输入真实姓名' }]}>
            <Input placeholder="张三" />
          </Form.Item>
          <Form.Item name="nickName" label="昵称" rules={[{ required: true, message: '请输入昵称' }]}>
            <Input placeholder="组内展示名称" />
          </Form.Item>
          <Button block type="primary" htmlType="submit" loading={loading}>
            注册
          </Button>
        </Form>
        <Space style={{ marginTop: 16 }}>
          <Typography.Text type="secondary">已有账号？</Typography.Text>
          <Link to="/login">返回登录</Link>
        </Space>
      </Card>
    </div>
  );
}
