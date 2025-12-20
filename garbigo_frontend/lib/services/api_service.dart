import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

class ApiService {
  static final ApiService _instance = ApiService._internal();
  factory ApiService() => _instance;
  ApiService._internal();

  Future<String?> _getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('jwt_token');
  }

  Future<Map<String, String>> _headers({bool withToken = true}) async {
    final Map<String, String> headers = {
      'Content-Type': 'application/json',
    };
    if (withToken) {
      final token = await _getToken();
      if (token != null) {
        headers['Authorization'] = 'Bearer $token';
      }
    }
    return headers;
  }

  Future<http.Response> get(String url) async {
    final headers = await _headers();
    return await http.get(Uri.parse(url), headers: headers);
  }

  Future<http.Response> post(String url, Map<String, dynamic> body) async {
    final headers = await _headers();
    return await http.post(Uri.parse(url), headers: headers, body: jsonEncode(body));
  }

  Future<http.Response> patch(String url, Map<String, dynamic> body) async {
    final headers = await _headers();
    return await http.patch(Uri.parse(url), headers: headers, body: jsonEncode(body));
  }

  Future<http.Response> delete(String url) async {
    final headers = await _headers();
    return await http.delete(Uri.parse(url), headers: headers);
  }

  // For multipart (profile picture)
  Future<http.Response> multipartPost(String url, Map<String, String> fields, {List<http.MultipartFile>? files}) async {
    final token = await _getToken();
    final request = http.MultipartRequest('POST', Uri.parse(url));
    if (token != null) {
      request.headers['Authorization'] = 'Bearer $token';
    }
    request.fields.addAll(fields);
    if (files != null) request.files.addAll(files);
    final streamedResponse = await request.send();
    return await http.Response.fromStream(streamedResponse);
  }
}