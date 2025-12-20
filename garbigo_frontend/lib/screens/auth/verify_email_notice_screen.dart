import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class VerifyEmailNoticeScreen extends StatelessWidget {
  const VerifyEmailNoticeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(32.0),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Icon(Icons.mark_email_read, size: 100, color: Color(0xFF22C55E)),
              const SizedBox(height: 32),
              const Text(
                'Check Your Email!',
                style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 16),
              const Text(
                'We\'ve sent a verification link to your email. Please click it to activate your account.',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 16),
              ),
              const SizedBox(height: 48),
              ElevatedButton(
                onPressed: () => context.go('/login'),
                child: const Text('Back to Login'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}