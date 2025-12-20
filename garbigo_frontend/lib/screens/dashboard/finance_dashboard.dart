import 'package:flutter/material.dart';

class FinanceDashboard extends StatelessWidget {
  const FinanceDashboard({super.key});

  @override
  Widget build(BuildContext context) {
    return const Center(
      child: Padding(
        padding: EdgeInsets.all(24.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.account_balance_wallet, size: 100, color: Color(0xFF22C55E)),
            SizedBox(height: 32),
            Text('Finance Dashboard', style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold)),
            SizedBox(height: 16),
            Text('Invoices, payments, and financial reports', textAlign: TextAlign.center, style: TextStyle(fontSize: 18)),
          ],
        ),
      ),
    );
  }
}