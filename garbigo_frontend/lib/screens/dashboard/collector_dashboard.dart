import 'package:flutter/material.dart';

class CollectorDashboard extends StatelessWidget {
  const CollectorDashboard({super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(24.0),
      child: Column(
        children: [
          const Icon(Icons.local_shipping, size: 100, color: Color(0xFF22C55E)),
          const SizedBox(height: 32),
          const Text('Collector Dashboard', style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold)),
          const SizedBox(height: 16),
          const Text('Manage your daily routes and collections', style: TextStyle(fontSize: 18), textAlign: TextAlign.center),
          const SizedBox(height: 40),
          _buildRouteCard('Route A - Downtown', '12 collections pending'),
          _buildRouteCard('Route B - Suburbs', '8 collections pending'),
          _buildRouteCard('Route C - Industrial', '15 collections pending'),
        ],
      ),
    );
  }

  Widget _buildRouteCard(String route, String status) {
    return Card(
      elevation: 4,
      margin: const EdgeInsets.symmetric(vertical: 8),
      child: ListTile(
        title: Text(route, style: const TextStyle(fontWeight: FontWeight.bold)),
        subtitle: Text(status),
        trailing: const Icon(Icons.navigate_next),
        onTap: () {},
      ),
    );
  }
}