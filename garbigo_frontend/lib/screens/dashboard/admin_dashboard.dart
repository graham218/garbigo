import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class AdminDashboard extends StatelessWidget {
  const AdminDashboard({super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(24.0),
      child: Column(
        children: [
          const Icon(Icons.admin_panel_settings, size: 100, color: Color(0xFF22C55E)),
          const SizedBox(height: 32),
          const Text('Admin Dashboard', style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold)),
          const SizedBox(height: 16),
          const Text('System overview and management', style: TextStyle(fontSize: 18)),
          const SizedBox(height: 40),
          ElevatedButton.icon(
            icon: const Icon(Icons.people),
            label: const Text('Manage Users'),
            style: ElevatedButton.styleFrom(padding: const EdgeInsets.symmetric(vertical: 16), minimumSize: const Size(double.infinity, 56)),
            onPressed: () => context.push('/admin/users'),
          ),
          const SizedBox(height: 16),
          ElevatedButton.icon(
            icon: const Icon(Icons.analytics),
            label: const Text('System Reports'),
            style: ElevatedButton.styleFrom(padding: const EdgeInsets.symmetric(vertical: 16), minimumSize: const Size(double.infinity, 56)),
            onPressed: () {},
          ),
        ],
      ),
    );
  }
}