import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { AuthResponse } from '../types'

interface AuthState {
  token: string | null
  userId: string | null
  username: string | null
  roles: string[]
  isAuthenticated: boolean
  isAdmin: boolean
  setAuth: (auth: AuthResponse) => void
  clearAuth: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      userId: null,
      username: null,
      roles: [],
      isAuthenticated: false,
      isAdmin: false,
      setAuth: (auth) =>
        set({
          token: auth.token,
          userId: auth.userId,
          username: auth.username,
          roles: auth.roles,
          isAuthenticated: true,
          isAdmin: auth.roles.includes('Admin'),
        }),
      clearAuth: () =>
        set({
          token: null,
          userId: null,
          username: null,
          roles: [],
          isAuthenticated: false,
          isAdmin: false,
        }),
    }),
    { name: 'comicshop-auth' },
  ),
)
