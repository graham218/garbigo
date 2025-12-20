import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../providers/user_provider.dart';
import '../../models/user_model.dart';

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
    ref.read(userListProvider.notifier).loadAllUsers();
  }

  @override
  Widget build(BuildContext context) {
    final userState = ref.watch(userListProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('User Management')),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: TextField(
              controller: _searchController,
              decoration: InputDecoration(
                labelText: 'Search by email, name, phone...',
                suffixIcon: IconButton(
                  icon: const Icon(Icons.search),
                  onPressed: () => ref.read(userListProvider.notifier).searchUsers(_searchController.text),
                ),
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
              ),
              onSubmitted: (q) => ref.read(userListProvider.notifier).searchUsers(q),
            ),
          ),
          Expanded(
            child: userState.when(
              data: (users) => ListView.builder(
                itemCount: users.length,
                itemBuilder: (context, index) {
                  final user = users[index];
                  return ListTile(
                    leading: CircleAvatar(child: Text(user.fullName[0].toUpperCase())),
                    title: Text(user.fullName),
                    subtitle: Text('${user.email} • ${user.role.name}'),
                    trailing: PopupMenuButton(
                      onSelected: (action) async {
                        if (action == 'delete') {
                          await ref.read(userListProvider.notifier).deleteUser(user.id);
                        } else if (action == 'activate') {
                          await ref.read(userListProvider.notifier).updateUser(user.id, status: AccountStatus.ACTIVE, emailVerified: true);
                        }
                      },
                      itemBuilder: (context) => [
                        const PopupMenuItem(value: 'activate', child: Text('Activate & Verify')),
                        const PopupMenuItem(value: 'delete', child: Text('Delete')),
                      ],
                    ),
                  );
                },
              ),
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (err, _) => Center(child: Text('Error: $err')),
            ),
          ),
        ],
      ),
    );
  }
}