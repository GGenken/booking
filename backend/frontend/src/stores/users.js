import {defineStore} from 'pinia';
import api from '@/services/api';

export const useUsersStore = defineStore('users', {
    state: () => ({
        list: []
    }),
    actions: {
        async fetchUsers() {
            try {
                const response = await api.get('/auth/users');
                this.list = response.data;
            } catch (e) {
                console.error("Failed to fetch users:", e);
                throw e;
            }
        }
    }
});
