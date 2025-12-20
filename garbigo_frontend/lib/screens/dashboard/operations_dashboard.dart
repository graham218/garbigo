import 'package:flutter/material.dart';

class OperationsDashboard extends StatelessWidget {
  const OperationsDashboard({super.key});

  @override
  Widget build(BuildContext context) {
    return const Center(
      child: Padding(
        padding: EdgeInsets.all(24.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.settings_suggest, size: 100, color: Color(0xFF22C55E)),
            SizedBox(height: 32),
            Text('Operations Dashboard', style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold)),
            SizedBox(height: 16),
            Text('Manage fleet, routes, and logistics', textAlign: TextAlign.center, style: TextStyle(fontSize: 18)),
          ],
        ),
      ),
    );
  }
}