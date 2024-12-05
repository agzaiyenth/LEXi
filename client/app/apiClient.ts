// app/apiClient.ts
import axios from 'axios';
import { BASE_URL } from '@/config';
import { useSession } from './ctx';

const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
});

apiClient.interceptors.request.use(
  async (config) => {
    const { session } = useSession();
    if (session && config.headers) {
      config.headers.Authorization = `Bearer ${session}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

export default apiClient;
