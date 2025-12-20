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
  final _confirmPasswordController = TextEditingController();
  bool _isClient = true;
  bool _isLoading = false;
  String? _errorMessage;

  Future<void> _signup() async {
    if (_passwordController.text != _confirmPasswordController.text) {
      setState(() {
        _errorMessage = 'Passwords do not match';
      });
      return;
    }

    if (_emailController.text.trim().isEmpty || _passwordController.text.isEmpty) {
      setState(() {
        _errorMessage = 'Please fill all fields';
      });
      return;
    }

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

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
    } else {
      setState(() {
        _errorMessage = 'Signup failed. Email may already exist.';
      });
    }
  }

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Create Account'),
        backgroundColor: const Color(0xFF22C55E),
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const Icon(
              Icons.recycling,
              size: 100,
              color: Color(0xFF22C55E),
            ),
            const SizedBox(height: 32),
            const Text(
              'Join Garbigo',
              style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 8),
            const Text(
              'Choose your role to get started',
              style: TextStyle(fontSize: 16, color: Colors.grey),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 40),

            CustomTextField(
              label: 'Email Address',
              controller: _emailController,
            ),
            const SizedBox(height: 16),
            CustomTextField(
              label: 'Password',
              obscureText: true,
              controller: _passwordController,
            ),
            const SizedBox(height: 16),
            CustomTextField(
              label: 'Confirm Password',
              obscureText: true,
              controller: _confirmPasswordController,
            ),
            const SizedBox(height: 32),

            // Role Selection
            const Text('I am a:', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: RadioListTile<bool>(
                    title: const Text('Client', style: TextStyle(fontSize: 18)),
                    subtitle: const Text('Request waste collection'),
                    value: true,
                    groupValue: _isClient,
                    onChanged: (val) => setState(() => _isClient = val!),
                    activeColor: const Color(0xFF22C55E),
                  ),
                ),
                Expanded(
                  child: RadioListTile<bool>(
                    title: const Text('Collector', style: TextStyle(fontSize: 18)),
                    subtitle: const Text('Collect waste'),
                    value: false,
                    groupValue: _isClient,
                    onChanged: (val) => setState(() => _isClient = val!),
                    activeColor: const Color(0xFF22C55E),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 32),

            if (_errorMessage != null)
              Padding(
                padding: const EdgeInsets.only(bottom: 16),
                child: Text(
                  _errorMessage!,
                  style: const TextStyle(color: Colors.red, fontSize: 16),
                  textAlign: TextAlign.center,
                ),
              ),

            CustomButton(
              text: 'Create Account',
              onPressed: _signup,
              isLoading: _isLoading,
            ),
            const SizedBox(height: 24),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Text('Already have an account?'),
                TextButton(
                  onPressed: () => context.go('/login'),
                  child: const Text('Login'),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}