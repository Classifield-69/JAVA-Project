<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const error = ref('')

const messages = {
  email_not_verified: 'Имейлът в Google не е потвърден',
  account_disabled: 'Акаунтът е деактивиран',
  google_login_failed: 'Входът с Google не успя',
}

onMounted(async () => {
  // адресът е /oauth2/callback#token=eyJ... или #error=...
  const params = new URLSearchParams(window.location.hash.slice(1))
  const token = params.get('token')

  if (token) {
    await auth.setToken(token)
    router.replace('/profile') // replace: адресът с токена не остава в историята
  } else {
    error.value = messages[params.get('error')] || 'Входът с Google не успя'
  }
})
</script>

<template>
  <div class="card form">
    <p v-if="error" class="error">{{ error }}</p>
    <p v-else class="muted">Влизане...</p>
    <RouterLink v-if="error" to="/login">Назад към вход</RouterLink>
  </div>
</template>