import 'dart:convert';
import '../core/constants/endpoints.dart';
import '../models/user_model.dart';
import 'api_service.dart';

class UserService {
  final ApiService _api = ApiService();

  Future<List<UserModel>> searchUsers(String query) async {
    final response = await _api.get('${Endpoints.searchUsers}?q=$query');
    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);
      return data.map((json) => UserModel.fromJson(json)).toList();
    }
    throw Exception('Search failed');
  }

  Future<List<UserModel>> getAllUsers() async {
    final response = await _api.get(Endpoints.getAllUsers);
    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);
      return data.map((json) => UserModel.fromJson(json)).toList();
    }
    throw Exception('Failed to load users');
  }

  Future<UserModel> updateUser(String userId, {
    UserRole? role,
    AccountStatus? status,
    bool? emailVerified,
  }) async {
    final Map<String, dynamic> body = {};
    if (role != null) body['role'] = role.name;
    if (status != null) body['accountStatus'] = status.name;
    if (emailVerified != null) body['emailVerified'] = emailVerified;

    final response = await _api.patch(Endpoints.updateUser(userId), body);
    if (response.statusCode == 200) {
      return UserModel.fromJson(jsonDecode(response.body));
    }
    throw Exception('Update failed');
  }

  Future<void> deleteUser(String userId) async {
    final response = await _api.delete(Endpoints.deleteUser(userId));
    if (response.statusCode != 204) throw Exception('Delete failed');
  }
}