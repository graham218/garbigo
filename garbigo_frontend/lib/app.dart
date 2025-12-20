import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'screens/auth/login_screen.dart';
import 'screens/auth/signup_screen.dart';
import 'screens/auth/forgot_password_screen.dart';
import 'screens/auth/verify_email_notice_screen.dart';
import 'screens/dashboard/home_screen.dart';
import 'screens/profile/profile_screen.dart';
import 'screens/admin/user_management_screen.dart';
import 'providers/auth_provider.dart';

final _router = GoRouter(
  initialLocation: '/login',
  redirect: (context, state) {
    final authState = context.read(authProvider);
    final isLoggedIn = authState.value != null;
    final isAuthPath = state.subloc.startsWith('/login') ||
        state.subloc.startsWith('/signup') ||
        state.subloc.startsWith('/forgot') ||
        state.subloc.startsWith('/verify');

    if (!isLoggedIn && !isAuthPath) return '/login';
    if (isLoggedIn && isAuthPath) return '/home';
    return null;
  },
  routes: [
    GoRoute(
      path: '/login',
      builder: (context, state) => const LoginScreen(),
    ),
    GoRoute(
      path: '/signup',
      builder: (context, state) => const SignupScreen(),
    ),
    GoRoute(
      path: '/forgot',
      builder: (context, state) => const ForgotPasswordScreen(),
    ),
    GoRoute(
      path: '/verify-notice',
      builder: (context, state) => const VerifyEmailNoticeScreen(),
    ),
    GoRoute(
      path: '/home',
      builder: (context, state) => const HomeScreen(),
    ),
    GoRoute(
      path: '/profile',
      builder: (context, state) => const ProfileScreen(),
    ),
    GoRoute(
      path: '/admin/users',
      builder: (context, state) => const UserManagementScreen(),
    ),
  ],
);

class GarbigoApp extends ConsumerWidget {
  const GarbigoApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return MaterialApp.router(
      title: 'Garbigo',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF22C55E)),
        useMaterial3: true,
      ),
      routerConfig: _router,
    );
  }
}