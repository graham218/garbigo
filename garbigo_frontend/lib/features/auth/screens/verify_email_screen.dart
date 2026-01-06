import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:garbigo_frontend/core/constants/app_strings.dart';
import 'package:garbigo_frontend/features/auth/providers/auth_provider.dart';

class VerifyEmailScreen extends ConsumerWidget {
  final String token;

  const VerifyEmailScreen({super.key, required this.token});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authNotifier = ref.read(authProvider.notifier);
    final authState = ref.watch(authProvider);

    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (!authState.isLoading) {
        authNotifier.verifyEmail(token);
      }
    });

    return Scaffold(
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(32.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.mark_email_read, size: 100, color: Colors.green),
              const SizedBox(height: 32),
              Text(AppStrings.verifyEmail, style: Theme.of(context).textTheme.headlineMedium),
              const SizedBox(height: 16),
              if (authState.isLoading)
                const CircularProgressIndicator()
              else if (authState.error != null)
                Text('Verification failed: ${authState.error}', style: const TextStyle(color: Colors.red))
              else
                const Text('Your email has been verified successfully!'),
              const SizedBox(height: 32),
              ElevatedButton(
                onPressed: () => context.go('/signin'),
                child: const Text('Go to Sign In'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}