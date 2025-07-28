<template>
  <div
      :class="{
      'bg-green-500': state === 'free',
      'bg-red-500': state === 'taken',
    }"
      class="seat w-24 h-24 flex items-center justify-center text-white font-bold rounded-lg cursor-pointer"
      :title="'Seat ' + id"
  >
    <span class="text-xl">{{ id }}</span>
  </div>
</template>

<script>
export default {
  props: {
    state: {
      type: String,
      required: true,
      validator(value) {
        return ['taken', 'free'].includes(value);
      },
    },
    id: {
      type: Number,
      required: true,
    },
  },
};
</script>

<style scoped>
.seat {
  transition: background-color 0.3s ease;
}
</style>
