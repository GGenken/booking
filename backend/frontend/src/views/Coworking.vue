<template>
  <div class="p-4">
    <h2 class="text-xl font-bold mb-4">Coworking Space Layout</h2>
    <div class="space-y-4">
      <div
          v-for="row in Array.from(new Set(seatsStore.layout.map(s => s.row))).sort((a, b) => a - b)"
          :key="row"
          class="flex space-x-4"
      >
        <Seat
            v-for="seat in seatsStore.layout
            .filter(s => s.row === row)
            .sort((a, b) => a.col - b.col)"
            :key="seat.id"
            :id="seat.id"
            :state="seatsStore.isSeatAvailable(seat.id) ? 'free' : 'taken'"
        />
      </div>
    </div>
  </div>
  <form @submit.prevent="updateAvailability" class="space-y-4 mb-6">
    <div class="flex space-x-4">
      <div>
        <label class="block text-sm font-medium text-gray-700">Start Date</label>
        <input
            type="date"
            v-model="startDate"
            class="mt-1 block w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700">Start Time</label>
        <input
            type="time"
            v-model="startTime"
            step="60"
            class="mt-1 block w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>
    </div>
    <div class="flex space-x-4">
      <div>
        <label class="block text-sm font-medium text-gray-700">End Date</label>
        <input
            type="date"
            v-model="endDate"
            class="mt-1 block w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700">End Time</label>
        <input
            type="time"
            v-model="endTime"
            step="60"
            class="mt-1 block w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>
    </div>
    <button
        type="submit"
        class="inline-flex items-center px-4 py-2 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
    >
      Update
    </button>
  </form>
</template>

<script>
import {useSeatsStore} from "@/stores/seats";
import Seat from '@/components/Seat.vue';
import {onMounted} from "vue";

export default {
  components: {
    Seat,
  },
  setup() {
    const seatsStore = useSeatsStore();

    onMounted(async () => {
      await seatsStore.intialize();
    });

    return {seatsStore};
  },
};
</script>

<style scoped>
</style>
