<template>
  <div class="p-6">
    <div v-if="loading" class="text-gray-500">Loading users...</div>
    <div v-else>
      <div v-if="!usersStore.list.length" class="text-gray-500">No users available.</div>
      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Username</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">UUID</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Reservations</th>
            <th class="px-6 py-3"></th>
            <th class="px-6 py-3"></th>
          </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="user in usersStore.list" :key="user.id">
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ user.id }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ user.username }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 break-all">{{ user.uuid }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ user.reservations.length }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">
              <router-link
                  :to="{ name: 'UserDashboard', params: { uuid: user.uuid } }"
                  class="bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700"
              >
                View
              </router-link>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">
              <button
                  @click="usersStore.deleteUser(user.uuid)"
                  class="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700"
              >
                Delete
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useUsersStore } from '@/stores/users';

const usersStore = useUsersStore();
const loading = ref(true);

onMounted(async () => {
  try {
    await usersStore.fetchUsers();
  } finally {
    loading.value = false;
  }
});
</script>
