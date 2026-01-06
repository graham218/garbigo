class AppConfig {
  static const String baseUrl = 'http://localhost:8080';
  // For physical device or web: use your machine IP
  // static const String baseUrl = 'http://192.168.1.100:8080';
  static const String authBase = '$baseUrl/auth';
  static const String usersBase = '$baseUrl/users';
}