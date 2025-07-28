import axios from 'axios';

const baseURL = import.meta.env.API_URL || "http://localhost/api/";

const api = axios.create({
    baseURL,
    headers: {
        'Content-Type': "application/json",
        Accept: "application/json"
    }
});

api.interceptors.request.use(cfg => {
    const token = localStorage.getItem('authToken');
    if (token) {
        cfg.headers.Authorization = `Bearer ${token}`;
    }
    return cfg;
});

api.interceptors.response.use(
    res => res,
    err => {
        if (err.response?.status === 401) {
            localStorage.removeItem('authToken');
            window.location.replace('/login');
        }
        return Promise.reject(err);
    }
);

export default api
