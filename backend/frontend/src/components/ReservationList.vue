<template>
  <div class="p-6">
    <div v-if="!localReservations.length" class="text-gray-500">
      No localReservations available.
    </div>
    <div v-else class="overflow-x-auto">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
        <tr>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Username</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Row</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Column</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Start Time</th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">End Time</th>
        </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
        <tr v-for="res in localReservations" :key="res.id">
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ res.id }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ res.username }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ res.seat.row }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ res.seat.col }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ res.startTime }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ res.endTime }}</td>
          <td v-if="showDelete" class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
            <button
                @click="deleteReservation(res.id)"
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
</template>

<script setup>
import {defineProps, ref} from 'vue';
import api from '@/services/api.js';

const props = defineProps({
  reservations: {
    type: Array,
    required: true
  },
  showDelete: {
    type: Boolean,
    default: false
  }
});
const emit = defineEmits(['deleted']);

const localReservations = ref([...props.reservations]);  // crutching since it's a readonly prop

async function deleteReservation(id) {
  try {
    await api.delete(`/reservations/${id}`);
    localReservations.value = localReservations.value.filter(r => r.id !== id);
    emit('deleted', id);
    return true;
  } catch (e) {
    console.error("Failed to delete reservation:", e);
    return false;
  }
}

const {reservations} = props;
</script>
