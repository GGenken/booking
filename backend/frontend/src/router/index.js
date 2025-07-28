import {createRouter, createWebHistory} from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Dashboard from '@/views/Dashboard.vue';
import UserDashboard from '@/views/UserDashboard.vue';
import Coworking from '@/views/Coworking.vue';

const routes = [
    {path: '/', name: 'Home', component: Home},
    {path: '/login', name: 'Login', component: Login},
    {path: '/dashboard', name: 'Dashboard', component: Dashboard},
    {path: '/dashboard/user/:uuid', name: 'UserDashboard', component: UserDashboard},
    {path: '/coworking', name: 'Coworking', component: Coworking},
]

export const router = createRouter({
    history: createWebHistory(),
    routes,
})
