import { apiClient } from './client'
import type { AuthResponse, LoginPayload, MessageResponse, RegisterPayload } from '../types'

export async function login(payload: LoginPayload): Promise<AuthResponse> {
  const { data } = await apiClient.post<AuthResponse>('/api/auth/login', payload)
  return data
}

export async function register(payload: RegisterPayload): Promise<AuthResponse> {
  const { data } = await apiClient.post<AuthResponse>('/api/auth/register', payload)
  return data
}

export async function logout(): Promise<MessageResponse> {
  const { data } = await apiClient.post<MessageResponse>('/api/auth/logout')
  return data
}
