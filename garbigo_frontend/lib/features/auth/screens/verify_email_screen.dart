import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:garbigo_frontend/core/utils/helpers.dart';
import 'package:garbigo_frontend/features/auth/providers/auth_provider.dart';
import 'package:go_router/go_router.dart';

class VerifyEmailScreen extends ConsumerWidget {
  final String token;

  VerifyEmailScreen({super.key, required this.token});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      ref.read(authProvider.notifier).verifyAccount(token);
    });

    final authState = ref.watch(authProvider);

    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            if (authState.isLoading) const CircularProgressIndicator(),
            if (authState.error != null) Text(authState.error!, style: const TextStyle(color: Colors.red)),
            if (!authState.isLoading && authState.error == null) const Text('Account verified! You can now sign in.'),
            ElevatedButton(
              onPressed: () => context.go(AppRouter.signinPath),
              child: const Text('Go to Signin'),
            ),
          ],
        ),
      ),
    );
  }
}