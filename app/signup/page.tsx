'use client';

import Link from 'next/link';
import Image from 'next/image';
import { useState } from 'react';
import { authApi } from '@/lib/api-client';

export default function SignUpPage() {
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [agreeTerms, setAgreeTerms] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (formData.password !== formData.confirmPassword) {
      setError("Passwords do not match!");
      return;
    }
    if (!agreeTerms) {
      setError("Please agree to the Terms of Service & Privacy Policy");
      return;
    }

    setLoading(true);
    try {
      await authApi.register(formData.fullName, formData.email, formData.password);
      setIsSubmitted(true);
    } catch (err: any) {
      setError(err.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  // Success Screen - Check Email
  if (isSubmitted) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-6">
        <div className="max-w-md w-full bg-white rounded-3xl shadow-lg p-10 text-center">
          <div className="mx-auto mb-6 w-20 h-20 bg-green-100 rounded-2xl flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" className="w-12 h-12 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M3 8l7.89 5.26a2.01 2.01 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2" />
            </svg>
          </div>
          
          <h1 className="text-3xl font-semibold text-black mb-4">Check your email</h1>
          <p className="text-black mb-8 leading-relaxed">
            We have sent an activation link to your email address.<br />
            Please check your inbox and click the link to activate your account.
          </p>

          <button
            onClick={() => window.location.href = '/login'}
            className="w-full bg-green-600 hover:bg-green-700 text-white font-semibold py-4 rounded-2xl transition-colors text-lg mb-6"
          >
            Go to Login
          </button>

          <p className="text-sm text-gray-600">
            Didn't receive the email? Check your spam folder or{' '}
            <button 
              onClick={() => setIsSubmitted(false)} 
              className="text-green-600 hover:underline font-medium"
            >
              try again
            </button>
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center py-12 px-6">
      <div className="max-w-md w-full bg-white rounded-3xl shadow-lg p-10">
        
        <div className="text-center mb-10">
          <h1 className="text-3xl font-semibold text-black">Create Account</h1>
          <p className="text-black mt-2">Sign up to get started with Scancode</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Error Message */}
          {error && (
            <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-600">
              {error}
            </div>
          )}

          {/* Full Name */}
          <div>
            <label className="block text-sm font-medium text-black mb-2">Full Name</label>
            <input
              type="text"
              name="fullName"
              value={formData.fullName}
              onChange={handleInputChange}
              placeholder="Enter your full name"
              className="w-full px-5 py-4 border border-gray-300 rounded-2xl focus:outline-none focus:border-green-600 text-black placeholder:text-gray-500"
              required
            />
          </div>

          {/* Email Address */}
          <div>
            <label className="block text-sm font-medium text-black mb-2">Email Address</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleInputChange}
              placeholder="Enter your email address"
              className="w-full px-5 py-4 border border-gray-300 rounded-2xl focus:outline-none focus:border-green-600 text-black placeholder:text-gray-500"
              required
            />
          </div>

          {/* Password */}
          <div>
            <label className="block text-sm font-medium text-black mb-2">Password</label>
            <div className="relative">
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                value={formData.password}
                onChange={handleInputChange}
                placeholder="Enter your password"
                className="w-full px-5 py-4 border border-gray-300 rounded-2xl focus:outline-none focus:border-green-600 text-black placeholder:text-gray-500 pr-12"
                required
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-5 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
              >
                {showPassword ? (
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908l3.42 3.42M3 3l18 18" />
                  </svg>
                ) : (
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path strokeLinecap="round" strokeLinejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5 16.477 5 20.268 7.943 21.542 12 20.268 16.057 16.477 19 12 19 7.523 19 3.732 16.057 2.458 12z" />
                  </svg>
                )}
              </button>
            </div>
          </div>

          {/* Confirm Password */}
          <div>
            <label className="block text-sm font-medium text-black mb-2">Confirm Password</label>
            <div className="relative">
              <input
                type={showConfirmPassword ? "text" : "password"}
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleInputChange}
                placeholder="Confirm your password"
                className="w-full px-5 py-4 border border-gray-300 rounded-2xl focus:outline-none focus:border-green-600 text-black placeholder:text-gray-500 pr-12"
                required
              />
              <button
                type="button"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                className="absolute right-5 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
              >
                {showConfirmPassword ? (
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908l3.42 3.42M3 3l18 18" />
                  </svg>
                ) : (
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path strokeLinecap="round" strokeLinejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5 16.477 5 20.268 7.943 21.542 12 20.268 16.057 16.477 19 12 19 7.523 19 3.732 16.057 2.458 12z" />
                  </svg>
                )}
              </button>
            </div>
          </div>

          {/* Terms Checkbox */}
          <div className="flex items-start gap-3">
            <input
              type="checkbox"
              checked={agreeTerms}
              onChange={(e) => setAgreeTerms(e.target.checked)}
              className="w-5 h-5 mt-0.5 accent-green-600 border-gray-300 rounded cursor-pointer"
              required
            />
            <label className="text-sm text-black cursor-pointer">
              I agree to the{' '}
              <Link href="#" className="text-green-600 hover:underline">Terms of Service</Link>
              {' & '}
              <Link href="#" className="text-green-600 hover:underline">Privacy Policy</Link>
            </label>
          </div>

          {/* Create Account Button */}
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-green-600 hover:bg-green-700 disabled:bg-gray-400 text-white font-semibold py-4 rounded-2xl transition-colors text-lg disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            {loading ? 'Creating Account...' : 'Create Account →'}
          </button>
        </form>

        {/* Divider */}
        <div className="my-8 flex items-center gap-4">
          <div className="flex-1 h-px bg-gray-200"></div>
          <span className="text-gray-500 text-sm">OR</span>
          <div className="flex-1 h-px bg-gray-200"></div>
        </div>

        {/* Social Sign Up */}
        <div className="grid grid-cols-2 gap-4">
          <button className="flex items-center justify-center gap-3 border border-gray-300 hover:border-gray-400 py-4 rounded-2xl transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
              <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.51h5.92c-.25 1.36-.98 2.51-2.08 3.28v2.65h3.36c1.97-1.82 3.1-4.48 3.1-7.19z"/>
              <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.36-2.65c-.93.63-2.12 1-3.92 1-3.01 0-5.56-2.03-6.47-4.76H2.18v2.99C3.99 20.53 7.7 23 12 23z"/>
              <path fill="#FBBC05" d="M5.53 14.59c-.23-.69-.36-1.42-.36-2.19s.13-1.5.36-2.19V7.2H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.8l2.35-1.99z"/>
              <path fill="#EA4335" d="M12 4.75c1.69 0 3.2.59 4.39 1.74l3.28-3.28C17.46 1.69 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.2l3.35 2.99c.91-2.73 3.46-4.76 6.47-4.76z"/>
            </svg>
            <span className="font-medium text-black">Google</span>
          </button>

          <button className="flex items-center justify-center gap-3 border border-gray-300 hover:border-gray-400 py-4 rounded-2xl transition-colors">
            <div className="w-6 h-6 bg-[#1877F2] rounded-full flex items-center justify-center text-white text-xl font-bold">f</div>
            <span className="font-medium text-black">Facebook</span>
          </button>
        </div>

        {/* Already have account */}
        <p className="text-center mt-10 text-black">
          Already have an account?{' '}
          <Link href="/login" className="text-green-600 font-semibold hover:underline">
            Log In
          </Link>
        </p>
      </div>
    </div>
  );
}