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

final GoRouter router = GoRouter(
  initialLocation: '/login',
  redirect: (context, state) {
    // Safely access the Riverpod container
    final container = ProviderScope.containerOf(context, listen: false);
    final authState = container.read(authProvider);

    final bool isLoggedIn = authState.hasValue && authState.value != null;
    final String currentPath = state.uri.path;

    const List<String> authPaths = ['/login', '/signup', '/forgot', '/verify-notice'];

    // If not logged in → force to login (except auth pages)
    if (!isLoggedIn && !authPaths.contains(currentPath)) {
      return '/login';
    }

    // If logged in → don't allow access to auth pages
    if (isLoggedIn && authPaths.contains(currentPath)) {
      return '/home';
    }

    // No redirect needed
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

class GarbigoApp extends StatelessWidget {
  const GarbigoApp({super.key});

  @override
  Widget build(BuildContext context) {
    return ProviderScope(
      child: MaterialApp.router(
        title: 'Garbigo',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF22C55E)),
          useMaterial3: true,
        ),
        routerConfig: router,
      ),
    );
  }
}