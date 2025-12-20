import 'package:flutter/material.dart';

class SupportDashboard extends StatelessWidget {
  const SupportDashboard({super.key});

  @override
  Widget build(BuildContext context) {
    return const Center(
      child: Padding(
        padding: EdgeInsets.all(24.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.support_agent, size: 100, color: Color(0xFF22C55E)),
            SizedBox(height: 32),
            Text('Support Dashboard', style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold)),
            SizedBox(height: 16),
            Text('Customer tickets and queries', textAlign: TextAlign.center, style: TextStyle(fontSize: 18)),
          ],
        ),
      ),
    );
  }
}