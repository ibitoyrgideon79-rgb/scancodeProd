'use client';

import Link from 'next/link';
import Image from 'next/image';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { authApi } from '@/lib/api-client';

export default function LoginPage() {
  const router = useRouter();
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    
    if (!email || !password) {
      setError('Please enter both email and password');
      return;
    }

    setLoading(true);
    try {
      const response = await authApi.login(email, password);
      
      // Store auth token if provided
      if (response.token) {
        localStorage.setItem('authToken', response.token);
        localStorage.setItem('user', JSON.stringify(response.user));
      }
      
      setSuccess('Login successful! Redirecting...');
      setTimeout(() => {
        router.push('/admin/dashboard');
      }, 1500);
    } catch (err: any) {
      setError(err.message || 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-6">
        <div className="max-w-md w-full bg-white rounded-3xl shadow-lg p-10">
          
          {/* Logo */}
          <div className="text-center mb-10">
            <div className="mx-auto mb-6 relative w-20 h-20">
              <Image
                src="/image4.png"
                alt="ScanCode Logo"
                fill
                className="object-contain"
                priority
              />
            </div>
            <h1 className="text-3xl font-semibold text-black">Log in to ScanCode Account</h1>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Error Message */}
            {error && (
              <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-600">
                {error}
              </div>
            )}

            {/* Success Message */}
            {success && (
              <div className="rounded-2xl border border-green-200 bg-green-50 px-4 py-3 text-sm font-medium text-green-600">
                {success}
              </div>
            )}

            {/* Email Address */}
            <div>
              <label className="block text-sm font-medium text-black mb-2">
                Email address
              </label>
              <input
                type="email"
                placeholder="Enter your email address"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-5 py-4 border border-gray-300 rounded-2xl focus:outline-none focus:border-green-600 transition-colors text-black placeholder:text-gray-500"
              />
            </div>

            {/* Password */}
            <div>
              <label className="block text-sm font-medium text-black mb-2">
                Password
              </label>
              <div className="relative">
                <input
                  type={showPassword ? "text" : "password"}
                  placeholder="Enter your password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full px-5 py-4 border border-gray-300 rounded-2xl focus:outline-none focus:border-green-600 transition-colors text-black placeholder:text-gray-500 pr-12"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-5 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
                  aria-label={showPassword ? "Hide password" : "Show password"}
                >
                  {showPassword ? (
                    // Closed Eye SVG
                    <svg xmlns="http://www.w3.org/2000/svg" className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908l3.42 3.42M3 3l18 18" />
                    </svg>
                  ) : (
                    // Open Eye SVG
                    <svg xmlns="http://www.w3.org/2000/svg" className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                      <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      <path strokeLinecap="round" strokeLinejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5 16.477 5 20.268 7.943 21.542 12 20.268 16.057 16.477 19 12 19 7.523 19 3.732 16.057 2.458 12z" />
                    </svg>
                  )}
                </button>
              </div>
            </div>

            {/* Remember Me + Forgot Password */}
            <div className="flex items-center justify-between">
              <label className="flex items-center gap-2 text-sm font-medium text-black cursor-pointer">
                <input 
                  type="checkbox" 
                  checked={rememberMe}
                  onChange={(e) => setRememberMe(e.target.checked)}
                  className="w-5 h-5 accent-green-600 rounded border-gray-300 cursor-pointer" 
                />
                Remember Me
              </label>
              <Link href="forgot" className="text-sm text-green-600 hover:underline font-medium">
                Forgot Password
              </Link>
            </div>

            {/* Login Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full bg-green-600 hover:bg-green-700 disabled:bg-gray-400 text-white font-semibold py-4 rounded-2xl transition-colors text-lg disabled:cursor-not-allowed"
            >
              {loading ? 'Logging In...' : 'Log In'}
            </button>
          </form>

          {/* Divider */}
          <div className="my-8 flex items-center gap-4">
            <div className="flex-1 h-px bg-gray-200"></div>
            <span className="text-gray-500 text-sm">OR</span>
            <div className="flex-1 h-px bg-gray-200"></div>
          </div>

          {/* Social Login Buttons */}
          <div className="grid grid-cols-2 gap-4">
            {/* Google Button */}
            <button className="flex items-center justify-center gap-3 border border-gray-300 hover:border-gray-400 py-4 rounded-2xl transition-colors">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.51h5.92c-.25 1.36-.98 2.51-2.08 3.28v2.65h3.36c1.97-1.82 3.1-4.48 3.1-7.19z"/>
                <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.36-2.65c-.93.63-2.12 1-3.92 1-3.01 0-5.56-2.03-6.47-4.76H2.18v2.99C3.99 20.53 7.7 23 12 23z"/>
                <path fill="#FBBC05" d="M5.53 14.59c-.23-.69-.36-1.42-.36-2.19s.13-1.5.36-2.19V7.2H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.8l2.35-1.99z"/>
                <path fill="#EA4335" d="M12 4.75c1.69 0 3.2.59 4.39 1.74l3.28-3.28C17.46 1.69 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.2l3.35 2.99c.91-2.73 3.46-4.76 6.47-4.76z"/>
              </svg>
              <span className="font-medium text-black">Google</span>
            </button>

            {/* Facebook Button */}
            <button className="flex items-center justify-center gap-3 border border-gray-300 hover:border-gray-400 py-4 rounded-2xl transition-colors">
              <div className="w-6 h-6 bg-[#1877F2] rounded-full flex items-center justify-center text-white text-xl font-bold">f</div>
              <span className="font-medium text-black">Facebook</span>
            </button>
          </div>

          {/* Sign Up Link */}
          <p className="text-center mt-10 text-black">
            Don&apos;t have an account?{' '}
            <Link href="/signup" className="text-green-600 font-semibold hover:underline">
              Sign Up
            </Link>
          </p>

        </div>
      </div>
    
  );
}
