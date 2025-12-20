class Endpoints {
  static const String baseUrl = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'http://10.0.2.2:8080/api',
  );

  // Auth
  static const String login = '$baseUrl/auth/signin';
  static const String signupClient = '$baseUrl/auth/signup/client';
  static const String signupCollector = '$baseUrl/auth/signup/collector';
  static const String verifyEmail = '$baseUrl/auth/verify-email';
  static const String forgotPassword = '$baseUrl/auth/forgot-password';
  static const String resetPassword = '$baseUrl/auth/reset-password';
  static const String changePassword = '$baseUrl/auth/change-password';
  static const String updateProfile = '$baseUrl/auth/profile';

  // Admin
  static const String searchUsers = '$baseUrl/admin/users/search';
  static const String getAllUsers = '$baseUrl/admin/users';
  static String updateUser(String id) => '$baseUrl/admin/users/$id';
  static String deleteUser(String id) => '$baseUrl/admin/users/$id';
}