import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../models/user_model.dart';
import '../../providers/auth_provider.dart';
import 'dashboard_scaffold.dart';
import 'client_dashboard.dart';
import 'collector_dashboard.dart';
import 'admin_dashboard.dart';
import 'operations_dashboard.dart';
import 'finance_dashboard.dart';
import 'support_dashboard.dart';

class HomeScreen extends ConsumerWidget {
  const HomeScreen({super.key});

  Widget _getDashboardContent(UserRole role) {
    switch (role) {
      case UserRole.CLIENT:
        return const ClientDashboard();
      case UserRole.COLLECTOR:
        return const CollectorDashboard();
      case UserRole.ADMIN:
        return const AdminDashboard();
      case UserRole.OPERATIONS:
        return const OperationsDashboard();
      case UserRole.FINANCE:
        return const FinanceDashboard();
      case UserRole.SUPPORT:
        return const SupportDashboard();
    }
  }

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authProvider);

    return authState.when(
      data: (user) {
        if (user == null) {
          return const Scaffold(body: Center(child: Text('Not authenticated')));
        }

        return DashboardScaffold(
          title: 'Welcome, ${user.fullName}',
          body: _getDashboardContent(user.role),
        );
      },
      loading: () => const Scaffold(body: Center(child: CircularProgressIndicator())),
      error: (err, _) => Scaffold(body: Center(child: Text('Error: $err'))),
    );
  }
}