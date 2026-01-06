import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:garbigo_frontend/core/utils/helpers.dart';
import 'package:garbigo_frontend/features/auth/providers/auth_provider.dart';
import 'package:garbigo_frontend/features/location/providers/live_location_provider.dart';

class CollectorDashboardScreen extends ConsumerWidget {
  const CollectorDashboardScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final locationState = ref.watch(liveLocationProvider);
    final authNotifier = ref.read(authProvider.notifier);

    // Auto-start tracking
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (!locationState.permissionGranted) {
        ref.read(liveLocationProvider.notifier).requestPermissionAndStart();
      }
    });

    return Scaffold(
      appBar: AppBar(
        title: const Text('Collector Dashboard'),
        actions: [
          IconButton(
            icon: Icon(Icons.location_on, color: locationState.isTracking ? Colors.green : Colors.grey),
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
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Live Location Status', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
            const SizedBox(height: 16),
            if (!locationState.permissionGranted)
              Card(
                color: Colors.orange.shade50,
                child: ListTile(
                  leading: const Icon(Icons.location_off, color: Colors.orange),
                  title: const Text('Location Permission Required'),
                  subtitle: const Text('Tap the location icon to enable live tracking'),
                  trailing: ElevatedButton(
                    onPressed: () => ref.read(liveLocationProvider.notifier).requestPermissionAndStart(),
                    child: const Text('Enable'),
                  ),
                ),
              )
            else if (locationState.currentPosition != null)
              Card(
                color: Colors.green.shade50,
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Icon(Icons.location_on, color: Colors.green[700]),
                          const SizedBox(width: 8),
                          Text('Live Tracking Active', style: TextStyle(color: Colors.green[700], fontWeight: FontWeight.bold)),
                        ],
                      ),
                      const SizedBox(height: 8),
                      Text('Lat: ${locationState.currentPosition!.latitude.toStringAsFixed(6)}'),
                      Text('Lng: ${locationState.currentPosition!.longitude.toStringAsFixed(6)}'),
                      Text('Accuracy: ±${locationState.currentPosition!.accuracy.toStringAsFixed(1)}m'),
                    ],
                  ),
                ),
              ),
            const SizedBox(height: 32),
            const Text('Today\'s Route', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
            const SizedBox(height: 16),
            Expanded(
              child: Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Icon(Icons.map, size: 80, color: Colors.grey),
                    const SizedBox(height: 16),
                    const Text('Route map will appear here'),
                    ElevatedButton(
                      onPressed: () {
                        // Open full map view
                      },
                      child: const Text('View Full Route'),
                    ),
                  ],
                ),
              ),
            ),
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