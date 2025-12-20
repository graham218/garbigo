import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/legacy.dart';
import '../models/user_model.dart';
import '../services/user_service.dart';

final userServiceProvider = Provider<UserService>((ref) => UserService());

final userListProvider = StateNotifierProvider<UserListNotifier, AsyncValue<List<UserModel>>>((ref) {
  return UserListNotifier(ref);
});

class UserListNotifier extends StateNotifier<AsyncValue<List<UserModel>>> {
  final Ref ref;

  UserListNotifier(this.ref) : super(const AsyncValue.loading()) {
    loadAllUsers();
  }

  Future<void> loadAllUsers() async {
    state = const AsyncValue.loading();
    try {
      final users = await ref.read(userServiceProvider).getAllUsers();
      state = AsyncValue.data(users);
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> searchUsers(String query) async {
    state = const AsyncValue.loading();
    try {
      final users = await ref.read(userServiceProvider).searchUsers(query);
      state = AsyncValue.data(users);
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> activateUser(String userId) async {
    try {
      await ref.read(userServiceProvider).activateUser(userId);
      await loadAllUsers(); // Refresh list
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> deactivateUser(String userId) async {
    try {
      await ref.read(userServiceProvider).deactivateUser(userId);
      await loadAllUsers();
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> verifyEmail(String userId) async {
    try {
      await ref.read(userServiceProvider).verifyEmailAdmin(userId);
      await loadAllUsers();
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> unverifyEmail(String userId) async {
    try {
      await ref.read(userServiceProvider).unverifyEmailAdmin(userId);
      await loadAllUsers();
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> archiveUser(String userId) async {
    try {
      await ref.read(userServiceProvider).archiveUser(userId);
      await loadAllUsers();
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> unarchiveUser(String userId) async {
    try {
      await ref.read(userServiceProvider).unarchiveUser(userId);
      await loadAllUsers();
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> changeRole(String userId, UserRole newRole) async {
    try {
      await ref.read(userServiceProvider).changeRole(userId, newRole);
      await loadAllUsers();
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> deleteUser(String userId) async {
    try {
      await ref.read(userServiceProvider).deleteUser(userId);
      await loadAllUsers();
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }
}