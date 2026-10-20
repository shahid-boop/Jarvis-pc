# JARVIS PC Companion Server (Windows Automation Agent)

This companion agent enables JARVIS AI on your mobile device or desktop emulator to control and operate your Windows PC directly over your local Wi-Fi or LAN.

## Quick Setup on Windows PC

1. **Install Python 3.10+**: Ensure Python and pip are installed.
2. **Install Dependencies**:
   ```bash
   pip install -r requirements.txt
   ```
3. **Run the Server**:
   ```bash
   python jarvis_pc_server.py
   ```
4. **Connect from the JARVIS Android App**:
   - Open **Settings** or **PC Operator** in JARVIS.
   - Enter your PC's local IP address (e.g. `192.168.1.105:8765`).
   - All voice and PC automation commands (e.g. *"Jarvis, Chrome kholo"*, *"Lock workstation"*, *"Downloads folder mein meri PDF dhoondo"*) will execute directly on your PC!

## Safety & Permissions
- Destructive commands (e.g. Workstation lock, terminating system processes) require explicit user confirmation.
- Sandboxed execution protects your files and privacy.
