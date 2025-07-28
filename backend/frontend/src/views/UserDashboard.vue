<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">Reservations for {{ user.username }}</h1>

    <ReservationList :reservations="reservationsWithUser" :showDelete="true"/>
  </div>
</template>

<script setup>
import {onMounted, computed} from 'vue';
import {useRoute} from 'vue-router';
import {useUsersStore} from '@/stores/users';
import ReservationList from '@/components/ReservationList.vue';

const route = useRoute();
const uuid = route.params.uuid;
const usersStore = useUsersStore();

onMounted(async () => {
  if (!usersStore.list.length) {
    await usersStore.fetchUsers();
  }
});

const user = computed(() => {
  return usersStore.list.find(u => u.uuid === uuid) || {username: '', reservations: []};
});

const reservationsWithUser = computed(() => {
  return user.value.reservations.map(r => ({
    ...r,
    username: user.value.username
  }));
});
</script>

<style scoped>
</style>
