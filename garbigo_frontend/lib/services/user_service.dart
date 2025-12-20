import 'dart:convert';
import '../core/constants/endpoints.dart';
import '../models/user_model.dart';
import 'api_service.dart';

class UserService {
  final ApiService _api = ApiService();

  Future<List<UserModel>> getAllUsers() async {
    final response = await _api.get(Endpoints.getAllUsers);
    final List<dynamic> data = jsonDecode(response.body);
    return data.map((json) => UserModel.fromJson(json)).toList();
  }

  Future<List<UserModel>> searchUsers(String query) async {
    final response = await _api.get('${Endpoints.searchUsers}?q=$query');
    final List<dynamic> data = jsonDecode(response.body);
    return data.map((json) => UserModel.fromJson(json)).toList();
  }

  Future<void> activateUser(String userId) async {
    await _api.patch(Endpoints.updateUser(userId), {
      'accountStatus': 'ACTIVE',
      'emailVerified': true,
    });
  }

  Future<void> deactivateUser(String userId) async {
    await _api.patch(Endpoints.updateUser(userId), {
      'accountStatus': 'SUSPENDED',
    });
  }

  Future<void> verifyEmailAdmin(String userId) async {
    await _api.patch(Endpoints.updateUser(userId), {'emailVerified': true});
  }

  Future<void> unverifyEmailAdmin(String userId) async {
    await _api.patch(Endpoints.updateUser(userId), {'emailVerified': false});
  }

  Future<void> archiveUser(String userId) async {
    await _api.patch(Endpoints.updateUser(userId), {'accountStatus': 'ARCHIVED'});
  }

  Future<void> unarchiveUser(String userId) async {
    await _api.patch(Endpoints.updateUser(userId), {'accountStatus': 'ACTIVE'});
  }

  Future<void> changeRole(String userId, UserRole role) async {
    await _api.patch(Endpoints.updateUser(userId), {'role': role.name});
  }

  Future<void> deleteUser(String userId) async {
    await _api.delete(Endpoints.deleteUser(userId));
  }
}