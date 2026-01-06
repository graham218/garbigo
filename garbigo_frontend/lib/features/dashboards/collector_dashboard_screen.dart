import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:garbigo_frontend/core/utils/helpers.dart';
import 'package:garbigo_frontend/features/auth/providers/auth_provider.dart';
import 'package:garbigo_frontend/features/location/providers/live_location_provider.dart';
import 'package:go_router/go_router.dart';

class CollectorDashboardScreen extends ConsumerWidget {
  const CollectorDashboardScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final locationState = ref.watch(liveLocationProvider);
    final authNotifier = ref.read(authProvider.notifier);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Collector Dashboard'),
        actions: [
          IconButton(
            icon: Icon(
              Icons.location_on,
              color: locationState.isTracking ? Colors.green : Colors.grey,
            ),
            onPressed: () {
              if (locationState.isTracking) {
                Helpers.showToast('Live tracking active');
              } else {
                ref.read(liveLocationProvider.notifier).requestPermissionAndStart();
              }
            },
          ),
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () => Helpers.showLogoutDialog(context, () {
              ref.read(liveLocationProvider.notifier).stopTracking();
              authNotifier.logout(context);
            }),
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Live Location Tracking', style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
            const SizedBox(height: 16),
            if (!locationState.permissionGranted)
              ElevatedButton(
                onPressed: () => ref.read(liveLocationProvider.notifier).requestPermissionAndStart(),
                child: const Text('Enable Live Tracking'),
              ),
            if (locationState.permissionGranted && locationState.currentPosition != null)
              Card(
                color: Colors.green.shade50,
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text('Current Position', style: TextStyle(fontWeight: FontWeight.bold)),
                      Text('Lat: ${locationState.currentPosition!.latitude.toStringAsFixed(6)}'),
                      Text('Lng: ${locationState.currentPosition!.longitude.toStringAsFixed(6)}'),
                      Text('Accuracy: ±${locationState.currentPosition!.accuracy.toStringAsFixed(1)}m'),
                      const SizedBox(height: 8),
                      Text(
                        'Status: Tracking active',
                        style: TextStyle(color: Colors.green[700], fontWeight: FontWeight.bold),
                      ),
                    ],
                  ),
                ),
              ),
            if (locationState.error != null)
              Text('Error: ${locationState.error}', style: const TextStyle(color: Colors.red)),
            const SizedBox(height: 32),
            const Text('Today\'s Route', style: TextStyle(fontSize: 20)),
            const Expanded(child: Center(child: Text('Map & route coming soon...'))),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => context.go('/profile'),
        child: const Icon(Icons.person),
      ),
    );
  }
}