import { Navigate, createBrowserRouter } from 'react-router-dom';
import AppLayout from '../components/AppLayout';
import Dashboard from '../pages/Dashboard';
import Login from '../pages/Login';
import Notices from '../pages/Notices';
import Profile from '../pages/Profile';
import Register from '../pages/Register';
import TaskDetail from '../pages/TaskDetail';
import Tasks from '../pages/Tasks';
import TeamDetail from '../pages/TeamDetail';
import Teams from '../pages/Teams';
import { getToken } from '../utils/auth';

function ProtectedRoute() {
  return getToken() ? <AppLayout /> : <Navigate to="/login" replace />;
}

const router = createBrowserRouter([
  { path: '/', element: <Navigate to="/dashboard" replace /> },
  { path: '/login', element: <Login /> },
  { path: '/register', element: <Register /> },
  {
    path: '/dashboard',
    element: <ProtectedRoute />,
    children: [
      { index: true, element: <Dashboard /> },
      { path: 'profile', element: <Profile /> },
      { path: 'teams', element: <Teams /> },
      { path: 'teams/:teamId', element: <TeamDetail /> },
      { path: 'notices', element: <Notices /> },
      { path: 'tasks', element: <Tasks /> },
      { path: 'tasks/:taskId', element: <TaskDetail /> },
    ],
  },
  { path: '*', element: <Navigate to="/dashboard" replace /> },
]);

export default router;
