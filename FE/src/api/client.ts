import axios from 'axios'
import type { AxiosError } from 'axios'
import { useAuthStore } from '../store/authStore'
import type { ApiErrorBody } from '../types'

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080',
})

apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error?.response?.status === 401) {
      useAuthStore.getState().clearAuth()
    }
    return Promise.reject(error)
  },
)

export function getApiErrorMessage(error: unknown, fallback = 'Qualcosa è andato storto. Riprova.'): string {
  const axiosError = error as AxiosError<ApiErrorBody>
  return axiosError.response?.data?.message ?? fallback
}
