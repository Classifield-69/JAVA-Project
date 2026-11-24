<script setup>
import { onMounted } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

onMounted(() => {
  auth.loadUser().catch(() => {})
})

function logout() {
  auth.logout()
  router.push('/')
}
</script>

<template>
  <header class="nav">
    <RouterLink to="/" class="logo">GameCollection</RouterLink>
    <nav>
      <RouterLink to="/">Игри</RouterLink>
      <template v-if="auth.isLoggedIn">
        <RouterLink to="/profile">{{ auth.user?.username || 'Профил' }}</RouterLink>
        <button class="link" @click="logout">Изход</button>
      </template>
      <RouterLink v-else to="/login">Вход</RouterLink>
    </nav>
  </header>

  <main class="container">
    <RouterView />
  </main>
</template>