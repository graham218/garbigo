import 'dart:convert';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/legacy.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/user_model.dart';
import '../services/auth_service.dart';

final authServiceProvider = Provider<AuthService>((ref) => AuthService());

final authProvider = StateNotifierProvider<AuthNotifier, AsyncValue<UserModel?>>((ref) {
  return AuthNotifier(ref);
});

class AuthNotifier extends StateNotifier<AsyncValue<UserModel?>> {
  final Ref _ref;
  AuthNotifier(this._ref) : super(const AsyncValue.loading()) {
    checkAuthStatus();
  }

  Future<void> checkAuthStatus() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('jwt_token');
      final userJson = prefs.getString('user_data');

      if (token != null && userJson != null) {
        final userMap = jsonDecode(userJson);
        state = AsyncValue.data(UserModel.fromJson(userMap));
      } else {
        state = const AsyncValue.data(null);
      }
    } catch (e) {
      state = AsyncValue.error(e, StackTrace.current);
    }
  }

  Future<bool> login(String email, String password) async {
    state = const AsyncValue.loading();
    try {
      final response = await _ref.read(authServiceProvider).login(email, password);
      final user = UserModel.fromJson(response!);

      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('jwt_token', response['token']);
      await prefs.setString('user_data', jsonEncode(response));

      state = AsyncValue.data(user);
      return true;
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
      return false;
    }
  }

  Future<bool> signupClient(String email, String password) async {
    try {
      final success = await _ref.read(authServiceProvider).signupClient(email, password);
      return success;
    } catch (e) {
      state = AsyncValue.error(e, StackTrace.current);
      return false;
    }
  }

  Future<bool> signupCollector(String email, String password) async {
    try {
      final success = await _ref.read(authServiceProvider).signupCollector(email, password);
      return success;
    } catch (e) {
      state = AsyncValue.error(e, StackTrace.current);
      return false;
    }
  }

  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.clear();
    state = const AsyncValue.data(null);
  }

  // Helper to get current user safely
  UserModel? get currentUser => state.value;
}