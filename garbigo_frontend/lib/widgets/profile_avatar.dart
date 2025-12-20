import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';

class ProfileAvatar extends StatelessWidget {
  final String? imageUrl;
  final double radius;

  const ProfileAvatar({super.key, this.imageUrl, this.radius = 60});

  @override
  Widget build(BuildContext context) {
    return CircleAvatar(
      radius: radius,
      backgroundColor: Colors.grey[300],
      backgroundImage: imageUrl != null && imageUrl!.isNotEmpty
          ? CachedNetworkImageProvider(imageUrl!)
          : null,
      child: imageUrl == null || imageUrl!.isEmpty
          ? Icon(Icons.person, size: radius)
          : null,
    );
  }
}