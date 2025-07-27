<template>
  <div class="login">
    <h1>Login</h1>
    <form @submit.prevent="submit">
      <div>
        <label for="username">Username</label>
        <input id="username" v-model="username" type="text" required/>
      </div>

      <div>
        <label for="password">Password</label>
        <input id="password" v-model="password" type="password" required/>
      </div>

      <button type="submit">Log in</button>
    </form>
  </div>
</template>

<script setup>
import {ref} from 'vue'

const username = ref('')
const password = ref('')

function submit() {
  console.log('Logging in with', username.value, password.value)
}
</script>

<style scoped>
</style>
