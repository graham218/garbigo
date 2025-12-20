import 'dart:convert';
import 'package:http/http.dart' as http;
import '../core/constants/endpoints.dart';
import '../models/user_model.dart';
import 'api_service.dart';

class AuthService {
  final ApiService _api = ApiService();

  Future<Map<String, dynamic>?> login(String email, String password) async {
    final response = await _api.post(Endpoints.login, {
      'email': email,
      'password': password,
    });

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    }
    throw Exception(jsonDecode(response.body)['message'] ?? 'Login failed');
  }

  Future<bool> signupClient(String email, String password) async {
    final response = await _api.post(Endpoints.signupClient, {
      'email': email,
      'password': password,
    });
    return response.statusCode == 200;
  }

  Future<bool> signupCollector(String email, String password) async {
    final response = await _api.post(Endpoints.signupCollector, {
      'email': email,
      'password': password,
    });
    return response.statusCode == 200;
  }

  Future<void> verifyEmail(String token) async {
    final response = await http.get(Uri.parse('${Endpoints.verifyEmail}?token=$token'));
    if (response.statusCode != 200) {
      throw Exception('Verification failed');
    }
  }

  Future<void> forgotPassword(String email) async {
    final response = await _api.post(Endpoints.forgotPassword, {'email': email});
    if (response.statusCode != 200) throw Exception('Failed to send reset link');
  }

  Future<void> resetPassword(String token, String newPassword) async {
    final response = await _api.post(Endpoints.resetPassword + '?token=$token', {
      'email': newPassword, // backend uses email field for new password
    });
    if (response.statusCode != 200) throw Exception('Reset failed');
  }

  Future<void> changePassword(String oldPassword, String newPassword, String confirmPassword) async {
    final response = await _api.post(Endpoints.changePassword, {
      'oldPassword': oldPassword,
      'newPassword': newPassword,
      'confirmPassword': confirmPassword,
    });
    if (response.statusCode != 200) throw Exception('Change password failed');
  }

  Future<UserModel> updateProfile({
    String? firstName,
    String? middleName,
    String? lastName,
    String? phoneNumber,
    List<http.MultipartFile>? profilePicture,
  }) async {
    final requestFields = <String, String>{};
    if (firstName != null) requestFields['firstName'] = firstName;
    if (middleName != null) requestFields['middleName'] = middleName;
    if (lastName != null) requestFields['lastName'] = lastName;
    if (phoneNumber != null) requestFields['phoneNumber'] = phoneNumber;

    final response = await _api.multipartPost(
      Endpoints.updateProfile,
      requestFields,
      files: profilePicture,
    );

    if (response.statusCode == 200) {
      final json = jsonDecode(response.body);
      return UserModel.fromJson(json);
    }
    throw Exception('Profile update failed');
  }
}