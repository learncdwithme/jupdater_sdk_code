# Security Policy

## Reporting Security Issues

If you discover a security vulnerability in JUpdater, please report it privately.

Do not publicly disclose security issues before they are fixed.

Include:

- Description of the vulnerability
- Steps to reproduce
- Possible impact
- Suggested fix (if available)

## Supported Versions

| Version        | Supported |
|----------------|-----------|
| 2.x            | Yes       |
| 1.x            | Limited   |
| Older versions | No        |

## Security Notes

- JUpdater does not store sensitive application data. It keeps a random installation ID and the last update-server answer in the app's private SharedPreferences.
- In **managed mode** the SDK sends device and app information to the managed server. See [PRIVACY.md](PRIVACY.md) for the exact fields and your obligations under GDPR / ePrivacy. In **custom mode** only the package name and version code are sent.
- Update URLs returned by the server must be `http(s)`; anything else is ignored.

Developers are responsible for:

- Protecting API keys
- Securing their backend endpoints
- Validating update responses
- Using HTTPS endpoints
- Disclosing the SDK's data processing in their privacy policy
