<template>
  <section>
    <div class="min-h-screen flex items-center justify-center bg-gray-100">
      <form
          @submit.prevent="submit"
          class="bg-white p-6 rounded shadow-md w-full max-w-sm space-y-4"
      >
        <h2 class="text-xl font-bold text-center">Coworking space booking</h2>
        <div>
          <input
              v-model="username"
              placeholder="Username"
              class="w-full px-3 py-2 border rounded focus:outline-none"
              @blur="$v.username.$touch()"
              :class="{ 'border-red-500': $v.username.$error }"
          />
          <p v-if="$v.username.$error" class="text-red-500 text-sm mt-1">
            {{ $v.username.$errors[0].$message }}
          </p>
        </div>

        <div>
          <input
              v-model="password"
              type="password"
              placeholder="Password"
              class="w-full px-3 py-2 border rounded focus:outline-none"
              @blur="$v.password.$touch()"
              :class="{ 'border-red-500': $v.password.$error }"
          />
          <p v-if="$v.password.$error" class="text-red-500 text-sm mt-1">
            {{ $v.password.$errors[0].$message }}
          </p>
        </div>

        <button
            type="submit"
            :disabled="$v.$invalid"
            class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:bg-gray-400"
        >
          Login
        </button>
      </form>
    </div>
  </section>
</template>

<script setup>
import {ref} from 'vue'
import useVuelidate from '@vuelidate/core'
import {required, minLength, maxLength, alphaNum, helpers} from '@vuelidate/validators'
import {useRouter} from 'vue-router';
import {useUserStore} from '@/stores/user.js';

const router = useRouter();
const user = useUserStore();

const username = ref('')
const password = ref('')

const validPasswordChars = helpers.regex(
    /^[a-zA-Z0-9!@#$%^&*()_+=]+$/
)

const rules = {
  username: {
    required: helpers.withMessage("Username is required", required),
    minLength: helpers.withMessage("Username must not be shorter than 2 characters", minLength(2)),
    maxLength: helpers.withMessage("Username must not be longer than 16 characters", maxLength(16)),
    alphaNum: helpers.withMessage("Username must contain only alphanumeric characters", alphaNum)

  },
  password: {
    required: helpers.withMessage("Password is required", required),
    minLength: helpers.withMessage("Password must not be shorter than 4 characters", minLength(4)),
    maxLength: helpers.withMessage("Password must not be longer than 128 characters", maxLength(128)),
    validChars: helpers.withMessage(
        "Password must contain only alphanumeric characters and !@#$%^&*()_+=",
        validPasswordChars
    )
  }
}

const $v = useVuelidate(rules, {username, password})

async function submit() {
  $v.value.$touch();
  if ($v.value.$invalid) return;
  try {
    await user.login(username.value, password.value);
    await router.push('/dashboard');
  } catch (e) {
    console.error("Login failed:", e);
  }
}
</script>

<style scoped>
</style>
