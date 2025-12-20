import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:image_picker/image_picker.dart';
import 'package:http/http.dart' as http;
import 'package:dio/dio.dart' as dio;
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../providers/auth_provider.dart';
import '../../models/user_model.dart';
import '../../services/api_service.dart';
import '../../services/location_service.dart';
import '../../widgets/custom_textfield.dart';
import '../../widgets/custom_button.dart';
import '../../widgets/profile_avatar.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

import 'map_picker_screen.dart';

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
  late TextEditingController _addressLine1Controller;
  late TextEditingController _addressLine2Controller;
  late TextEditingController _cityController;
  late TextEditingController _stateController;
  late TextEditingController _postalCodeController;
  late TextEditingController _countryController;

  XFile? _pickedImage;
  LatLng? _selectedLocation;
  bool _isLoading = false;
  String? _errorMessage;

  final LocationService _locationService = LocationService();

  Future<void> _pickLocationOnMap() async {
    final position = await _locationService.getCurrentPosition();
    LatLng initialPosition = position != null
        ? LatLng(position.latitude, position.longitude)
        : const LatLng(-1.2921, 36.8219); // Default Nairobi

    final result = await Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => MapPickerScreen(initialPosition: initialPosition),
      ),
    );

    if (result is LatLng) {
      setState(() {
        _selectedLocation = result;
      });
    }
  }

  Future<String?> _uploadToCloudinary(XFile image) async {
    final cloudName = dotenv.env['CLOUDINARY_CLOUD_NAME'];
    final uploadPreset = dotenv.env['CLOUDINARY_UPLOAD_PRESET'];

    if (cloudName == null || uploadPreset == null) {
      throw Exception('Cloudinary configuration missing');
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
    final bytes = await _pickedImage!.readAsBytes();
    return [
      http.MultipartFile.fromBytes(
        'profilePicture',
        bytes,
        filename: _pickedImage!.name,
      )
    ];
  }

  Future<void> _updateProfile() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final multipartFiles = await _prepareMultipart();

      final fields = <String, String>{};

      if (_firstNameController.text.trim().isNotEmpty) fields['firstName'] = _firstNameController.text.trim();
      if (_middleNameController.text.trim().isNotEmpty) fields['middleName'] = _middleNameController.text.trim();
      if (_lastNameController.text.trim().isNotEmpty) fields['lastName'] = _lastNameController.text.trim();
      if (_phoneController.text.trim().isNotEmpty) fields['phoneNumber'] = _phoneController.text.trim();

      if (_addressLine1Controller.text.trim().isNotEmpty) fields['addressLine1'] = _addressLine1Controller.text.trim();
      if (_addressLine2Controller.text.trim().isNotEmpty) fields['addressLine2'] = _addressLine2Controller.text.trim();
      if (_cityController.text.trim().isNotEmpty) fields['city'] = _cityController.text.trim();
      if (_stateController.text.trim().isNotEmpty) fields['stateOrProvince'] = _stateController.text.trim();
      if (_postalCodeController.text.trim().isNotEmpty) fields['postalCode'] = _postalCodeController.text.trim();
      if (_countryController.text.trim().isNotEmpty) fields['country'] = _countryController.text.trim();

      // Add location if selected
      if (_selectedLocation != null) {
        fields['latitude'] = _selectedLocation!.latitude.toString();
        fields['longitude'] = _selectedLocation!.longitude.toString();
      }

      final api = ApiService();
      final response = await api.multipartPost(
        '/auth/profile',
        fields,
        files: multipartFiles,
      );

      final updatedUserJson = jsonDecode(response.body);
      final updatedUser = UserModel.fromJson(updatedUserJson);

      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('jwt_token');
      await prefs.setString('user_data', jsonEncode({'token': token, ...updatedUserJson}));

      ref.read(authProvider.notifier).state = AsyncValue.data(updatedUser);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Profile updated successfully!')),
        );
        context.pop();
      }
    } catch (e) {
      setState(() {
        _errorMessage = e.toString().replaceFirst('Exception: ', '');
      });
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  void initState() {
    super.initState();
    final user = ref.read(authProvider).value;

    _firstNameController = TextEditingController(text: user?.firstName ?? '');
    _middleNameController = TextEditingController(text: user?.middleName ?? '');
    _lastNameController = TextEditingController(text: user?.lastName ?? '');
    _phoneController = TextEditingController(text: user?.phoneNumber ?? '');
    _addressLine1Controller = TextEditingController(text: user?.addressLine1 ?? '');
    _addressLine2Controller = TextEditingController(text: user?.addressLine2 ?? '');
    _cityController = TextEditingController(text: user?.city ?? '');
    _stateController = TextEditingController(text: user?.stateOrProvince ?? '');
    _postalCodeController = TextEditingController(text: user?.postalCode ?? '');
    _countryController = TextEditingController(text: user?.country ?? 'Kenya');

    if (user?.hasLocation == true) {
      _selectedLocation = LatLng(user!.latitude!, user.longitude!);
    }
  }

  @override
  void dispose() {
    _firstNameController.dispose();
    _middleNameController.dispose();
    _lastNameController.dispose();
    _phoneController.dispose();
    _addressLine1Controller.dispose();
    _addressLine2Controller.dispose();
    _cityController.dispose();
    _stateController.dispose();
    _postalCodeController.dispose();
    _countryController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final user = ref.watch(authProvider).value;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Edit Profile'),
        backgroundColor: const Color(0xFF22C55E),
        foregroundColor: Colors.white,
      ),
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
                alignment: Alignment.bottomRight,
                children: [
                  ProfileAvatar(
                    imageUrl: _pickedImage?.path ?? user?.profilePictureUrl,
                    radius: 80,
                  ),
                  const CircleAvatar(
                    radius: 20,
                    backgroundColor: Color(0xFF22C55E),
                    child: Icon(Icons.camera_alt, color: Colors.white, size: 20),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 40),

            const Align(
              alignment: Alignment.centerLeft,
              child: Text('Personal Information', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
            ),
            const SizedBox(height: 16),
            CustomTextField(label: 'First Name', controller: _firstNameController),
            const SizedBox(height: 16),
            CustomTextField(label: 'Middle Name', controller: _middleNameController),
            const SizedBox(height: 16),
            CustomTextField(label: 'Last Name', controller: _lastNameController),
            const SizedBox(height: 16),
            CustomTextField(label: 'Phone Number', controller: _phoneController),
            const SizedBox(height: 40),

            const Align(
              alignment: Alignment.centerLeft,
              child: Text('Address', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
            ),
            const SizedBox(height: 16),
            CustomTextField(label: 'Address Line 1', controller: _addressLine1Controller),
            const SizedBox(height: 16),
            CustomTextField(label: 'Address Line 2 (Optional)', controller: _addressLine2Controller),
            const SizedBox(height: 16),
            CustomTextField(label: 'City', controller: _cityController),
            const SizedBox(height: 16),
            CustomTextField(label: 'State/Province', controller: _stateController),
            const SizedBox(height: 16),
            CustomTextField(label: 'Postal Code', controller: _postalCodeController),
            const SizedBox(height: 16),
            CustomTextField(label: 'Country', controller: _countryController),
            const SizedBox(height: 40),

            // Map Picker Button
            ElevatedButton.icon(
              onPressed: _pickLocationOnMap,
              icon: const Icon(Icons.location_on),
              label: Text(_selectedLocation == null ? 'Set Location on Map' : 'Change Location on Map'),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF22C55E),
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 16),
                minimumSize: const Size(double.infinity, 56),
              ),
            ),
            if (_selectedLocation != null)
              Padding(
                padding: const EdgeInsets.only(top: 8),
                child: Text(
                  'Location set: ${_selectedLocation!.latitude.toStringAsFixed(6)}, ${_selectedLocation!.longitude.toStringAsFixed(6)}',
                  style: const TextStyle(color: Colors.green),
                ),
              ),
            const SizedBox(height: 40),

            if (_errorMessage != null)
              Padding(
                padding: const EdgeInsets.only(bottom: 16),
                child: Text(_errorMessage!, style: const TextStyle(color: Colors.red)),
              ),

            CustomButton(
              text: 'Save Changes',
              onPressed: _updateProfile,
              isLoading: _isLoading,
            ),
          ],
        ),
      ),
    );
  }
}