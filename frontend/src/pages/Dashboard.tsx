import { BellOutlined, CheckSquareOutlined, TeamOutlined, UserOutlined } from '@ant-design/icons';
import { Button, Card, Col, Row, Space, Statistic, Typography } from 'antd';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { selectNotices } from '../api/notice';
import { getMyTasks } from '../api/task';
import { getMyTeams } from '../api/team';
import { getMe } from '../api/user';

export default function Dashboard() {
  const navigate = useNavigate();
  const [counts, setCounts] = useState({ teams: 0, notices: 0, tasks: 0 });
  const [nickName, setNickName] = useState('');

  useEffect(() => {
    Promise.allSettled([getMe(), getMyTeams(), selectNotices({ type: 3, page: 0, size: 1 }), getMyTasks({ page: 0, size: 1 })]).then(
      ([meResult, teamResult, noticeResult, taskResult]) => {
        if (meResult.status === 'fulfilled') {
          setNickName(meResult.value.nickName);
        }
        setCounts({
          teams: teamResult.status === 'fulfilled' ? teamResult.value.teams.length : 0,
          notices: noticeResult.status === 'fulfilled' ? noticeResult.value.totalItems : 0,
          tasks: taskResult.status === 'fulfilled' ? taskResult.value.totalItems : 0,
        });
      },
    );
  }, []);

  return (
    <div className="page-stack">
      <Card>
        <Space direction="vertical" size={4}>
          <Typography.Title level={3} style={{ margin: 0 }}>
            {nickName ? `${nickName}，欢迎使用 TaskPie` : 'TaskPie 工作台'}
          </Typography.Title>
          <Typography.Text type="secondary">当前课程小组、通知和任务状态</Typography.Text>
        </Space>
      </Card>
      <Row gutter={[16, 16]}>
        <Col xs={24} md={8}>
          <Card>
            <Statistic title="我的小组" value={counts.teams} prefix={<TeamOutlined />} />
            <Button type="link" onClick={() => navigate('/dashboard/teams')}>
              进入小组
            </Button>
          </Card>
        </Col>
        <Col xs={24} md={8}>
          <Card>
            <Statistic title="待处理邀请/申请" value={counts.notices} prefix={<BellOutlined />} />
            <Button type="link" onClick={() => navigate('/dashboard/notices')}>
              查看通知
            </Button>
          </Card>
        </Col>
        <Col xs={24} md={8}>
          <Card>
            <Statistic title="我的任务" value={counts.tasks} prefix={<CheckSquareOutlined />} />
            <Button type="link" onClick={() => navigate('/dashboard/tasks')}>
              处理任务
            </Button>
          </Card>
        </Col>
      </Row>
      <Card>
        <Space wrap>
          <Button icon={<UserOutlined />} onClick={() => navigate('/dashboard/profile')}>
            个人中心
          </Button>
          <Button icon={<TeamOutlined />} onClick={() => navigate('/dashboard/teams')}>
            我的小组
          </Button>
          <Button icon={<BellOutlined />} onClick={() => navigate('/dashboard/notices')}>
            通知中心
          </Button>
          <Button type="primary" icon={<CheckSquareOutlined />} onClick={() => navigate('/dashboard/tasks')}>
            任务中心
          </Button>
        </Space>
      </Card>
    </div>
  );
}
