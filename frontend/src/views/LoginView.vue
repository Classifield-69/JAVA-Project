<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api, { errorMessage } from '@/api'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

const mode = ref('login') // 'login' или 'register'
const username = ref('')
const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  loading.value = true
  try {
    const { data } =
      mode.value === 'login'
        ? await api.post('/auth/login', { email: email.value, password: password.value })
        : await api.post('/auth/register', {
            username: username.value,
            email: email.value,
            password: password.value,
          })
    await auth.setToken(data.token)
    router.push('/profile')
  } catch (e) {
    error.value = errorMessage(e)
  } finally {
    loading.value = false
  }
}

function toggleMode() {
  mode.value = mode.value === 'login' ? 'register' : 'login'
  error.value = ''
}

function loginWithGoogle() {
  window.location.href = import.meta.env.VITE_API_URL + '/oauth2/authorization/google'
}
</script>

<template>
  <div class="card form">
    <h1>{{ mode === 'login' ? 'Вход' : 'Регистрация' }}</h1>

    <form @submit.prevent="submit">
      <input v-if="mode === 'register'" v-model="username" placeholder="Потребителско име" required />
      <input v-model="email" type="email" placeholder="Имейл" required />
      <input v-model="password" type="password" placeholder="Парола" required />

      <p v-if="error" class="error">{{ error }}</p>

      <button type="submit" :disabled="loading">
        {{ mode === 'login' ? 'Вход' : 'Регистрация' }}
      </button>
    </form>

    <p>
      <button class="secondary" @click="loginWithGoogle">Вход с Google</button>
    </p>

    <p class="muted">
      {{ mode === 'login' ? 'Нямаш акаунт?' : 'Имаш акаунт?' }}
      <a href="#" @click.prevent="toggleMode">
        {{ mode === 'login' ? 'Регистрирай се' : 'Влез' }}
      </a>
    </p>
  </div>
</template>