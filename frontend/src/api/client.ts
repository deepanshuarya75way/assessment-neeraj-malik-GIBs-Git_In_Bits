import axios, { AxiosError } from 'axios';

// Constants
export const API_BASE_URL = 'http://localhost:9090';

// Axios Instance
export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true, // Crucial for Spring Security Sessions
});

// Request Interceptor: Attach Data Source headers
apiClient.interceptors.request.use((config) => {
  const sourceType = localStorage.getItem('gib_source_type');
  const sourceValue = localStorage.getItem('gib_source_value');
  
  if (sourceType && sourceValue) {
    config.headers['X-GitHub-Source-Type'] = sourceType;
    config.headers['X-GitHub-Source-Value'] = sourceValue;
  }
  return config;
});

// Response Interceptor: Centralized error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response) {
      const status = error.response.status;

      if (status === 401) {
        // Unauthorized - session expired or not logged in
        if (window.location.pathname !== '/login') {
          window.location.href = '/login';
        }
      } else if (status === 403) {
        console.error('Access Denied or Rate Limited:', error.response.data);
        // We let the UI handle rate limits based on error messages
      } else if (status === 404) {
        console.error('Resource not found:', error.response.data);
      } else if (status === 429) {
        console.error('Too many requests (Rate Limited):', error.response.data);
      }
    }
    return Promise.reject(error);
  }
);
