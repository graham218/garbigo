import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:garbigo_frontend/core/config/app_config.dart';
import 'package:garbigo_frontend/core/constants/app_themes.dart';
import 'package:garbigo_frontend/routing/app_router.dart';
import 'package:garbigo_frontend/features/auth/providers/auth_provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  AppConfig.init();  // Initialize app config if needed
  final prefs = await SharedPreferences.getInstance();
  runApp(ProviderScope(
    overrides: [
      sharedPreferencesProvider.overrideWithValue(prefs),
    ],
    child: const MyApp(),
  ));
}

final sharedPreferencesProvider = Provider<SharedPreferences>((ref) {
  throw UnimplementedError();
});

class MyApp extends ConsumerWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // Listen to auth state to handle global loading/error
    ref.watch(authProvider);

    return MaterialApp.router(
      title: 'Garbigo',
      theme: AppThemes.lightTheme,  // Use your custom theme
      darkTheme: AppThemes.darkTheme,
      themeMode: ThemeMode.system,  // Or use Riverpod for theme switching
      routerConfig: AppRouter.router,  // GoRouter setup
      debugShowCheckedModeBanner: false,
    );
  }
}