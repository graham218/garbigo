import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/user_model.dart';
import '../services/user_service.dart';

final userServiceProvider = Provider<UserService>((ref) => UserService());

final userListProvider = StateNotifierProvider<UserListNotifier, AsyncValue<List<UserModel>>>((ref) {
  return UserListNotifier(ref);
});

class UserListNotifier extends StateNotifier<AsyncValue<List<UserModel>>> {
  final Ref _ref;

  UserListNotifier(this._ref) : super(const AsyncValue.loading()) {
    loadAllUsers();
  }

  Future<void> loadAllUsers() async {
    state = const AsyncValue.loading();
    try {
      final users = await _ref.read(userServiceProvider).getAllUsers();
      state = AsyncValue.data(users);
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> searchUsers(String query) async {
    state = const AsyncValue.loading();
    try {
      final users = await _ref.read(userServiceProvider).searchUsers(query);
      state = AsyncValue.data(users);
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> updateUser(
      String userId, {
        UserRole? role,
        AccountStatus? status,
        bool? emailVerified,
      }) async {
    try {
      final updatedUser = await _ref.read(userServiceProvider).updateUser(
        userId,
        role: role,
        status: status,
        emailVerified: emailVerified,
      );

      state.whenData((users) {
        final updatedList = users.map((u) => u.id == userId ? updatedUser : u).toList();
        state = AsyncValue.data(updatedList);
      });
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> deleteUser(String userId) async {
    try {
      await _ref.read(userServiceProvider).deleteUser(userId);
      state.whenData((users) {
        final filtered = users.where((u) => u.id != userId).toList();
        state = AsyncValue.data(filtered);
      });
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }
}