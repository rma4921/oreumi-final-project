const baseURL = window.location.hostname === 'localhost'
    ? 'http://localhost:8080' : '';

const apiClient = axios.create({
    baseURL,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    },
});

apiClient.interceptors.response.use(
    response => {
        console.log('[apiClient] 응답 성공:', response.config.url);
        return response;
    },
    async error => {
        const originalRequest = error.config;

        console.warn('[apiClient] 에러 발생:', error.config?.url,
            error.response?.status);

        if (error.response && error.response.status === 401
            && !originalRequest._retry) {
            originalRequest._retry = true;
            console.info('[apiClient] 401 감지됨. 토큰 갱신 시도 중...');

            try {
                const refreshResponse = await axios.post('/api/token/refresh',
                    {}, {withCredentials: true});
                console.info('[apiClient] 토큰 갱신 성공:', refreshResponse.status);

                return apiClient(originalRequest);
            } catch (refreshError) {
                console.error('[apiClient] 토큰 갱신 실패. 로그인 페이지로 이동');
                window.location.href = '/custom-login';
                return Promise.reject(refreshError);
            }
        }

        console.error('[apiClient] 에러 처리 실패:', error);
        return Promise.reject(error);
    }
);

window.apiClient = apiClient;
