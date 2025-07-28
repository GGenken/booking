import {defineStore} from 'pinia';
import api from '@/services/api';
import {jwtDecode} from 'jwt-decode';

export const useUserStore = defineStore('user', {
    state: () => ({
        token: null,
        profile: null,
    }),
    actions: {
        async login(username, password) {
            const res = await api.post('/auth/login', {username, password});
            const auth = res.headers['authorization'] || res.headers['Authorization'];
            const token = auth?.replace("Bearer ", "");
            if (!token) throw new Error("No token in response");

            this.token = token;
            this.profile = jwtDecode(token);
            localStorage.setItem('authToken', token);
        },

        logout() {
            this.token = null;
            this.profile = null;
            localStorage.removeItem('authToken');
        },

        loadFromStorage() {
            const token = localStorage.getItem('authToken');
            if (token) {
                this.token = token;
                this.profile = jwtDecode(token);
            }
        }
    }
});
