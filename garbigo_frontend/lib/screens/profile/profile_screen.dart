import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:image_picker/image_picker.dart';
import 'package:http/http.dart' as http;
import 'package:dio/dio.dart' as dio;
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../providers/auth_provider.dart';
import '../../models/user_model.dart';
import '../../widgets/profile_avatar.dart';
import '../../widgets/custom_textfield.dart';
import '../../widgets/custom_button.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class ProfileScreen extends ConsumerStatefulWidget {
  const ProfileScreen({super.key});

  @override
  ConsumerState<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends ConsumerState<ProfileScreen> {
  late TextEditingController _firstNameController;
  late TextEditingController _middleNameController;
  late TextEditingController _lastNameController;
  late TextEditingController _phoneController;
  XFile? _pickedImage;
  bool _isLoading = false;

  Future<String?> _uploadToCloudinary(XFile image) async {
    final cloudName = dotenv.env['CLOUDINARY_CLOUD_NAME'];
    final uploadPreset = dotenv.env['CLOUDINARY_UPLOAD_PRESET'];

    if (cloudName == null || uploadPreset == null) {
      throw Exception('Cloudinary config missing');
    }

    final formData = dio.FormData.fromMap({
      'file': await dio.MultipartFile.fromFile(image.path),
      'upload_preset': uploadPreset,
    });

    final response = await dio.Dio().post(
      'https://api.cloudinary.com/v1_1/$cloudName/image/upload',
      data: formData,
    );

    if (response.statusCode == 200) {
      return response.data['secure_url'];
    }
    return null;
  }

  Future<List<http.MultipartFile>?> _prepareMultipart() async {
    if (_pickedImage == null) return null;
    final byteData = await _pickedImage!.readAsBytes();
    return [http.MultipartFile.fromBytes('profilePicture', byteData, filename: _pickedImage!.name)];
  }

  Future<void> _updateProfile() async {
    setState(() => _isLoading = true);
    try {
      final multipartFiles = await _prepareMultipart();

      // Note: AuthService.updateProfile is already in services
      final updatedUser = await ref.read(authServiceProvider).updateProfile(
        firstName: _firstNameController.text.trim().isEmpty ? null : _firstNameController.text.trim(),
        middleName: _middleNameController.text.trim().isEmpty ? null : _middleNameController.text.trim(),
        lastName: _lastNameController.text.trim().isEmpty ? null : _lastNameController.text.trim(),
        phoneNumber: _phoneController.text.trim().isEmpty ? null : _phoneController.text.trim(),
        profilePicture: multipartFiles,
      );

      // Update auth state
      final prefs = await SharedPreferences.getInstance();
      final currentToken = prefs.getString('jwt_token');
      final updatedJson = updatedUser.toJson()..['token'] = currentToken;
      await prefs.setString('user_data', jsonEncode(updatedJson));

      ref.read(authProvider.notifier).state = AsyncValue.data(updatedUser);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Profile updated successfully')));
        context.pop();
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
      }
    }
    setState(() => _isLoading = false);
  }

  @override
  void initState() {
    super.initState();
    final user = ref.read(authProvider).value;
    _firstNameController = TextEditingController(text: user?.firstName ?? '');
    _middleNameController = TextEditingController(text: user?.middleName ?? '');
    _lastNameController = TextEditingController(text: user?.lastName ?? '');
    _phoneController = TextEditingController(text: user?.phoneNumber ?? '');
  }

  @override
  void dispose() {
    _firstNameController.dispose();
    _middleNameController.dispose();
    _lastNameController.dispose();
    _phoneController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final user = ref.watch(authProvider).value;

    return Scaffold(
      appBar: AppBar(title: const Text('Edit Profile')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          children: [
            GestureDetector(
              onTap: () async {
                final picker = ImagePicker();
                final image = await picker.pickImage(source: ImageSource.gallery);
                if (image != null) {
                  setState(() => _pickedImage = image);
                }
              },
              child: Stack(
                children: [
                  ProfileAvatar(imageUrl: _pickedImage != null ? _pickedImage!.path : user?.profilePictureUrl, radius: 80),
                  if (_pickedImage != null)
                    Positioned(
                      bottom: 0,
                      right: 0,
                      child: CircleAvatar(
                        backgroundColor: Colors.green,
                        radius: 20,
                        child: const Icon(Icons.check, color: Colors.white),
                      ),
                    ),
                  Positioned(
                    bottom: 0,
                    right: 0,
                    child: CircleAvatar(
                      backgroundColor: Theme.of(context).primaryColor,
                      radius: 20,
                      child: const Icon(Icons.camera_alt, color: Colors.white),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 32),
            CustomTextField(label: 'First Name', controller: _firstNameController),
            const SizedBox(height: 16),
            CustomTextField(label: 'Middle Name', controller: _middleNameController),
            const SizedBox(height: 16),
            CustomTextField(label: 'Last Name', controller: _lastNameController),
            const SizedBox(height: 16),
            CustomTextField(label: 'Phone Number', controller: _phoneController),
            const SizedBox(height: 32),
            SizedBox(
              width: double.infinity,
              child: CustomButton(
                text: 'Save Changes',
                onPressed: _updateProfile,
                isLoading: _isLoading,
              ),
            ),
          ],
        ),
      ),
    );
  }
}