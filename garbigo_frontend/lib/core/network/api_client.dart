import 'package:dio/dio.dart';
import 'package:garbigo_frontend/core/config/app_config.dart';
import 'package:garbigo_frontend/core/constants/app_strings.dart';
import 'package:garbigo_frontend/features/auth/providers/auth_provider.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

final dioProvider = Provider<Dio>((ref) {
  final dio = Dio(BaseOptions(
    baseUrl: AppConfig.baseUrl,
    connectTimeout: Duration(seconds: 5),
    receiveTimeout: Duration(seconds: 3),
  ));

  dio.interceptors.add(InterceptorsWrapper(
    onRequest: (options, handler) {
      final token = ref.read(authProvider).token;
      if (token != null) {
        options.headers['Authorization'] = 'Bearer $token';
      }
      return handler.next(options);
    },
    onError: (error, handler) {
      // Handle errors globally, e.g., token refresh or logout
      if (error.response?.statusCode == 401) {
        ref.read(authProvider.notifier).logout();
      }
      return handler.next(error);
    },
  ));

  return dio;
});