import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'app.dart';

Future<void> main() async {
  // Ensure Flutter bindings are initialized
  WidgetsFlutterBinding.ensureInitialized();

  // Load .env file — but DON'T crash if it's missing or has errors
  try {
    await dotenv.load(fileName: ".env");
    print(".env file loaded successfully");
  } catch (e) {
    print(".env file not found or failed to load — continuing without it");
    print("   Error: $e");
    // App continues even if .env is missing (good for testing)
  }

  // Run the app wrapped in ProviderScope
  runApp(
    const ProviderScope(
      child: GarbigoApp(),
    ),
  );
}