import { defineStore } from 'pinia';
import axios from 'axios';
import api from '@/services/api.js';

export const useSeatsStore = defineStore('seats', {
    state: () => ({
        layout: [],
        availableSeats: [],
    }),

    getters: {
        isSeatAvailable: (state) => (seatId) => {
            return state.availableSeats.includes(seatId);
        },
    },

    actions: {
        async fetchSeatLayout() {
            try {
                const response = await api.get('/seats/layout');
                this.layout = response.data;
            } catch (error) {
                console.error("Error fetching seat layout:", error);
            }
        },

        async fetchAvailableSeats(startTime = new Date(), endTime = new Date()) {
            try {
                const response = await api.get('/seats/available', {
                    params: {
                        startTime: startTime,
                        endTime: endTime
                    }
                });
                console.log(response);
                this.availableSeats = response.data.map(seat => seat.id);
            } catch (error) {
                console.error("Error fetching available seats:", error);
            }
        },

        async intialize(startTime, endTime) {
            await this.fetchSeatLayout();
            await this.fetchAvailableSeats(startTime, endTime);
        },
    },
});
