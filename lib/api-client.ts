/**
 * API Client for ScanCode Backend
 * Central configuration for all backend API calls
 */

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export interface ApiResponse<T> {
  data?: T;
  message?: string;
  error?: string;
}

export class ApiError extends Error {
  constructor(
    public status: number,
    public statusText: string,
    message: string
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

/**
 * Generic fetch wrapper with error handling
 */
async function apiCall<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const url = `${API_BASE_URL}${endpoint}`;
  const token = typeof window !== 'undefined' ? localStorage.getItem('authToken') : null;

  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(url, {
    ...options,
    headers,
  });

  const contentType = response.headers.get('content-type');
  let data: any;

  if (contentType?.includes('application/json')) {
    data = await response.json();
  } else if (contentType?.includes('image')) {
    data = await response.blob();
  } else {
    data = await response.text();
  }

  if (!response.ok) {
    const errorMessage = data?.error || data?.message || response.statusText;
    throw new ApiError(response.status, response.statusText, errorMessage);
  }

  return data as T;
}

/**
 * Authentication Endpoints
 */
export const authApi = {
  register: async (username: string, email: string, password: string) => {
    return apiCall('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, email, password }),
    });
  },

  forgotPassword: async (email: string) => {
    return apiCall('/auth/forgot-password', {
      method: 'POST',
      body: JSON.stringify({ email }),
    });
  },

  resetPassword: async (token: string, newPassword: string, confirmPassword: string) => {
    return apiCall('/auth/reset-password', {
      method: 'POST',
      body: JSON.stringify({ token, newPassword, confirmPassword }),
    });
  },

  verifyEmail: async (token: string) => {
    return apiCall(`/auth/verify?token=${token}`, {
      method: 'GET',
    });
  },

  login: async (email: string, password: string) => {
    return apiCall('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    });
  },
};

/**
 * Business Endpoints
 */
export const businessApi = {
  createStorefront: async (businessData: any) => {
    return apiCall('/api/business/storefronts', {
      method: 'POST',
      body: JSON.stringify(businessData),
    });
  },

  getStorefrontBySlug: async (slug: string) => {
    return apiCall(`/api/business/storefronts/${slug}`, {
      method: 'GET',
    });
  },

  getStorefrontQrCode: async (id: number, size: number = 512) => {
    return apiCall(`/api/business/storefronts/${id}/qr-code?size=${size}`, {
      method: 'GET',
    });
  },

  downloadStorefrontQrCode: async (id: number, size: number = 1024) => {
    return apiCall(`/api/business/storefronts/${id}/qr-code/download?size=${size}`, {
      method: 'GET',
    });
  },

  getStorefrontByPublicSlug: async (slug: string) => {
    return apiCall(`/${slug}`, {
      method: 'GET',
    });
  },
};

export default { authApi, businessApi };
