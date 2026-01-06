import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:garbigo_frontend/core/utils/helpers.dart';
import 'package:garbigo_frontend/features/auth/providers/auth_provider.dart';
import 'package:go_router/go_router.dart';
import 'package:garbigo_frontend/routing/app_router.dart';

import '../../../core/constants/app_strings.dart';

class ForgotPasswordScreen extends ConsumerWidget {
  ForgotPasswordScreen({super.key});

  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authProvider);
    final screenSize = MediaQuery.of(context).size;

    return Scaffold(
      appBar: AppBar(title: Text(AppStrings.forgotPassword)),
      body: Center(
        child: SizedBox(
          width: screenSize.width * 0.9,
          child: Form(
            key: _formKey,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                TextFormField(
                  controller: _emailController,
                  decoration: InputDecoration(labelText: 'Email'),
                  validator: (value) => value!.isEmpty ? 'Required' : null,
                ),
                SizedBox(height: 16),
                if (authState.isLoading) CircularProgressIndicator(),
                if (authState.error != null) Text(authState.error!, style: TextStyle(color: Colors.red)),
                SizedBox(height: 16),
                ElevatedButton(
                  onPressed: () {
                    if (_formKey.currentState!.validate()) {
                      ref.read(authProvider.notifier).requestPasswordReset(_emailController.text);
                    }
                  },
                  child: Text('Send Reset Link'),
                ),
                TextButton(
                  onPressed: () => context.go(AppRouter.signinPath),
                  child: Text('Back to Signin'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}