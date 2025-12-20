import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../providers/auth_provider.dart';
import '../../widgets/custom_textfield.dart';
import '../../widgets/custom_button.dart';

class SignupScreen extends ConsumerStatefulWidget {
  const SignupScreen({super.key});

  @override
  ConsumerState<SignupScreen> createState() => _SignupScreenState();
}

class _SignupScreenState extends ConsumerState<SignupScreen> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _isClient = true;
  bool _isLoading = false;

  Future<void> _signup() async {
    setState(() => _isLoading = true);
    final success = _isClient
        ? await ref.read(authProvider.notifier).signupClient(
      _emailController.text.trim(),
      _passwordController.text,
    )
        : await ref.read(authProvider.notifier).signupCollector(
      _emailController.text.trim(),
      _passwordController.text,
    );
    setState(() => _isLoading = false);

    if (success && mounted) {
      context.go('/verify-notice');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Sign Up')),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          children: [
            CustomTextField(label: 'Email', controller: _emailController),
            const SizedBox(height: 16),
            CustomTextField(label: 'Password', obscureText: true, controller: _passwordController),
            const SizedBox(height: 24),
            Row(
              children: [
                Expanded(
                  child: RadioListTile<bool>(
                    title: const Text('Client'),
                    value: true,
                    groupValue: _isClient,
                    onChanged: (val) => setState(() => _isClient = val!),
                  ),
                ),
                Expanded(
                  child: RadioListTile<bool>(
                    title: const Text('Collector'),
                    value: false,
                    groupValue: _isClient,
                    onChanged: (val) => setState(() => _isClient = val!),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 32),
            SizedBox(
              width: double.infinity,
              child: CustomButton(
                text: 'Create Account',
                onPressed: _signup,
                isLoading: _isLoading,
              ),
            ),
          ],
        ),
      ),
    );
  }
}