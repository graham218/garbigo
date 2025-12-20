import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../models/user_model.dart';
import '../../providers/user_provider.dart';

class UserManagementScreen extends ConsumerStatefulWidget {
  const UserManagementScreen({super.key});

  @override
  ConsumerState<UserManagementScreen> createState() => _UserManagementScreenState();
}

class _UserManagementScreenState extends ConsumerState<UserManagementScreen> {
  final _searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    // Load all users when screen opens
    ref.read(userListProvider.notifier).loadAllUsers();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  void _showUserActionsBottomSheet(String userId, UserModel user) {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (context) => SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // User Info Header
              Row(
                children: [
                  CircleAvatar(
                    radius: 30,
                    backgroundImage: user.profilePictureUrl != null
                        ? NetworkImage(user.profilePictureUrl!)
                        : null,
                    child: user.profilePictureUrl == null
                        ? Text(user.fullName[0].toUpperCase())
                        : null,
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          user.fullName,
                          style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                        ),
                        Text(
                          user.email,
                          style: const TextStyle(color: Colors.grey),
                        ),
                        Text(
                          '${user.role.name} • ${user.accountStatus.name}',
                          style: const TextStyle(fontSize: 12, color: Colors.grey),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
              const Divider(height: 32),

              // Activate / Deactivate
              ListTile(
                leading: Icon(
                  user.accountStatus == AccountStatus.ACTIVE
                      ? Icons.block
                      : Icons.check_circle,
                  color: user.accountStatus == AccountStatus.ACTIVE ? Colors.orange : Colors.green,
                ),
                title: Text(
                  user.accountStatus == AccountStatus.ACTIVE
                      ? 'Deactivate User'
                      : 'Activate User',
                ),
                onTap: () async {
                  Navigator.pop(context);
                  if (user.accountStatus == AccountStatus.ACTIVE) {
                    await ref.read(userListProvider.notifier).deactivateUser(userId);
                  } else {
                    await ref.read(userListProvider.notifier).activateUser(userId);
                  }
                },
              ),

              // Verify / Unverify Email
              ListTile(
                leading: Icon(
                  user.isEmailVerified ? Icons.mark_email_unread : Icons.mark_email_read,
                  color: user.isEmailVerified ? Colors.red : Colors.green,
                ),
                title: Text(
                  user.isEmailVerified ? 'Unverify Email' : 'Verify Email',
                ),
                onTap: () async {
                  Navigator.pop(context);
                  if (user.isEmailVerified) {
                    await ref.read(userListProvider.notifier).unverifyEmail(userId);
                  } else {
                    await ref.read(userListProvider.notifier).verifyEmail(userId);
                  }
                },
              ),

              // Archive / Unarchive
              ListTile(
                leading: Icon(
                  user.accountStatus == AccountStatus.ARCHIVED
                      ? Icons.unarchive
                      : Icons.archive,
                  color: Colors.blue,
                ),
                title: Text(
                  user.accountStatus == AccountStatus.ARCHIVED
                      ? 'Unarchive User'
                      : 'Archive User',
                ),
                onTap: () async {
                  Navigator.pop(context);
                  if (user.accountStatus == AccountStatus.ARCHIVED) {
                    await ref.read(userListProvider.notifier).unarchiveUser(userId);
                  } else {
                    await ref.read(userListProvider.notifier).archiveUser(userId);
                  }
                },
              ),

              const Divider(),

              // Change Role (Simple dropdown for now)
              ListTile(
                leading: const Icon(Icons.swap_horiz),
                title: const Text('Change Role'),
                onTap: () {
                  Navigator.pop(context);
                  _showChangeRoleDialog(userId, user.role);
                },
              ),

              const Divider(),

              // Delete User (Dangerous action)
              ListTile(
                leading: const Icon(Icons.delete_forever, color: Colors.red),
                title: const Text('Delete User', style: TextStyle(color: Colors.red)),
                onTap: () {
                  Navigator.pop(context);
                  _showDeleteConfirmationDialog(userId, user.fullName);
                },
              ),

              const SizedBox(height: 16),
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('Cancel'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _showChangeRoleDialog(String userId, UserRole currentRole) {
    UserRole? selectedRole = currentRole;

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Change User Role'),
        content: DropdownButton<UserRole>(
          value: selectedRole,
          isExpanded: true,
          items: UserRole.values.map((role) {
            return DropdownMenuItem(
              value: role,
              child: Text(role.name),
            );
          }).toList(),
          onChanged: (value) {
            selectedRole = value;
          },
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
          ElevatedButton(
            onPressed: () async {
              if (selectedRole != null && selectedRole != currentRole) {
                await ref.read(userListProvider.notifier).changeRole(userId, selectedRole!);
              }
              Navigator.pop(context);
            },
            child: const Text('Update Role'),
          ),
        ],
      ),
    );
  }

  void _showDeleteConfirmationDialog(String userId, String userName) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete User'),
        content: Text('Are you sure you want to permanently delete "$userName"? This action cannot be undone.'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
          TextButton(
            onPressed: () async {
              Navigator.pop(context);
              await ref.read(userListProvider.notifier).deleteUser(userId);
            },
            child: const Text('Delete', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final userState = ref.watch(userListProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('User Management'),
        backgroundColor: const Color(0xFF22C55E),
        foregroundColor: Colors.white,
      ),
      body: Column(
        children: [
          // Search Bar
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: TextField(
              controller: _searchController,
              decoration: InputDecoration(
                hintText: 'Search by name, email, phone, city...',
                prefixIcon: const Icon(Icons.search),
                suffixIcon: IconButton(
                  icon: const Icon(Icons.clear),
                  onPressed: () {
                    _searchController.clear();
                    ref.read(userListProvider.notifier).loadAllUsers();
                  },
                ),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
                filled: true,
                fillColor: Colors.grey[100],
              ),
              onSubmitted: (value) {
                if (value.trim().isNotEmpty) {
                  ref.read(userListProvider.notifier).searchUsers(value.trim());
                } else {
                  ref.read(userListProvider.notifier).loadAllUsers();
                }
              },
            ),
          ),

          // User List
          Expanded(
            child: userState.when(
              data: (users) {
                if (users.isEmpty) {
                  return const Center(child: Text('No users found'));
                }
                return ListView.builder(
                  itemCount: users.length,
                  itemBuilder: (context, index) {
                    final user = users[index];
                    return Card(
                      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                      child: ListTile(
                        leading: CircleAvatar(
                          backgroundImage: user.profilePictureUrl != null
                              ? NetworkImage(user.profilePictureUrl!)
                              : null,
                          child: user.profilePictureUrl == null
                              ? Text(user.fullName[0].toUpperCase())
                              : null,
                        ),
                        title: Text(user.fullName, style: const TextStyle(fontWeight: FontWeight.bold)),
                        subtitle: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(user.email),
                            Text('${user.role.name} • ${user.accountStatus.name}'),
                            if (user.fullAddress != 'No address') Text(user.fullAddress, style: const TextStyle(fontSize: 12)),
                          ],
                        ),
                        trailing: IconButton(
                          icon: const Icon(Icons.more_vert),
                          onPressed: () => _showUserActionsBottomSheet(user.id, user),
                        ),
                      ),
                    );
                  },
                );
              },
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (error, stack) => Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Icon(Icons.error, color: Colors.red, size: 60),
                    const SizedBox(height: 16),
                    Text('Error: $error'),
                    ElevatedButton(
                      onPressed: () => ref.read(userListProvider.notifier).loadAllUsers(),
                      child: const Text('Retry'),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}