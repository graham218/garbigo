import 'package:flutter/material.dart';

class ClientDashboard extends StatelessWidget {
  const ClientDashboard({super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(24.0),
      child: Column(
        children: [
          const Icon(Icons.home, size: 100, color: Color(0xFF22C55E)),
          const SizedBox(height: 32),
          const Text(
            'Client Dashboard',
            style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 16),
          const Text(
            'Manage your waste collection schedule',
            style: TextStyle(fontSize: 18),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 40),
          _buildFeatureCard(
            icon: Icons.schedule,
            title: 'Schedule Pickup',
            subtitle: 'Book a new collection',
          ),
          _buildFeatureCard(
            icon: Icons.history,
            title: 'Collection History',
            subtitle: 'View past pickups',
          ),
          _buildFeatureCard(
            icon: Icons.bar_chart,
            title: 'Reports',
            subtitle: 'Waste statistics',
          ),
        ],
      ),
    );
  }

  Widget _buildFeatureCard({required IconData icon, required String title, required String subtitle}) {
    return Card(
      elevation: 4,
      margin: const EdgeInsets.symmetric(vertical: 8),
      child: ListTile(
        leading: Icon(icon, size: 40, color: const Color(0xFF22C55E)),
        title: Text(title, style: const TextStyle(fontWeight: FontWeight.bold)),
        subtitle: Text(subtitle),
        trailing: const Icon(Icons.arrow_forward_ios),
        onTap: () {},
      ),
    );
  }
}