import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import GamesView from '@/views/GamesView.vue'
import GameDetailsView from '@/views/GameDetailsView.vue'
import LoginView from '@/views/LoginView.vue'
import OAuthCallbackView from '@/views/OAuthCallbackView.vue'
import ProfileView from '@/views/ProfileView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'games', component: GamesView },
    { path: '/games/:id', name: 'game', component: GameDetailsView, props: true },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/oauth2/callback', name: 'oauth-callback', component: OAuthCallbackView },
    { path: '/profile', name: 'profile', component: ProfileView, meta: { requiresAuth: true } },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    return { name: 'login' }
  }
})

export default router