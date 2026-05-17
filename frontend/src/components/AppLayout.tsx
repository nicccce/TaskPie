import {
  BellOutlined,
  CheckSquareOutlined,
  LogoutOutlined,
  PieChartOutlined,
  TeamOutlined,
  UserOutlined,
} from '@ant-design/icons';
import { Button, Layout, Menu, Space, Typography, message } from 'antd';
import type { MenuProps } from 'antd';
import { useEffect, useState } from 'react';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { logout } from '../api/auth';
import { clearAuth, getNickName } from '../utils/auth';

const { Header, Sider, Content } = Layout;

const menuItems: MenuProps['items'] = [
  { key: '/dashboard', icon: <PieChartOutlined />, label: '工作台' },
  { key: '/dashboard/profile', icon: <UserOutlined />, label: '个人中心' },
  { key: '/dashboard/teams', icon: <TeamOutlined />, label: '我的小组' },
  { key: '/dashboard/notices', icon: <BellOutlined />, label: '通知中心' },
  { key: '/dashboard/tasks', icon: <CheckSquareOutlined />, label: '任务中心' },
];

function selectedKey(pathname: string) {
  if (pathname.startsWith('/dashboard/teams')) return '/dashboard/teams';
  if (pathname.startsWith('/dashboard/notices')) return '/dashboard/notices';
  if (pathname.startsWith('/dashboard/tasks')) return '/dashboard/tasks';
  if (pathname.startsWith('/dashboard/profile')) return '/dashboard/profile';
  return '/dashboard';
}

export default function AppLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const [nickName, setNickName] = useState(getNickName());

  useEffect(() => {
    const syncNickName = () => setNickName(getNickName());
    window.addEventListener('taskpie-auth-change', syncNickName);
    return () => window.removeEventListener('taskpie-auth-change', syncNickName);
  }, []);

  const handleLogout = async () => {
    try {
      await logout();
    } catch {
      // The local session must be cleared even if the backend token is already invalid.
    } finally {
      clearAuth();
      message.success('已退出登录');
      navigate('/login', { replace: true });
    }
  };

  return (
    <Layout className="app-shell">
      <Sider width={224} className="app-sider">
        <div className="brand">
          <div className="brand-mark">TP</div>
          <div>
            <Typography.Title level={4} className="brand-title">
              TaskPie
            </Typography.Title>
            <Typography.Text type="secondary">高校小组协作</Typography.Text>
          </div>
        </div>
        <Menu
          mode="inline"
          selectedKeys={[selectedKey(location.pathname)]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header className="app-header">
          <Typography.Text strong>当前用户：{nickName}</Typography.Text>
          <Space>
            <Button icon={<LogoutOutlined />} onClick={handleLogout}>
              退出登录
            </Button>
          </Space>
        </Header>
        <Content className="app-content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
