import { apiClient } from './client'
import type { Item, MessageResponse } from '../types'

export async function getItems(): Promise<Item[]> {
  const { data } = await apiClient.get<Item[]>('/api/items')
  return data
}

export async function addFavourite(itemId: string): Promise<MessageResponse> {
  const { data } = await apiClient.post<MessageResponse>(`/api/items/${itemId}/favourites`)
  return data
}

export async function removeFavourite(itemId: string): Promise<MessageResponse> {
  const { data } = await apiClient.delete<MessageResponse>(`/api/items/${itemId}/favourites`)
  return data
}
